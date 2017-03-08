package sepm.ss17.e1526280.dao;

import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExist;
import sepm.ss17.e1526280.dto.Box;

import java.util.HashMap;
import java.util.List;

/**
 * This interface int the Base Interface for the Box Persistence part,
 * it implements a single query for the primary key and also the names
 * of the query parameters
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public interface BoxPersistenceDAO extends PersistenceDAO<Box> {

    //TODO: Describe all this fields
    String QUERY_PARAM_BOX_ID = "boxID";
    String QUERY_PARAM_PRICE = "price";
    String QUERY_PARAM_SIZE  = "size";
    String QUERY_PARAM_LITTER = "litter";
    String QUERY_PARAM_WINDOW = "window";
    String QUERY_PARAM_INDOOR = "indoor";
    String QUERY_PARAM_LIMIT = "limit";
    String QUERY_PARAM_DELFLAG = "deleted";

    /**
     * This function allows the user to search for a Box with it's primary key in
     * a easy way
     * @param key the primary key of the box (boxID)
     * @return the Box which was found
     * @throws ObjectDoesNotExist is thrown if on Box was found
     */
    default Box query(int key) throws ObjectDoesNotExist {
        final List<Box> data = query(new HashMap<String,Object>(){
            {
                put(QUERY_PARAM_BOX_ID,key);
            }
        });

        if( data.size() == 0)
            throw new ObjectDoesNotExist();

        return data.get(0);
    }

}
