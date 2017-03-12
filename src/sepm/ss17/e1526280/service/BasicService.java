package sepm.ss17.e1526280.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface which provides the functions of the Services which they should
 * all implement
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public interface BasicService<T> {

    /**
     * Queries all the Entities from the Backend
     * @return a future with a list of entities
     */
    default CompletableFuture<List<T>> queryAll() {
        return query(new HashMap<>());
    }

    /**
     * Queries a list of entities which match the parameters given
     * @param param parameters for the search query
     * @return a future a list of matching entities
     */
    CompletableFuture<List<T>> query(Map<String,Object> param);

    /**
     * This function sends a list of objects to the backend
     * @param o a list of objects which should be persisted
     * @return a future with all given objects
     */
    CompletableFuture<List<T>> persist(List<T> o);

    CompletableFuture<T> persist(T o);

    CompletableFuture<T> update(T o);

    CompletableFuture<Void> remove(T o);

}
