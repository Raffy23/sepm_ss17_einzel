package sepm.ss17.e1526280.service.provider;

import sepm.ss17.e1526280.service.BoxService;
import sepm.ss17.e1526280.service.ReservationService;
import sepm.ss17.e1526280.util.datasource.DataSource;

/**
 * This class provides functions for the DataServiceManager to
 * construct the Services needed. Only the Simple Base Services
 * are created by this class
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public class SimpleServiceProvider implements ServiceProvider {

    @Override
    public BoxService getBoxService(DataSource dataSource) {
        return new BoxService(dataSource.getBoxDAO(), dataSource.getImageDAO());
    }

    @Override
    public ReservationService getReservationService(DataSource dataSource) {
        return new ReservationService(dataSource.getReservationDAO());
    }

}
