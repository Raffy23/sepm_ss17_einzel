package sepm.ss17.e1526280.util.datasource;

import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public interface DataSource {

    BoxPersistenceDAO getBoxDAO();
    ReservationPersistenceDAO getReservationDAO();
    ImageDAO getImageDAO();

}
