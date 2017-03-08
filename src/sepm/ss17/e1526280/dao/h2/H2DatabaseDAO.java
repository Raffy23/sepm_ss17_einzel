package sepm.ss17.e1526280.dao.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.DatabaseDAO;
import sepm.ss17.e1526280.dao.DatabaseManager;
import sepm.ss17.e1526280.dao.exceptions.DatabaseException;
import sepm.ss17.e1526280.service.DatabaseService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This abstract class is the Base for all H2 DAO classes, it does currently only
 * check the existence of the table and provide the connection to all it's subclasses
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public abstract class H2DatabaseDAO<T> implements DatabaseDAO<T> {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(H2DatabaseDAO.class);

    /** Store the manager for be able to access the connection at any time **/
    private final DatabaseManager manager;

    /**
     * Creates a new Instance of the H2DatabaseDAO from the DatabaseManager with the tablename.
     * A automatic check is performed of the Table exists and if doesn't than the load SQL script
     * is request from the DatabaseManager
     * @param manager the manager which does handle the database connection
     * @param tablename the table on which the DAO does operate
     */
    public H2DatabaseDAO(DatabaseManager manager, String tablename) {
        this.manager = manager;

        if( !manager.probeForTable(tablename) ) {
            LOG.warn("Table '" +tablename+ "' is missing, requesting create script from database" );

            try {
                DatabaseService.loadSetupSQL();
            } catch (IOException | SQLException e) {
                throw new DatabaseException(e);
            }
        }
    }

    /**
     * @return The connection from the Database
     */
    @Override
    public Connection getConnection() {
        return manager.getConnection();
    }

}
