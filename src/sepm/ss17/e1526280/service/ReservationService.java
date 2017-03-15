package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.Reservation;

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

    private final ReservationPersistenceDAO persistenceDAO;

    public ReservationService(ReservationPersistenceDAO persistenceDAO) {
        super(persistenceDAO,ReservationService.class);
        this.persistenceDAO = persistenceDAO;
    }

    @Override
    public CompletableFuture<Collection<List<Reservation>>> queryGrouped() {
        LOG.debug("Query Grouped");

        return CompletableFuture.supplyAsync(() -> {
            final Map<Integer,List<Reservation>> dataCollector = new HashMap<>();
            final Map<String,Object> parameters = new HashMap<>();
            parameters.put(ReservationPersistenceDAO.QUERY_PARAM_IS_INVOICE, Boolean.FALSE);

            final List<Reservation> rawData = persistenceDAO.query(parameters);

            rawData.forEach(reservation -> {
                final List<Reservation> dL = dataCollector.getOrDefault(reservation.getId(),new ArrayList<>());
                dL.add(reservation);

                dataCollector.put(reservation.getId(),dL);
            });

            return dataCollector.values();
        });
    }

    @Override
    public CompletableFuture<List<Reservation>> delete(List<Reservation> o) {
        LOG.debug("Delete " + o);

        return CompletableFuture.supplyAsync(() -> {
             persistenceDAO.remove(o);
            return o;
        });
    }

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

    @Override
    public CompletableFuture<List<Reservation>> update(List<Reservation> o) {
        return null;
    }
}
