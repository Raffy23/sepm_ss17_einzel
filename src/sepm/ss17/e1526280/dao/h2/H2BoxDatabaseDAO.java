package sepm.ss17.e1526280.dao.h2;

import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.DatabaseException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExist;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExist;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.LitterType;
import sepm.ss17.e1526280.service.DatabaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the DAO implementation for the H2 Database which does handle
 * all the Statements for the Box class.
 *
 * @author Raphael Ludwig
 * @version 04.03.17
 */
public class H2BoxDatabaseDAO extends H2DatabaseDAO<Box> implements BoxPersistenceDAO {

    //TODO: Comment Statements
    //Some private statements
    private final PreparedStatement queryID;
    private final PreparedStatement update;
    private final PreparedStatement insert;
    private final PreparedStatement delete;
    private final PreparedStatement selectByID;

    /**
     * Creates a new DAO from the Connection of the DatabaseService
     */
    public H2BoxDatabaseDAO() {
        super(DatabaseService.getManager(),"Box");

        //Prepare all the Statements
        final Connection connection = getConnection();
        try {
            update = connection.prepareStatement("UPDATE Box SET price=?, size=?, litter=?, window=?, indoor=?, photo=?, deleted=? WHERE BOXID = ?");
            insert = connection.prepareStatement("INSERT INTO Box VALUES (?,?,?,?,?,?,?,?)");
            delete = connection.prepareStatement("DELETE FROM Box WHERE BOXID = ?");
            queryID = connection.prepareStatement("SELECT MAX(BOXID) FROM BOX");
            selectByID = connection.prepareStatement("SELECT * FROM BOX WHERE BOXID = ?");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        //Append us to the clean-up crew
        DatabaseService.getDestroyListeners().add(this);
    }

    /**
     * Closes all Resources which are held by this DAO
     */
    @Override
    public void destroy() {
        try {
            update.close();
            insert.close();
            delete.close();
            queryID.close();
            selectByID.close();
        } catch (SQLException e) {
            /* I don't really care if cleanup fails ... */
        }
    }

    /**
     * This function issues a Query which does query all the Elements T from the Datasource
     * which do match the param Objects
     * @param t a Map of Parameters for the Query
     * @return a List of all found Elements
     */
    @Override
    public List<Box> query(Map<String, Object> t) {
        final List<Box> data = new ArrayList<>();
        final StringBuilder rawStatement = new StringBuilder();
        rawStatement.append("SELECT * FROM Box");

        //Build SQL-String for the prepared Statement
        if(!t.isEmpty() ) {
            rawStatement.append(" WHERE ");

            if (t.containsKey(QUERY_PARAM_BOX_ID)) rawStatement.append(" boxid=?");
            if (t.containsKey(QUERY_PARAM_PRICE)) rawStatement.append(" price=?");
            if (t.containsKey(QUERY_PARAM_SIZE)) rawStatement.append(" size=?");
            if (t.containsKey(QUERY_PARAM_LITTER)) rawStatement.append(" litter=?");
            if (t.containsKey(QUERY_PARAM_WINDOW)) rawStatement.append(" window=?");
            if (t.containsKey(QUERY_PARAM_INDOOR)) rawStatement.append(" indoor=?");
            if (t.containsKey(QUERY_PARAM_DELFLAG)) rawStatement.append(" deleted=?");

            if (t.containsKey(QUERY_PARAM_LIMIT)) rawStatement.append(" LIMIT ").append(t.get(QUERY_PARAM_LIMIT));
        }

        try {
            final PreparedStatement s = getConnection().prepareStatement(rawStatement.toString());
            int position = 1;

            //Fill the Data into the Statement
            if( !t.isEmpty() ) {
                if (t.containsKey(QUERY_PARAM_BOX_ID)) s.setInt(position++, (Integer) t.get(QUERY_PARAM_BOX_ID));
                if (t.containsKey(QUERY_PARAM_PRICE)) s.setFloat(position++, (Float) t.get(QUERY_PARAM_PRICE));
                if (t.containsKey(QUERY_PARAM_SIZE)) s.setFloat(position++, (Float) t.get(QUERY_PARAM_SIZE));
                if (t.containsKey(QUERY_PARAM_LITTER)) s.setString(position++, ((LitterType) t.get(QUERY_PARAM_LITTER)).name());
                if (t.containsKey(QUERY_PARAM_WINDOW)) s.setBoolean(position++, (Boolean) t.get(QUERY_PARAM_LITTER));
                if (t.containsKey(QUERY_PARAM_INDOOR)) s.setBoolean(position++, (Boolean) t.get(QUERY_PARAM_INDOOR));
                if (t.containsKey(QUERY_PARAM_DELFLAG)) s.setBoolean(position, (Boolean) t.get(QUERY_PARAM_DELFLAG));
            }

            //Finally execute Query
            final ResultSet result = s.executeQuery();
            while( result.next() ) {
                data.add(new Box(result.getInt(1)
                        , result.getFloat(2)
                        , result.getFloat(3)
                        , LitterType.valueOf(result.getString(4))
                        , result.getBoolean(5)
                        , result.getBoolean(6)
                        , result.getString(7)));
            }

            s.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    /**
     * This function persists a Object in the Datasource, if the Object already exists a Exception is thrown
     * @param object object wich should be persisted
     * @throws ObjectDoesAlreadyExist thrown if the object already exists
     */
    @Override
    public void persist(Box object) throws ObjectDoesAlreadyExist {
        ResultSet result;
        try {
            int currentID = object.getBoxID();

            if( currentID < 0 ) {
                result = queryID.executeQuery();
                result.next(); // Can not fail due max(...)

                currentID = result.getInt(1) + 1;
                object.setBoxID(currentID);
            }

            insert.setInt(1,currentID);
            insert.setFloat(2,object.getPrice());
            insert.setFloat(3,object.getSize());
            insert.setString(4,object.getLitter().name());
            insert.setBoolean(5,object.isWindow());
            insert.setBoolean(6,object.isIndoor());
            insert.setString(7,object.getPhoto());
            insert.setBoolean(8,object.isDeleted());

            insert.executeUpdate();
        } catch (SQLException e) {
            throw new ObjectDoesAlreadyExist();
        }

    }

    /**
     * This function does merge the current Object with the one in the Datasource where the Data in the
     * Datasource if overwritten
     * @param object current object
     * @throws ObjectDoesNotExist thrown if the Object does not exist in the Database
     */
    @Override
    public void merge(Box object) throws ObjectDoesNotExist {
        final List<Box> data = this.query(new HashMap<String,Object>(){
            {this.put(H2BoxDatabaseDAO.QUERY_PARAM_BOX_ID,object.getBoxID());}
        });

        if( data.size() < 1 )
            throw new ObjectDoesNotExist();

        try {
            update.setFloat(1,object.getPrice());
            update.setFloat(2,object.getSize());
            update.setString(3,object.getLitter().name());
            update.setBoolean(4,object.isWindow());
            update.setBoolean(5,object.isIndoor());
            update.setString(6,object.getPhoto());
            update.setInt(8,object.getBoxID());
            update.setBoolean(7,object.isDeleted());

            update.executeUpdate();
        } catch (SQLException e) {
            throw new ObjectDoesNotExist();
        }
    }

    /**
     * This function does remove the Object from the Datasource
     * @param object object which should be removed
     */
    @Override
    public void remove(Box object) {
        try {
            delete.setInt(1, object.getBoxID());
            delete.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
