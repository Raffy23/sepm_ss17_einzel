package sepm.ss17.e1526280.dao;

import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;

import java.util.Date;
import java.util.List;

/**
 * Interface which is the base for the Reservations DAOs and provides some
 * functions which are commonly used by the callers
 *
 * @author Raphael Ludwig
 * @version 11.03.17
 */
public interface ReservationPersistenceDAO extends PersistenceDAO<Reservation> {

    String QUERY_PARAM_IS_INVOICE = "alreadyinvoice";
    String QUERY_PARAM_LIMIT = "limit";
    String QUERY_PARAM_BOX_ID = "boxid";

    /**
     * Queries if the Box is reserved in between the start and end date
     * @param box box which should be looked up
     * @param start start Date
     * @param end end date
     * @return true if somehow reserved otherwise false
     */
    boolean isBoxReserved(Box box, Date start, Date end);

    /**
     * Batch-deletes the reservations
     * @param o reservations which should be deleted
     */
    void remove(List<Reservation> o);

    /**
     * Batch-merges the reservations
     * @param o reservations which should be deleted
     */
    void merge(List<Reservation> o);

    /**
     * Searches for all Reservations for that box which are between start and end
     * @param box box which should be searched for
     * @param start start date
     * @param end end date
     * @return a list of reservations
     */
    List<Reservation> queryFor(Box box,Date start, Date end);

}
