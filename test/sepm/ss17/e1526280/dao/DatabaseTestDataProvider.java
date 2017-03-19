package sepm.ss17.e1526280.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.util.DatabaseService;
import sepm.ss17.e1526280.util.GlobalSettings;

import java.io.IOException;
import java.sql.SQLException;

/**
 * A little helper class which does define some stuff for the Database
 *
 * @author Raphael Ludwig
 * @version 19.03.17
 */
public interface DatabaseTestDataProvider<DAO extends DatabaseDAO> {

    String CONFIG_FILE = "./config.junit.properties";
    String DROP_DATA_FILE = "sql/drop.sql";

    @Before
    default void isolateTest() throws SQLException {
        getDAO().getConnection().setAutoCommit(false);
    }

    @After
    default void removeDataAfterTest() throws SQLException {
       getDAO().getConnection().rollback();
    }

    @BeforeClass
    static void setUp() throws CheckedDatabaseException {
        GlobalSettings.initialize(CONFIG_FILE);
        DatabaseService.initialize();
    }

    @AfterClass
    static void turnDown() {
        try {
            DatabaseService.getManager().executeSQLFile(DROP_DATA_FILE);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        DatabaseService.destroyService();
        try {
            DatabaseService.deleteDatabaseFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    DAO getDAO();
}
