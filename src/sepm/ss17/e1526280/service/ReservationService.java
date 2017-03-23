package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;
import sepm.ss17.e1526280.service.exception.DataException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The actual Reservation Service which does handle all the Data
 * requests from the UI
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public class ReservationService extends AbstractService<Reservation> implements ReservationDataService {

    /** The DAO which is used to query and modify Data **/
    private final ReservationPersistenceDAO persistenceDAO;

    /**
     * Creates a ReservationService with the DAO to do the data operations
     * @param persistenceDAO which is used to to the data operation in the backend
     */
    public ReservationService(ReservationPersistenceDAO persistenceDAO) {
        super(persistenceDAO,ReservationService.class);
        this.persistenceDAO = persistenceDAO;
    }

    /**
     * This function does group the Reservations by their boxes
     * @param rawData which should be groups
     * @return a List of groups reservations (in a List)
     */
    private static Collection<List<Reservation>> group(List<Reservation> rawData) {
        final Map<Integer,List<Reservation>> dataCollector = new HashMap<>();

        rawData.forEach(reservation -> {
            final List<Reservation> dL = dataCollector.getOrDefault(reservation.getId(),new ArrayList<>());
            dL.add(reservation);

            dataCollector.put(reservation.getId(),dL);
        });

        return dataCollector.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Collection<List<Reservation>>> queryGrouped(boolean isInvoice) {
        LOG.debug("Query Grouped ("+isInvoice+")");

        return CompletableFuture.supplyAsync(() -> {
            final Map<String,Object> parameters = new HashMap<>();
            parameters.put(ReservationPersistenceDAO.QUERY_PARAM_IS_INVOICE, isInvoice);

            return group(persistenceDAO.query(parameters));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<Reservation>> delete(List<Reservation> o) {
        LOG.debug("Delete " + o);

        return CompletableFuture.supplyAsync(() -> {
             persistenceDAO.remove(o);
            return o;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<Box>> queryBlocked(List<Box> boxes, Date start, Date end) {
        LOG.debug("Query Blocked Boxes " + boxes + " between " + start + " and " + end);

        return CompletableFuture.supplyAsync(() -> {
            final List<Box> rs = new ArrayList<>();

            boxes.forEach(box -> {
                if (persistenceDAO.isBoxReserved(box, start, end))
                    rs.add(box);
            });

            return rs;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<Reservation>> toInvoice(List<Reservation> o) {
        LOG.debug("Convert to Invoice " + o);

        o.forEach(reservation -> reservation.setAlreadyInvoice(true));
        return CompletableFuture.supplyAsync(() -> {

            for(Reservation reservation:o) {
                try {
                    persistenceDAO.merge(reservation);
                } catch (ObjectDoesNotExistException e) {
                    throw new DataException(e);
                }
            }

            return o;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<Reservation>> update(List<Reservation> o) {
        LOG.debug("Update " + o);

        return  CompletableFuture.supplyAsync(() -> {
            persistenceDAO.merge(o);
            return o;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<Reservation>> queryFor(Box box, Date start, Date end) {
        LOG.debug("queryFor: " + box + " between " + start + " " + end);

        return CompletableFuture.supplyAsync(() -> persistenceDAO.queryFor(box,start,end));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Collection<List<Reservation>>> queryAllIn(Date start, Date end) {
        LOG.debug("queryAllIn: " + start + " to " + end);

        return CompletableFuture.supplyAsync(() -> group(persistenceDAO.queryBetween(start,end)));
    }
}
