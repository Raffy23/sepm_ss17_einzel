package sepm.ss17.e1526280.util.datasource;

import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;
import sepm.ss17.e1526280.dao.h2.H2BoxDatabaseDAO;
import sepm.ss17.e1526280.dao.h2.H2ReservationDatabaseDAO;
import sepm.ss17.e1526280.util.DatabaseService;

/**
 * A DataSource implementation for the H2 Database DAOs for the
 * DataService Manager
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public class DatabaseSource implements DataSource {

    private final BoxPersistenceDAO boxPersistenceDAO;
    private final ReservationPersistenceDAO reservationPersistenceDAO;
    private final ImageDAO imageDAO;

    /**
     * Creates the DAO instances and connects to the Database
     * with the DatabaseService.
     *
     * @throws CheckedDatabaseException if something goes wrong connecting to the database
     */
    public DatabaseSource() throws CheckedDatabaseException {
        //Only init Database if we must
        if( DatabaseService.getManager() == null)
            DatabaseService.initialize();

        boxPersistenceDAO = new H2BoxDatabaseDAO();
        reservationPersistenceDAO = new H2ReservationDatabaseDAO();
        imageDAO = DatabaseService.getManager().getImageStorage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BoxPersistenceDAO getBoxDAO() {
        return boxPersistenceDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReservationPersistenceDAO getReservationDAO() {
        return reservationPersistenceDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageDAO getImageDAO() {
        return imageDAO;
    }
}
