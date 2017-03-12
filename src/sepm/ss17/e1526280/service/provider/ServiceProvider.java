package sepm.ss17.e1526280.service.provider;

import sepm.ss17.e1526280.service.BoxService;
import sepm.ss17.e1526280.service.ReservationService;
import sepm.ss17.e1526280.util.datasource.DataSource;

/**
 * This interface does provide all the functions which are
 * needed by the DataServiceManager so it can construct the
 * Services
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public interface ServiceProvider {

    /**
     * Constructs a BoxService from the given DataSource
     * @param dataSource source of data which should be used
     * @return BoxService
     */
    BoxService getBoxService(DataSource dataSource);

    /**
     * Constructs a ReservationService form the given DataSource
     * @param dataSource source of data which should be used
     * @return ReservationService
     */
    ReservationService getReservationService(DataSource dataSource);

}
