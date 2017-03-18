package sepm.ss17.e1526280.service;

import org.jetbrains.annotations.Nullable;
import sepm.ss17.e1526280.dao.BoxPersistenceDAO;
import sepm.ss17.e1526280.dto.Box;
import sepm.ss17.e1526280.dto.LitterType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface which does specify the functions of the
 * Box Service
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public interface BoxDataService extends BasicService<Box> {

    /**
     * Queries only the visible Boxes for the UI
     * @return a future with a list of boxes
     */
    default CompletableFuture<List<Box>> queryVisible() {
        //Fill search Map with the Values
        final Map<String,Object> searchData = new HashMap<String,Object>() {
            {
                put(BoxPersistenceDAO.QUERY_PARAM_DELFLAG, false);
            }

            @Override
            public Object put(String key, Object o) {
                if (o != null) return super.put(key, o);
                return null;
            }
        };

        return query(searchData);
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
    default CompletableFuture<List<Box>> search(@Nullable Float price,@Nullable Float size,@Nullable LitterType litterType,@Nullable Boolean window,@Nullable Boolean indoor) {
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

        return query(searchData);
    }

    /**
     * Resolves the Image for a Box
     * @param box target box
     * @return the file object for the image
     */
    File resolveImage(Box box);
    
}
