package sepm.ss17.e1526280.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dao.h2.H2ReservationDatabaseDAO;
import sepm.ss17.e1526280.util.datasource.DataSource;
import sepm.ss17.e1526280.util.datasource.DatabaseSource;

import java.sql.SQLException;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 19.03.17
 */
public class H2ReservationPersistenceTest extends AbstractReservationPersistenceTest implements DatabaseTestDataProvider<H2ReservationDatabaseDAO> {

    public H2ReservationPersistenceTest() throws CheckedDatabaseException, ObjectDoesAlreadyExistException, ObjectDoesNotExistException {
        super(new DatabaseSource().getReservationDAO(), new DatabaseSource().getBoxDAO());

        // Seed Database with some initial Test data
        final DataSource dataSource = new DatabaseSource();

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
    public H2ReservationDatabaseDAO getDAO() {
        return (H2ReservationDatabaseDAO) super.dao;
    }
}
