package sepm.ss17.e1526280.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.ReservationPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;
import sepm.ss17.e1526280.dao.h2.H2BoxDatabaseDAO;
import sepm.ss17.e1526280.dao.h2.H2ReservationDatabaseDAO;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.LitterType;
import sepm.ss17.e1526280.dto.Reservation;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This class does handle all the communication between the controller and the
 * Backend in a more or less asynchronous way so the ui is not blocked by any
 * of the requests of this service
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class DataService {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(DataService.class);

    /** Static service variable -> singleton pattern **/
    private static final DataService service = new DataService();

    /** PersistenceDAO for all the Boxes we have **/
    private final BoxPersistenceDAO boxDAO = new H2BoxDatabaseDAO();
    private final ReservationPersistenceDAO resDAO = new H2ReservationDatabaseDAO();

    /** DAO for the Images we have **/
    private final ImageDAO imageDAO = DatabaseService.getManager().getImageStorage();


    private DataService() {}
    public static DataService getService() {
        return service;
    }

    /**
     * This function does handle the Image stuff for the Box
     * @param box a box which should have stored the image in the database
     */
    private void persistImageToStore(Box box) {
        LOG.trace("persistImageToStore( " + box +" )");
        if( box.getPhoto() == null || !new File(box.getPhoto()).exists() )
            return;

        final File blob = imageDAO.persistImage(box.getPhoto());
        box.setPhoto(blob.getName());
    }

    /**
     * This function does create a Box into the Backend
     * @param box the object which should be persisted
     * @return a CompletableFuture which returns the result of the operation asynchronously
     */
    public CompletableFuture<Box> persist(Box box) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.trace("persist " + box);
            persistImageToStore(box);

            try {
                boxDAO.persist(box);
                return box;
            } catch (ObjectDoesAlreadyExistException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This function does update a Box in the Backend
     * @param box box which should be updated
     * @return a CompletableFuture which returns the result of the operation asynchronously
     */
    public CompletableFuture<Box> update(Box box) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.trace("update: " + box);
            try {
                final Box oldBox = boxDAO.query(box.getBoxID());

                if( oldBox.getPhoto() != null )
                    imageDAO.deleteImage(oldBox.getPhoto());

                if( box.getPhoto() != null )
                    if(new File(box.getPhoto()).exists()) {
                        persistImageToStore(box);
                    }

                boxDAO.merge(box);
                return box;
            } catch (ObjectDoesNotExistException e) {
                LOG.error("There was an error while updating the following Box: " + box + "\n the Exception Message was: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This function does query for all Boxes in the Backend
     * @return a CompletableFuture which returns the result of the operation asynchronously
     */
    public CompletableFuture<List<Box>> getAllBoxes() {
        LOG.trace("getAllBoxes");
        return CompletableFuture.supplyAsync(boxDAO::queryAll);
    }

    /**
     * This function does query for specific boxes in the Backend
     * @param price the price of the Box
     * @param size the size of the Box
     * @param litterType the litter type
     * @param window true if the box has a window
     * @param indoor true if the box is indoor
     * @return a CompletableFuture which returns the result of the operation asynchronously
     */
    public CompletableFuture<List<Box>> searchForBoxes(Float price, Float size, LitterType litterType, Boolean window, Boolean indoor) {
        //Fill search Map with the Values
        final Map<String,Object> searchData = new HashMap<String,Object>() {
            {
                put(BoxPersistenceDAO.QUERY_PARAM_LITTER, litterType);
                put(BoxPersistenceDAO.QUERY_PARAM_PRICE, price);
                put(BoxPersistenceDAO.QUERY_PARAM_SIZE, size);
                put(BoxPersistenceDAO.QUERY_PARAM_INDOOR, indoor);
                put(BoxPersistenceDAO.QUERY_PARAM_WINDOW, window);
            }

            @Override
            public Object put(String key, Object o) {
                if (o != null) return super.put(key, o);
                return null;
            }
        };

        LOG.trace("searchForBoxes");
        return CompletableFuture.supplyAsync(() -> boxDAO.query(searchData));
    }

    /**
     * This function deletes a Box from the backend
     * //TODO: make it async
     * @param box which should be deleted
     */
    public void delete(Box box) {
        LOG.trace("delete: " + box);
        boxDAO.remove(box);
    }

    public CompletableFuture<List<Reservation>> persist(List<Reservation> rs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                resDAO.persist(rs);
                return rs;
            } catch (ObjectDoesAlreadyExistException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Collection<List<Reservation>>> queryReservations() {
        return CompletableFuture.supplyAsync(() -> {
            final Map<Integer,List<Reservation>> dataCollector = new HashMap<>();
            final List<Reservation> rawData = resDAO.queryAll();

            rawData.forEach(reservation -> {
                final List<Reservation> dL = dataCollector.getOrDefault(reservation.getId(),new ArrayList<>());
                dL.add(reservation);

                dataCollector.put(reservation.getId(),dL);
            });

            return dataCollector.values();
        });
    }

    public void delete(Reservation res) {
        LOG.error("delete is not implemented!");
        throw new NotImplementedException();
    }

    /**
     * This function does get the image File object from the image name in the Box
     * @param box the box where the image should be resolved
     * @return a File class for the image
     */
    public File resolveImage(Box box) {
        return imageDAO.getFilePath(box.getPhoto());
    }

}
