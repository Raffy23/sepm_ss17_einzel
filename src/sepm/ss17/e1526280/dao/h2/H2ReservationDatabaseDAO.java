package sepm.ss17.e1526280.dao.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.DatabaseException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.service.DataException;
import sepm.ss17.e1526280.util.DatabaseService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public H2ReservationDatabaseDAO() {
        super(DatabaseService.getManager(),"Reservation");
        LOG.trace("Constructor");

        final Connection connection = getConnection();
        try {
            insert = connection.prepareStatement("INSERT INTO RESERVATION (RESERVATIONID,BOXID,START,END,CUSTOMER,HORSE,PRICE,ALREADYINVOICE) VALUES (?,?,?,?,?,?,?,?)");
            delete = connection.prepareStatement("DELETE FROM RESERVATION WHERE RESERVATIONID=? AND BOXID=?");
            queryID = connection.prepareStatement("SELECT MAX(RESERVATIONID) FROM RESERVATION");
            update = connection.prepareStatement("UPDATE RESERVATION SET ALREADYINVOICE=? WHERE RESERVATIONID=?");
            reservedCount = connection.prepareStatement("SELECT COUNT(*) FROM RESERVATION WHERE boxid = ? AND ((? < start AND ? > end) OR (? BETWEEN start and end OR ? BETWEEN start and end))");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        DatabaseService.getDestroyListeners().add(this);
    }

    @Override
    public void destroy() {
        LOG.trace("Destroy");
        try {
            insert.close();
            delete.close();
            queryID.close();
            update.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reservation> query(Map<String, Object> param) {
        final List<Reservation> data = new ArrayList<>();
        final StringBuilder rawStatement = new StringBuilder();
        rawStatement.append("SELECT RESERVATIONID,BOXID,START,END,CUSTOMER,HORSE,PRICE,ALREADYINVOICE FROM Reservation");

        //Build SQL-String for the prepared Statement
        if(!param.isEmpty() ) {
            rawStatement.append(" WHERE ");

            if (param.containsKey(QUERY_PARAM_IS_INVOICE)) rawStatement.append(" alreadyinvoice=?");
            if (param.containsKey(QUERY_PARAM_LIMIT)) rawStatement.append(" LIMIT ").append(param.get(QUERY_PARAM_LIMIT));
        }

        try {
            final PreparedStatement s = getConnection().prepareStatement(rawStatement.toString());
            LOG.debug("Query:\t"+s + " with data " + param);
            int position = 1;

            //Fill the Data into the Statement
            if( !param.isEmpty() ) {
                if (param.containsKey(QUERY_PARAM_IS_INVOICE)) s.setBoolean(position, (Boolean) param.get(QUERY_PARAM_IS_INVOICE));
            }

            //Finally execute Query
            final ResultSet rs = s.executeQuery();
            while( rs.next() ) {
                final Box box = boxDAO.query(rs.getInt(2));
                data.add( new Reservation(rs.getInt(1)
                        , box
                        , new java.util.Date(rs.getDate(3).getTime())
                        , new java.util.Date(rs.getDate(4).getTime())
                        , rs.getString(5)
                        , rs.getString(6)
                        , rs.getFloat(7)
                        , rs.getBoolean(8))
                );
            }

            s.close();
        } catch (SQLException | ObjectDoesNotExistException e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    @Override
    public void persist(Reservation o) throws ObjectDoesAlreadyExistException {
        LOG.trace("Persist:\t"+o);

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

        } catch (SQLException e) {
            throw new ObjectDoesAlreadyExistException();
        }
    }

    private void setInsert(Reservation o) throws SQLException {
        insert.setInt(2,o.getBox().getBoxID());
        insert.setDate(3,new Date(o.getStart().getTime()));
        insert.setDate(4,new Date(o.getEnd().getTime()));
        insert.setString(5,o.getCustomer());
        insert.setString(6,o.getHorse());
        insert.setFloat(7,o.getPrice());
        insert.setBoolean(8,o.isAlreadyInvoice());
    }

    @Override
    public void merge(Reservation o) throws ObjectDoesNotExistException {
        LOG.info("Merge operation is ignore everything execpt the field 'alreadyinvoice'!");

        try {
            update.setBoolean(1,o.isAlreadyInvoice());
            update.setInt(2,o.getId());

            if( update.executeUpdate() == 0 )
                throw new ObjectDoesNotExistException();

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void merge(List<Reservation> o) {
        throw new NotImplementedException();
    }

    @Override
    public void remove(Reservation o) {
        LOG.debug("Remove\t" + o);

        try {
            delete.setInt(1,o.getId());
            delete.setInt(2,o.getBoxId());
            delete.executeUpdate();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public void persist(List<Reservation> o) throws ObjectDoesAlreadyExistException {
        LOG.debug("Persist\t" + o);
        final int queryID;

        try {
            final ResultSet rs = this.queryID.executeQuery();
            rs.next();

            queryID = rs.getInt(1) + 1;


            insert.setInt(1,queryID);
            for(Reservation oo:o) {
                setInsert(oo);
                insert.addBatch();
            }

            System.out.println(insert);
            insert.executeBatch();

        } catch (SQLException e) {
            throw new ObjectDoesAlreadyExistException();
        }

    }

    @Override
    public boolean isBoxReserved(Box box, java.util.Date start, java.util.Date end) {
        //SELECT COUNT(*) FROM table WHERE boxid = box.getID AND box.start >= start AND box.start <= end AND box.end <= end AND box.end >= start

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
            throw new DataException(e);
        }
    }

    @Override
    public void remove(List<Reservation> o) {
        LOG.debug("Batch-Remove\t" + o);

        try {
            for(Reservation oo:o){
                delete.setInt(1, oo.getId());
                delete.setInt(2, oo.getBoxId());
                delete.addBatch();
            }

            if( delete.executeUpdate() != o.size() )
                throw new DataException(new ObjectDoesNotExistException());

        } catch (SQLException e) {
            throw new DataException(e);
        }
    }
}
