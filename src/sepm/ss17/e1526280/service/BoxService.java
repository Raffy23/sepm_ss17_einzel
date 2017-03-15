package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;
import sepm.ss17.e1526280.dto.Box;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * The Box Service which handles all the Ui data Requests
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public class BoxService extends AbstractService<Box> implements BoxDataService {

    private final BoxPersistenceDAO boxDAO;
    private final ImageDAO imageDAO;

    public BoxService(BoxPersistenceDAO boxDAO, ImageDAO imageDAO) {
        super(boxDAO,BoxService.class);
        this.boxDAO = boxDAO;
        this.imageDAO = imageDAO;
    }


    @Override
    public CompletableFuture<Box> persist(Box o) {
        final File f = imageDAO.persistImage(o.getPhoto());
        o.setPhoto(f.getName());

        return super.persist(o);
    }

    @Override
    public CompletableFuture<Void> remove(Box o) {
        LOG.trace("remove " + o);

        return CompletableFuture.supplyAsync(() -> {
            super.remove(o).join();
            return null;
        }).exceptionally(throwable ->  {
            o.setDeleted(true);
            this.update(o).join();
            return null;
        }).thenRun(() -> { /*Only needed to get back to type Void*/ });
    }

    /**
     * This function does update a Box in the Backend
     *
     * @param box box which should be updated
     * @return a CompletableFuture which returns the result of the operation asynchronously
     */
    public CompletableFuture<Box> update(Box box) {
        LOG.trace("update: " + box);
        return CompletableFuture.supplyAsync(() -> {
            try {
                final Box oldBox = boxDAO.query(box.getBoxID());

                if (oldBox.getPhoto() != null)
                    imageDAO.deleteImage(oldBox.getPhoto());

                if (box.getPhoto() != null)
                    if (new File(box.getPhoto()).exists()) {
                        persistImageToStore(box);
                    }

                boxDAO.merge(box);
                return box;
            } catch (ObjectDoesNotExistException e) {
                LOG.error("There was an error while updating the following Box: " + box + "\n the Exception Message was: " + e.getMessage());
                throw new DataException(e);
            }
        });
    }


    /**
     * This function does handle the Image stuff for the Box
     *
     * @param box a box which should have stored the image in the database
     */
    private void persistImageToStore(Box box) {
        LOG.trace("persistImageToStore( " + box + " )");
        if (box.getPhoto() == null || !new File(box.getPhoto()).exists())
            return;

        final File blob = imageDAO.persistImage(box.getPhoto());
        box.setPhoto(blob.getName());
    }

    @Override
    public File resolveImage(Box box) {
        return imageDAO.getFilePath(box.getPhoto());
    }
}
