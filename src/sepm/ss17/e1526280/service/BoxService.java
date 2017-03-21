package sepm.ss17.e1526280.service;

import org.jetbrains.annotations.NotNull;
import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.gui.dialogs.DialogUtil;
import sepm.ss17.e1526280.service.exception.DataException;

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


    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Box> persist(@NotNull Box o) {
        //TODO: make async
        final File f = imageDAO.persistImage(o.getPhoto());
        o.setPhoto(f.getName());

        return super.persist(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> remove(@NotNull Box o) {
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
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Box> update(@NotNull Box box) {
        LOG.trace("update: " + box);

        //TODO: Handle image creation & merge as a "transaction"
        return CompletableFuture.supplyAsync(() -> {
            try {
                final Box oldBox = boxDAO.query(box.getBoxID());

                if( !(box.getPhoto() != null && new File(box.getPhoto()).exists()) ) {
                    LOG.debug("Image in Box is not a File, will query the one from the database!");
                    box.setPhoto(oldBox.getPhoto());

                    boxDAO.merge(box);
                    return box;
                }

                if( !imageDAO.getFilePath(box.getPhoto()).exists() ) {
                    persistImageToStore(box);

                    if (oldBox.getPhoto() != null)
                        imageDAO.deleteImage(oldBox.getPhoto());
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
        if( box.getPhoto() == null ) {
            LOG.debug("Removing current box ...");
            return;
        }

        final File blob = imageDAO.persistImage(box.getPhoto());
        box.setPhoto(blob.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File resolveImage(Box box) {
        final File f = imageDAO.getFilePath(box.getPhoto());
        if( f.exists() )
            return f;

        LOG.debug("Box DTO is not consistent with DAO, will update Object");
        try {
            final Box db = boxDAO.query(box.getBoxID());
            box.setPhoto(db.getPhoto());

            return imageDAO.getFilePath(db.getPhoto());
        } catch (ObjectDoesNotExistException e) {
            DialogUtil.onFatal(e);
        }

        return null;
    }
}
