package sepm.ss17.e1526280.dao.h2;

import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.DatabaseException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
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
 * @version 09.03.17
 */
public class H2BoxDatabaseDAO extends H2DatabaseDAO<Box> implements BoxPersistenceDAO {

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
    public void destroy() {
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
                if (t.containsKey(QUERY_PARAM_WINDOW)) s.setBoolean(position++, (Boolean) t.get(QUERY_PARAM_WINDOW));
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
    public void persist(Box object) throws ObjectDoesAlreadyExistException {
        try {

            //Have to Check Object if ID is set
            if( object.getBoxID() >= 0 ) {

                //Query Object from Database
                selectByID.setInt(1, object.getBoxID());
                final ResultSet result = selectByID.executeQuery();

                //Throw if there is one there
                if( result.next() ) {
                    result.close();
                    throw new ObjectDoesAlreadyExistException();
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
            throw new ObjectDoesAlreadyExistException();
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
    public void merge(Box object) throws ObjectDoesNotExistException {
        final List<Box> data = this.query(new HashMap<String,Object>(){
            {this.put(H2BoxDatabaseDAO.QUERY_PARAM_BOX_ID,object.getBoxID());}
        });

        if( data.size() < 1 )
            throw new ObjectDoesNotExistException();

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
            throw new ObjectDoesNotExistException();
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
