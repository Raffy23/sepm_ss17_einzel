package sepm.ss17.e1526280.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.service.BoxDataService;
import sepm.ss17.e1526280.service.ReservationDataService;
import sepm.ss17.e1526280.service.provider.ServiceProvider;
import sepm.ss17.e1526280.util.datasource.DataSource;

/**
 * This class is responsible to manage all the DataServices which are needed
 * by the Application. With the initialize Method it can be initialized with a
 * specific DataSource and a ServiceProvider which does provide the functions
 * to create the Services from the DataSource
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public class DataServiceManager {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(DataServiceManager.class);

    /** Static service variable -> singleton pattern **/
    private static DataServiceManager service;

    /** Box Service from the Provider **/
    private final BoxDataService boxDataService;

    /** Reservation Service from the Provider **/
    private final ReservationDataService reservationDataService;


    /**
     * Private Constructor for the Singleton which creates all the stuff we need
     * @param source source which should be used
     * @param serviceProvider provider which should be uesed
     */
    private DataServiceManager(DataSource source,ServiceProvider serviceProvider) {
       boxDataService = serviceProvider.getBoxService(source);
       reservationDataService = serviceProvider.getReservationService(source);
    }

    /**
     * This Method must be called before getService()
     * As the name states it does initialize the Manager with the given Parameters
     * @param dataSource Data Source which should be used by all Services
     * @param serviceProvider Class which can construct the Services from the Data Source
     */
    public static void initialize(DataSource dataSource, ServiceProvider serviceProvider) {
        if( service == null )
            service = new DataServiceManager(dataSource, serviceProvider);
    }

    /**
     * @return the instance of the Service
     */
    public static DataServiceManager getService() {
        return service;
    }

    /**
     * @return the service for Boxes
     */
    public BoxDataService getBoxDataService() {
        return boxDataService;
    }

    /**
     * @return the service for reservations
     */
    public ReservationDataService getReservationDataService() {
        return reservationDataService;
    }

}
