package sepm.ss17.e1526280.dao.h2;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.DatabaseException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.service.exception.DataException;
import sepm.ss17.e1526280.util.DatabaseService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 05.03.17
 */
public class H2ReservationDatabaseDAO extends H2DatabaseDAO<Reservation> implements ReservationPersistenceDAO {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(H2ReservationDatabaseDAO.class);

    private final BoxPersistenceDAO boxDAO = new H2BoxDatabaseDAO();

    private final PreparedStatement queryID;
    private final PreparedStatement insert;
    private final PreparedStatement delete;
    private final PreparedStatement update;
    private final PreparedStatement reservedCount;
    private final PreparedStatement reserved;

    public H2ReservationDatabaseDAO() {
        super(DatabaseService.getManager(),"Reservation");
        LOG.trace("Constructor");

        final Connection connection = getConnection();
        try {
            insert = connection.prepareStatement("INSERT INTO RESERVATION (RESERVATIONID,BOXID,START,END,CUSTOMER,HORSE,PRICE,ALREADYINVOICE) VALUES (?,?,?,?,?,?,?,?)");
            delete = connection.prepareStatement("DELETE FROM RESERVATION WHERE RESERVATIONID=? AND BOXID=?");
            queryID = connection.prepareStatement("SELECT MAX(RESERVATIONID) FROM RESERVATION");
            update = connection.prepareStatement("UPDATE RESERVATION SET ALREADYINVOICE=?, START=?, END=?, CUSTOMER=?, HORSE=? WHERE RESERVATIONID=?");
            reservedCount = connection.prepareStatement("SELECT COUNT(*) FROM RESERVATION WHERE boxid = ? AND ((? < start AND ? > end) OR (? BETWEEN start and end OR ? BETWEEN start and end))");
            reserved = connection.prepareStatement("SELECT RESERVATIONID,BOXID,START,END,CUSTOMER,HORSE,PRICE,ALREADYINVOICE FROM RESERVATION WHERE BOXID=? AND ((? < start AND ? > end) OR (? BETWEEN start and end OR ? BETWEEN start and end))");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        DatabaseService.getDestroyListeners().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("Duplicates")
    public synchronized void destroy() {
        LOG.trace("Destroy");
        try {
            insert.close();
            delete.close();
            queryID.close();
            update.close();
            reservedCount.close();
            reserved.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Reservation> query(@NotNull Map<String, Object> param) {
        final List<Reservation> data = new ArrayList<>();
        final StringBuilder rawStatement = new StringBuilder();
        final Map<String,Integer> order = new HashMap<>();

        //Build SQL-String for the prepared Statement
        rawStatement.append(generateQueryStatement("RESERVATIONID,BOXID,START,END,CUSTOMER,HORSE,PRICE,ALREADYINVOICE",param, order));

        try {
            final PreparedStatement s = getConnection().prepareStatement(rawStatement.toString());
            LOG.debug("Query:\t"+rawStatement + " with data " + param);

            //Fill the Data into the Statement
            if( !param.isEmpty() ) {
                if (param.containsKey(QUERY_PARAM_IS_INVOICE)) s.setBoolean(order.get(QUERY_PARAM_IS_INVOICE), (Boolean) param.get(QUERY_PARAM_IS_INVOICE));
                if (param.containsKey(QUERY_PARAM_BOX_ID)) s.setInt(order.get(QUERY_PARAM_BOX_ID), (Integer) param.get(QUERY_PARAM_BOX_ID));
                if (param.containsKey(QUERY_PARAM_ID)) s.setInt(order.get(QUERY_PARAM_ID), (Integer) param.get(QUERY_PARAM_ID));
            }

            //Finally execute Query
            final ResultSet rs = s.executeQuery();
            while( rs.next() ) {
                final Box box = boxDAO.query(rs.getInt(2));
                data.add( resultSetToReservation(rs, box) );
            }

            s.close();
        } catch (SQLException | ObjectDoesNotExistException e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void persist(@NotNull Reservation o) throws ObjectDoesAlreadyExistException {
        LOG.debug("Persist:\t"+o);

        try {

            int queryID = o.getId();
            if( o.getId() < 0 ) {
                final ResultSet rs = this.queryID.executeQuery();
                rs.next();

                queryID = rs.getInt(1) + 1;
            }

            insert.setInt(1,queryID);
            setInsert(o);
            insert.executeUpdate();

            o.setId(queryID);
        } catch (SQLException e) {
            throw new ObjectDoesAlreadyExistException(e);
        }
    }

    /**
     * Sets all Data to the insert Statement except the ID
     * @param o reservation which should be inserted
     * @throws SQLException thrown if something goes wrong
     */
    private synchronized void setInsert(Reservation o) throws SQLException {
        insert.setInt(2,o.getBox().getBoxID());
        insert.setDate(3,new Date(o.getStart().getTime()));
        insert.setDate(4,new Date(o.getEnd().getTime()));
        insert.setString(5,o.getCustomer());
        insert.setString(6,o.getHorse());
        insert.setFloat(7,o.getPrice());
        insert.setBoolean(8,o.isAlreadyInvoice());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void merge(@NotNull Reservation o) throws ObjectDoesNotExistException {
        LOG.debug("Merge\t"+o);

        try {
            fillUpdateStatement(o);

            if( update.executeUpdate() == 0 )
                throw new ObjectDoesNotExistException(new RuntimeException("There is no such Reservation in the Database"));

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void merge(List<Reservation> oo) {
        LOG.debug("Batch-Merge\t" + oo);

        if (oo.isEmpty()) {
            LOG.warn("Can not persist empty List, operation will be aborted!");
            return;
        }

        try {

            for (Reservation o : oo) {
               fillUpdateStatement(o);
                update.addBatch();
            }

            update.executeBatch();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void remove(@NotNull Reservation o) throws ObjectDoesNotExistException {
        LOG.debug("Remove\t" + o);

        try {
            delete.setInt(1,o.getId());
            delete.setInt(2,o.getBoxId());
            if( delete.executeUpdate() != 1) {
                throw new ObjectDoesNotExistException(new RuntimeException("There is no such Reservation in the Databse"));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void persist(@NotNull List<Reservation> o) throws ObjectDoesAlreadyExistException {
        LOG.debug("Persist\t" + o);

        int queryID;
        if( o.isEmpty() ) {
            LOG.warn("Can not persist empty List, operation will be aborted!");
            return;
        }

        try {
            final ResultSet rs = this.queryID.executeQuery();
            rs.next();

            queryID = rs.getInt(1);
            for(Reservation oo:o) {
                insert.setInt(1,queryID++);
                setInsert(oo);

                LOG.trace(insert.toString());
                insert.addBatch();
            }

            insert.executeBatch();

        } catch (SQLException e) {
            throw new ObjectDoesAlreadyExistException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean isBoxReserved(Box box, java.util.Date start, java.util.Date end) {
        LOG.debug("isBoxReserved: "+box+" from " + start +" to " + end);

        try {
            reservedCount.setInt(1,box.getBoxID());
            reservedCount.setDate(2,new Date(start.getTime()));
            reservedCount.setDate(3,new Date(end.getTime()));
            reservedCount.setDate(4,new Date(start.getTime()));
            reservedCount.setDate(5,new Date(end.getTime()));

            final ResultSet rs = reservedCount.executeQuery();
            rs.next();

            int count = rs.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void remove(List<Reservation> o) {
        LOG.debug("Batch-Remove\t" + o);
        if( o.isEmpty() ) {
            LOG.warn("Can not remove empty List, operation will be aborted!");
            return;
        }

        try {
            for(Reservation oo:o){
                delete.setInt(1, oo.getId());
                delete.setInt(2, oo.getBoxId());

                LOG.trace(delete.toString());
                delete.addBatch();
            }

            delete.executeBatch();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Reservation> queryFor(Box box, java.util.Date start, java.util.Date end) {
        final List<Reservation> result = new ArrayList<>();
        LOG.debug("queryFor " + box + " between " + start + " and " + end);

        try {
            reserved.setInt(1,box.getBoxID());
            reserved.setDate(2,new Date(start.getTime()));
            reserved.setDate(3,new Date(end.getTime()));
            reserved.setDate(4,new Date(start.getTime()));
            reserved.setDate(5,new Date(end.getTime()));

            final ResultSet rs = reserved.executeQuery();

            while( rs.next() ) {
                result.add( resultSetToReservation(rs, box) );
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return result;
    }

    private static Reservation resultSetToReservation(ResultSet rs, Box box) throws SQLException {
        LOG.trace("resultSetToReservation");
        return new Reservation(rs.getInt(1)
                , box
                , new java.util.Date(rs.getDate(3).getTime())
                , new java.util.Date(rs.getDate(4).getTime())
                , rs.getString(5)
                , rs.getString(6)
                , rs.getFloat(7)
                , rs.getBoolean(8));
    }

    private void fillUpdateStatement(Reservation o) throws SQLException {
        LOG.trace("fillUpdateStatement");
        update.setBoolean(1, o.isAlreadyInvoice());
        update.setDate(2, new Date(o.getStart().getTime()));
        update.setDate(3, new Date(o.getEnd().getTime()));
        update.setString(4, o.getCustomer());
        update.setString(5, o.getHorse());
        update.setInt(6, o.getId());
    }
}
