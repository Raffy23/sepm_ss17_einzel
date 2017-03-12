package sepm.ss17.e1526280.util.datasource;

import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.CheckedDatabaseException;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;
import sepm.ss17.e1526280.dao.h2.H2BoxDatabaseDAO;
import sepm.ss17.e1526280.dao.h2.H2ReservationDatabaseDAO;
import sepm.ss17.e1526280.util.DatabaseService;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public class DatabaseSource implements DataSource {

    private final BoxPersistenceDAO boxPersistenceDAO;
    private final ReservationPersistenceDAO reservationPersistenceDAO;
    private final ImageDAO imageDAO;


    public DatabaseSource() throws CheckedDatabaseException {
        DatabaseService.initialize();

        boxPersistenceDAO = new H2BoxDatabaseDAO();
        reservationPersistenceDAO = new H2ReservationDatabaseDAO();
        imageDAO = DatabaseService.getManager().getImageStorage();
    }

    @Override
    public BoxPersistenceDAO getBoxDAO() {
        return boxPersistenceDAO;
    }

    @Override
    public ReservationPersistenceDAO getReservationDAO() {
        return reservationPersistenceDAO;
    }

    @Override
    public ImageDAO getImageDAO() {
        return imageDAO;
    }
}
