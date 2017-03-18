package sepm.ss17.e1526280.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.dao.h2.H2BoxDatabaseDAO;
import sepm.ss17.e1526280.util.DatabaseService;
import sepm.ss17.e1526280.util.GlobalSettings;
import sepm.ss17.e1526280.util.datasource.DataSource;
import sepm.ss17.e1526280.util.datasource.DatabaseSource;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public class H2BoxPersistenceTest extends AbstractBoxPersistenceTest {

    public H2BoxPersistenceTest() throws CheckedDatabaseException {
        super(setUpDatabaseConnection());
    }

    private static H2BoxDatabaseDAO setUpDatabaseConnection() throws CheckedDatabaseException {
        final DataSource source = new DatabaseSource();
        return (H2BoxDatabaseDAO) source.getBoxDAO();
    }

    @Before
    public void isolateTest() throws SQLException {
        ((H2BoxDatabaseDAO)dao).getConnection().setAutoCommit(false);
    }

    @After
    public void removeDataAfterTest() throws SQLException {
        ((H2BoxDatabaseDAO)dao).getConnection().rollback();
    }

    @BeforeClass
    public static void setUp() throws CheckedDatabaseException {
        GlobalSettings.initialize("./config.junit.properties");
        DatabaseService.initialize();
    }

    @AfterClass
    public static void turnDown() {
        try {
            DatabaseService.getManager().executeSQLFile("sql/drop.sql");
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
}
