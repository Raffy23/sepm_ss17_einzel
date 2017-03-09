package sepm.ss17.e1526280.service;

import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.dao.filesystem.ImageDAO;
import sepm.ss17.e1526280.dao.h2.H2BoxDatabaseDAO;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.LitterType;
import sepm.ss17.e1526280.dto.Reservation;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 *
 * TODO: This class needs a rewrite (async stuff)
 * TODO: Comments
 *
 * Created by
 *
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class DataService {

    private final BoxPersistenceDAO boxDAO = new H2BoxDatabaseDAO();
    private final ImageDAO imageDAO = DatabaseService.getManager().getImageStorage();


    private void handleImage(Box box) {
        if( box.getPhoto() == null || !new File(box.getPhoto()).exists() )
            return;

        final File blob = imageDAO.persistImage(box.getPhoto());
        box.setPhoto(blob.getName());
    }

    public void createNewBox(Box box) throws ObjectDoesAlreadyExistException {
        handleImage(box);
        boxDAO.persist(box);
    }

    public void updateBoxData(Box box) throws ObjectDoesNotExistException {
        final Box oldBox = boxDAO.query(box.getBoxID());

        if( oldBox.getPhoto() != null )
            imageDAO.deleteImage(oldBox.getPhoto());

        if( box.getPhoto() != null )
            if( new File(box.getPhoto()).exists() ) {
                handleImage(box);
            }

        boxDAO.merge(box);
    }

    public List<Box> getAllBoxes() {
        return boxDAO.queryAll();
    }

    public List<Box> searchForBoxes(Float price, Float size, LitterType litterType, Boolean window, Boolean indoor) {
        return boxDAO.query(new HashMap<String,Object>(){
            {
                put(BoxPersistenceDAO.QUERY_PARAM_LITTER,litterType);
                put(BoxPersistenceDAO.QUERY_PARAM_PRICE,price);
                put(BoxPersistenceDAO.QUERY_PARAM_SIZE,size);
                put(BoxPersistenceDAO.QUERY_PARAM_INDOOR,indoor);
                put(BoxPersistenceDAO.QUERY_PARAM_WINDOW,window);
            }

            @Override
            public Object put(String key,Object o) {
                if(o != null) return super.put(key,o);
                return null;
            }
        });
    }

    public void delete(Box box) {
        boxDAO.remove(box);
    }

    public void delete(Reservation res) {
        System.err.println("Err: DataServer.delete is not implemented!");
    }

    public File resolveImage(Box box) {
        return imageDAO.getFilePath(box.getPhoto());
    }

}
