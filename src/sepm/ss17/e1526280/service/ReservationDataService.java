package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for the Reservation Service which does specify some
 * functions for the actual Service
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public interface ReservationDataService extends BasicService<Reservation> {

    /**
     * Queries all the Reservations grouped by their reservation ID
     * @param isInvoice a flag if the invoice or the reservations should be queried
     * @return a future with a list of reservation lists
     */
    CompletableFuture<Collection<List<Reservation>>> queryGrouped(boolean isInvoice);

    /**
     * Removes all reservations given
     * @param o a list of reservations
     * @return a future with the list of reservations given as argument
     */
    CompletableFuture<List<Reservation>> delete(List<Reservation> o);

    /**
     * Returns a List of Boxes which is a subset of the boxes parameter which is not blocked in the days
     * from start to end parameters
     * @param boxes a list of boxes which should be filtered by the date
     * @param start start date
     * @param end end date
     * @return a future with a list of boxes that are free
     */
    CompletableFuture<List<Box>> queryBlocked(List<Box> boxes, Date start, Date end);

    /**
     * Returns the list of reservation whith modified attributes so they can be used
     * for invoices
     * @param o a list of reservations which should be converted to invoices
     * @return a future with the
     */
    CompletableFuture<List<Reservation>> toInvoice(List<Reservation> o);

    /**
     * Updates all reservations given
     * @param o a list of reservations
     * @return a future with the list of reservations given as argument
     */
    CompletableFuture<List<Reservation>> update(List<Reservation> o);

    /**
     * Queries all reservations for the given box
     * @param box of which the reservations should be queried
     * @param start the start date
     * @param end the end date
     * @return a future with the list of reservations
     */
    CompletableFuture<List<Reservation>> queryFor(Box box, Date start, Date end);

    /**
     * Queries all reservations for the given box
     * @param box of which the reservations should be queried
     * @return a future with the list of reservations
     */
    CompletableFuture<List<Reservation>> queryFor(Box box);

    /**
     * Queries all reservation in the given time interval
     * @param start start date
     * @param end end date
     * @return a future with the list of reservations
     */
    CompletableFuture<Collection<List<Reservation>>> queryAllIn(Date start, Date end);

}
