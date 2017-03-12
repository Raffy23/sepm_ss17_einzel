package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dto.Reservation;

import java.util.ArrayList;
import java.util.Collection;
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
        return CompletableFuture.supplyAsync(() -> {
            final Map<Integer,List<Reservation>> dataCollector = new HashMap<>();
            final List<Reservation> rawData = persistenceDAO.queryAll();

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
        return CompletableFuture.supplyAsync(() -> {
           for(Reservation oo : o)
               try {
                   persistenceDAO.remove(oo);
               } catch (ObjectDoesNotExistException e) {
                   throw new DataException(e);
               }

            return o;
        });
    }
}
