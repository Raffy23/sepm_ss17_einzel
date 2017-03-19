package sepm.ss17.e1526280.dao.h2;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.DatabaseException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.LitterType;
import sepm.ss17.e1526280.util.DatabaseService;

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
 * @version 09.03.17
 */
public class H2BoxDatabaseDAO extends H2DatabaseDAO<Box> implements BoxPersistenceDAO {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(H2BoxDatabaseDAO.class);

    //TODO: Comment Statements
    //Some private statements
    private final PreparedStatement queryID;
    private final PreparedStatement update;
    private final PreparedStatement insert;
    private final PreparedStatement insertAbsolute;
    private final PreparedStatement delete;
    private final PreparedStatement selectByID;

    /**
     * Creates a new DAO from the Connection of the DatabaseService
     */
    public H2BoxDatabaseDAO() {
        super(DatabaseService.getManager(),"Box");
        LOG.trace("Constructor");

        //Prepare all the Statements
        final Connection connection = getConnection();
        try {
            update = connection.prepareStatement("UPDATE Box SET price=?, size=?, litter=?, window=?, indoor=?, photo=?, deleted=? WHERE BOXID = ?");
            insert = connection.prepareStatement("INSERT INTO Box (price,size,litter,window,indoor,photo,deleted) VALUES (?,?,?,?,?,?,?)",new String[]{"BOXID"});
            insertAbsolute = connection.prepareStatement("INSERT INTO Box (BOXID,price,size,litter,window,indoor,photo,deleted) VALUES (?,?,?,?,?,?,?,?)");
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
    @SuppressWarnings("Duplicates")
    public synchronized void destroy() {
        LOG.trace("destroy");

        try {
            update.close();
            insert.close();
            delete.close();
            queryID.close();
            selectByID.close();
            insertAbsolute.close();
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
    public synchronized List<Box> query(@NotNull Map<String, Object> t) {
        final List<Box> data = new ArrayList<>();
        final StringBuilder rawStatement = new StringBuilder();
        final Map<String,Integer> order = new HashMap<>();

        //Build SQL-String for the prepared Statement
        rawStatement.append( generateQueryStatement("*",t,order) );
        if (t.containsKey(QUERY_PARAM_LIMIT)) rawStatement.append(" LIMIT ").append(t.get(QUERY_PARAM_LIMIT));

        try {
            final PreparedStatement s = getConnection().prepareStatement(rawStatement.toString());
            LOG.debug("Query:\t"+rawStatement + " with data " + t);

            //Fill the Data into the Statement
            if( !t.isEmpty() ) {

                if (t.containsKey(QUERY_PARAM_BOX_ID)) s.setInt(order.get(QUERY_PARAM_BOX_ID), (Integer) t.get(QUERY_PARAM_BOX_ID));
                if (t.containsKey(QUERY_PARAM_PRICE)) s.setFloat(order.get(QUERY_PARAM_PRICE), (Float) t.get(QUERY_PARAM_PRICE));
                if (t.containsKey(QUERY_PARAM_SIZE)) s.setFloat(order.get(QUERY_PARAM_SIZE), (Float) t.get(QUERY_PARAM_SIZE));
                if (t.containsKey(QUERY_PARAM_LITTER)) s.setString(order.get(QUERY_PARAM_LITTER), ((LitterType) t.get(QUERY_PARAM_LITTER)).name());
                if (t.containsKey(QUERY_PARAM_WINDOW)) s.setBoolean(order.get(QUERY_PARAM_WINDOW), (Boolean) t.get(QUERY_PARAM_WINDOW));
                if (t.containsKey(QUERY_PARAM_INDOOR)) s.setBoolean(order.get(QUERY_PARAM_INDOOR), (Boolean) t.get(QUERY_PARAM_INDOOR));
                if (t.containsKey(QUERY_PARAM_DELFLAG)) s.setBoolean(order.get(QUERY_PARAM_DELFLAG), (Boolean) t.get(QUERY_PARAM_DELFLAG));
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
                        , result.getString(7)
                        , result.getBoolean(8)));
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
     * @throws ObjectDoesAlreadyExistException thrown if the object already exists
     */
    @Override
    public synchronized void persist(@NotNull Box object) throws ObjectDoesAlreadyExistException {
        LOG.trace("Persist:\t"+object);

        try {
            //Have to Check Object if ID is set
            if( object.getBoxID() >= 0 ) {

                //Query Object from Database
                selectByID.setInt(1, object.getBoxID());
                final ResultSet result = selectByID.executeQuery();

                //Throw if there is one there
                if( result.next() ) {
                    result.close();
                    throw new ObjectDoesAlreadyExistException(new RuntimeException("There is already a Box with that ID!"));
                }

                result.close();

                //If no Exception occurred insert it with the ID
                insertAbsolute.setInt(1,object.getBoxID());
                insertWithStatement(insertAbsolute,object,2);
            } else {
                //Insert Object into Database with auto-index
                insertWithStatement(insert,object,1);

                //Query the Data from the Database and set the id
                final ResultSet idResultSet = insert.getGeneratedKeys();
                idResultSet.next();
                object.setBoxID(idResultSet.getInt(1));

                idResultSet.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ObjectDoesAlreadyExistException(e);
        }

    }

    private static void insertWithStatement(PreparedStatement insert, Box object, int p) throws SQLException {
        insert.setFloat(p++,object.getPrice());
        insert.setFloat(p++,object.getSize());
        insert.setString(p++,object.getLitter().name());
        insert.setBoolean(p++,object.isWindow());
        insert.setBoolean(p++,object.isIndoor());
        insert.setString(p++,object.getPhoto());
        insert.setBoolean(p,object.isDeleted());
        insert.executeUpdate();
    }

    /**
     * This function does merge the current Object with the one in the Datasource where the Data in the
     * Datasource if overwritten
     * @param object current object
     * @throws ObjectDoesNotExistException thrown if the Object does not exist in the Database
     */
    @Override
    public synchronized void merge(@NotNull Box object) throws ObjectDoesNotExistException {
        LOG.debug("Merge\t" + object);

        try {
            update.setFloat(1,object.getPrice());
            update.setFloat(2,object.getSize());
            update.setString(3,object.getLitter().name());
            update.setBoolean(4,object.isWindow());
            update.setBoolean(5,object.isIndoor());
            update.setString(6,object.getPhoto());
            update.setBoolean(7,object.isDeleted());
            update.setInt(8,object.getBoxID());

            if( update.executeUpdate() == 0) {
                throw new ObjectDoesNotExistException(new RuntimeException("There are no Boxes that can be updated!"));
            }
        } catch (SQLException e) {
            throw new ObjectDoesNotExistException(e);
        }
    }

    /**
     * This function does remove the Object from the Datasource
     * @param object object which should be removed
     */
    @Override
    public synchronized void remove(@NotNull Box object) throws ObjectDoesNotExistException {
        LOG.debug("Remove\t" + object);

        try {
            delete.setInt(1, object.getBoxID());
            if( delete.executeUpdate() == 0 )
                throw new ObjectDoesNotExistException(new RuntimeException("There is no such Box!"));

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
