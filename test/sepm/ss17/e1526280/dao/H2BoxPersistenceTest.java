package sepm.ss17.e1526280.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.dao.h2.H2BoxDatabaseDAO;
import sepm.ss17.e1526280.util.datasource.DatabaseSource;

import java.sql.SQLException;

/**
 * This class initialises the AbstractBoxPersistence test with the
 * Database backend (H2)
 *
 * @author Raphael Ludwig
 * @version 18.03.17
 */
public class H2BoxPersistenceTest extends AbstractBoxPersistenceTest implements DatabaseTestDataProvider<H2BoxDatabaseDAO> {

    public H2BoxPersistenceTest() throws CheckedDatabaseException {
        super(new DatabaseSource().getBoxDAO());
    }

    @Before
    public void isolateTest() throws SQLException {
        getDAO().getConnection().setAutoCommit(false);
    }

    @After
    public void removeDataAfterTest() throws SQLException {
        getDAO().getConnection().rollback();
    }

    @BeforeClass
    public static void setUp() throws CheckedDatabaseException {
        DatabaseTestDataProvider.setUp();
    }

    @AfterClass
    public static void turnDown() {
      DatabaseTestDataProvider.turnDown();
    }

    @Override
    public H2BoxDatabaseDAO getDAO() {
        return (H2BoxDatabaseDAO) super.dao;
    }
}
