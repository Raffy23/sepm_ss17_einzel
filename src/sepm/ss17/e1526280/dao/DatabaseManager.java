package sepm.ss17.e1526280.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.dao.exceptions.DatabaseException;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class is a Generic Database manager which can open a Connection to many Databases if the
 * JDBC Driver was loaded. There are multiple ways to construct this class, one way to call the
 * Constructor with call the values which are needed by the JDBC Driver or with the Configuration
 * of a File.
 *
 * This class provides basic access to a single connection from the Database
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class DatabaseManager implements Destroyable {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

    /** The Connection of the Database **/
    private final Connection connection;

    /** Since we have to store Images and H2 doesn't handle Blobs that well this DAO can be used **/
    private final ImageDAO imageStorage;

    /** A read-only Flag which indicates if the Manager has found a existing Database or not **/
    public final boolean wasInitialized;

    /**
     * With this constructor every field of the JDBC String must be specified
     *
     * @param driver the class-path of the Driver which should be used
     * @param proto the protocol for the Driver
     * @param database the Database location (is also used for the Image Blob Storage)
     * @param user the user which should be used
     * @param password the password which should be used
     * @throws CheckedDatabaseException exception is thrown if something goes wrong
     */
    public DatabaseManager(String driver, String proto, String database, String user, String password) throws CheckedDatabaseException {
        LOG.debug("A new Database Manager was created for the Database: " + database);

        //First check if the location exists
        final File location = new File(database);

        if( !location.exists() ) {
            LOG.debug("Database location does not exist");

            wasInitialized = false;
            if (!location.mkdirs()) {
                LOG.error("Could not create Image Storage for the Database ...");
                throw new DatabaseException(new RuntimeException("Could not create Image Blobstorage!"));
            }
        } else {
            LOG.debug("Database seems to exist");

            wasInitialized = true;
        }

        //Create the Image Storage
        imageStorage = new ImageDAO(database);

        //Load the JDBC Driver & connect to the Database
        try {

            Class.forName(driver);
            connection = DriverManager.getConnection(proto+":"+database,user,password);
            connection.setAutoCommit(true);

        } catch (ClassNotFoundException | SQLException e) {
            LOG.error("Could not load the JDBC Driver or connect to the Database!");

            throw new CheckedDatabaseException(e);
        }
    }

    /**
     * This Constructor does does create the Database from the Properties in the parameter. These
     * Strings must be Set in the Property class otherwise a NullPointerException will occur:
     * - "database.driver"
     * - "database.protocol"
     * - "database.user"
     * - "database.password"
     *
     * @param prop the Properties for the Database
     * @throws CheckedDatabaseException Is thrown if something went wrong while constructing the Database
     */
    public DatabaseManager(Properties prop) throws CheckedDatabaseException {
        this( prop.getProperty("database.driver")
            , prop.getProperty("database.protocol")
            , prop.getProperty("database.location")
            , prop.getProperty("database.user")
            , prop.getProperty("database.password"));
    }

    /**
     * @return The Connection of the Database
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @return The ImageDAO for the Image Blob Storage
     */
    public ImageDAO getImageStorage() {
        return imageStorage;
    }

    /**
     * This function does execute a SQL File for the current connection.
     * Comments must only be single line (--) and must be the first chars otherwise it is interpreted as valid SQL!
     *
     * @param file the path to the file which should be executed
     * @throws IOException is thrown if the file could not be read
     * @throws SQLException os thrown if there was something wrong with the statements
     */
    public void executeSQLFile(String file) throws IOException, SQLException {
        LOG.debug("Execute SQL-File: " + file);
        final StringBuilder sqlBuilder = new StringBuilder();

        //Parse file into a single String and cut out Comments
        Files.readAllLines(Paths.get(file)).forEach(x -> {
            if( !x.trim().startsWith("--") )
                sqlBuilder.append(x.trim());
        });

        //Split the *hopefully* valid SQL Lines again
        final String[] sqlStatements = sqlBuilder.toString().split(";");

        //Execute the SQL Lines
        for(String str:sqlStatements) {
            LOG.debug("\t- Execute Statement: " + str);
            getConnection().createStatement().execute(str);
        }
    }

    /**
     * This function does test if a certain Table does exist or not.
     * This is done by a simple select to the Table
     *
     * @param tablename the name of the table which should be checked
     * @return true if the table exists otherwise false
     */
    public boolean probeForTable(String tablename) {
        try {
            getConnection().createStatement().executeQuery("SELECT * FROM " + tablename + " LIMIT 1");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void destroy() {
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
