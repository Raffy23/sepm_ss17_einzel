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
import sepm.ss17.e1526280.service.DatabaseService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private final PreparedStatement selectByID;

    public H2ReservationDatabaseDAO() {
        super(DatabaseService.getManager(),"Reservation");
        LOG.trace("Constructor");

        final Connection connection = getConnection();
        try {
            insert = connection.prepareStatement("INSERT INTO RESERVATION (RESERVATIONID,BOXID,START,END,CUSTOMER,HORSE,PRICE,ALREADYINVOICE) VALUES (?,?,?,?,?,?,?,?)");
            delete = connection.prepareStatement("DELETE FROM RESERVATION WHERE RESERVATIONID=?");
            queryID = connection.prepareStatement("SELECT MAX(RESERVATIONID) FROM RESERVATION");
            selectByID = connection.prepareStatement("SELECT RESERVATIONID,BOXID,START,END,CUSTOMER,HORSE,PRICE,ALREADYINVOICE FROM RESERVATION WHERE RESERVATIONID = ?");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        DatabaseService.getDestroyListeners().add(this);
    }

    @Override
    public void destroy() {
        LOG.trace("destroy");
        try {
            insert.close();
            delete.close();
            queryID.close();
            selectByID.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reservation> query(Map<String, Object> param) {
        System.err.println("params are ignored!");
        final List<Reservation> data = new ArrayList<>();

        try {
            final Statement statement = getConnection().createStatement();
            final ResultSet rs = statement.executeQuery("SELECT RESERVATIONID,BOXID,START,END,CUSTOMER,HORSE,PRICE,ALREADYINVOICE  FROM RESERVATION");
            LOG.debug("Query:\t"+rs);

            while( rs.next() ) {
                final Box box = boxDAO.query(rs.getInt(2));
                data.add(new Reservation(rs.getInt(1),box,rs.getDate(3),rs.getDate(4),rs.getString(5),rs.getString(6),rs.getFloat(7),rs.getBoolean(8)));
            }

            rs.close();
            statement.close();
        } catch (SQLException | ObjectDoesNotExistException e) {
            e.printStackTrace();
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
        throw new NotImplementedException();
    }

    @Override
    public void remove(Reservation o) {
        throw new NotImplementedException();
    }

    @Override
    public void persist(List<Reservation> o) throws ObjectDoesAlreadyExistException {
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
            e.printStackTrace();
            throw new ObjectDoesAlreadyExistException();
        }

    }
}
