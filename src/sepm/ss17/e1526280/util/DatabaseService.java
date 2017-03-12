package sepm.ss17.e1526280.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.DatabaseManager;
import sepm.ss17.e1526280.dao.Destroyable;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Database Service is a Service which can only be stared once and does initialize the
 * DatabaseManager withe the Setting of the GlobalSettings Class.
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class DatabaseService {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseService.class);

    /** List of all DatabaseDAOs which have to bea cleaned up after **/
    private static final List<Destroyable> destroyListeners = new ArrayList<>();

    /** The actual Database Manager **/
    private static DatabaseManager databaseManager;

    /**
     * This Method does initialize the Service and configure the Database Manager according to
     * the Settings provided by the GlobalSettings class.
     *
     * @throws CheckedDatabaseException if the Database has a problem this Exception is thrown
     */
    public static void initialize() throws CheckedDatabaseException {
        LOG.debug("initialize was called");

        if( databaseManager != null ) {
            LOG.warn("Service was already initialized, skipping it ... !");
            return;
        }

        databaseManager = new DatabaseManager(GlobalSettings.getConfig());

        if( !databaseManager.wasInitialized) {
            LOG.info("Database was not present, loading the SetupFile from the config ...");

            try {
                databaseManager.executeSQLFile(GlobalSettings.getConfig().getProperty("database.setupfile"));
            } catch (IOException | SQLException e) {
                throw new CheckedDatabaseException((new RuntimeException("Could not Setup Database!", e)));
            }
        }
    }

    /**
     * This function exposes the Database Manager to other Classes to operate on it
     * @return a DatabaseManager object or null if initialized was not called
     */
    public static DatabaseManager getManager() {
        return databaseManager;
    }

    /**
     * @return a List of Destroyable Object which are called when the Service gets destroyed
     */
    public static List<Destroyable> getDestroyListeners() {
        return destroyListeners;
    }

    /**
     * Destroys the Service and notifies all the Listeners, this means that the DatabaseManager
     * will also be destroyed and connections will be closed
     */
    public static void destroyService() {
        LOG.debug("Destroy Service ...");

        //First notify the listeners, so if the have anything to cleanup this is the right time
        destroyListeners.forEach(Destroyable::destroy);
        destroyListeners.clear();

        //Now destroy the manager and kill the connection to the DB
        if( databaseManager != null)
            databaseManager.destroy();
        databaseManager = null;
    }

    /**
     * This Method does delete the Database Files in a very unsage way and might corrupt a running Database
     * @throws IOException Is thrown if there was some Problem while cleaning up
     */
    public static void deleteDatabaseFiles() throws IOException {
        LOG.info("Deleting Database files");

        if( databaseManager != null )
            LOG.warn("Deleting but Database-Files manager was initialized ... this might crash the Program!");

        //Delete all Files in the Database location
        Files.list(Paths.get(GlobalSettings.getConfig().getProperty("database.location"))).forEach(x -> {
            try {
                Files.delete(x);
            } catch (IOException ignored) { }
        });

        //Delete the Folder
        new File(GlobalSettings.getConfig().getProperty("database.location")).delete();

        //Delete all the .db files which are created outside of the Folder
        Files.deleteIfExists(Paths.get(GlobalSettings.getConfig().getProperty("database.location")+".mv.db"));
        Files.deleteIfExists(Paths.get(GlobalSettings.getConfig().getProperty("database.location")+"trace.db"));
    }

    /**
     * Loads the Setup File for the Database which is specified in the GlobalSettings under "database.setupfile"
     * @throws IOException is thrown if the File could not be read
     * @throws SQLException is thrown if the SQL File could not be processed
     */
    public static void loadSetupSQL() throws IOException, SQLException {
        LOG.info("Loading Setup-SQL File");
        databaseManager.executeSQLFile(GlobalSettings.getConfig().getProperty("database.setupfile"));
    }
}
