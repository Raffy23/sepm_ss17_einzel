package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dto.Reservation;

import java.util.Collection;
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
     * @return a future with a list of reservation lists
     */
    CompletableFuture<Collection<List<Reservation>>> queryGrouped();

    /**
     * Removes all reservations given
     * @param o a list of reservations
     * @return a future with the list of reservations given as argument
     */
    CompletableFuture<List<Reservation>> delete(List<Reservation> o);

}
