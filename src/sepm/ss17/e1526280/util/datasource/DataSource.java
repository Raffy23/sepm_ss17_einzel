package sepm.ss17.e1526280.util.datasource;

import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;

/**
 * A Interface which describes the DataSource for the Service Manager
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public interface DataSource {

    /**
     * @return a instance of the BoxDAO from the DataSource
     */
    BoxPersistenceDAO getBoxDAO();

    /**
     * @return a instance of the ReservationDAO from the DataSource
     */
    ReservationPersistenceDAO getReservationDAO();

    /**
     * @return a instance of the ImageDAO from the DataSource
     */
    ImageDAO getImageDAO();

}
