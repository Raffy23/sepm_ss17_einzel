package sepm.ss17.e1526280.service;

import org.jetbrains.annotations.NotNull;

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
    CompletableFuture<List<T>> query(@NotNull Map<String,Object> param);

    /**
     * This function sends a list of objects to the backend
     * @param o a list of objects which should be persisted
     * @return a future with all given objects
     */
    CompletableFuture<List<T>> persist(@NotNull List<T> o);

    /**
     * This functions sends the Object to the Persistence DAO to save it
     * @param o object which should be saved to the backend
     * @return a future with the persisted object
     */
    CompletableFuture<T> persist(@NotNull T o);

    /**
     * This function sends the Object to the Persistance DAO to update
     * it's state in the backend
     * @param o a object which should be updated
     * @return a future with the updated object
     */
    CompletableFuture<T> update(@NotNull T o);

    /**
     * This function does send a Object to the Backend which should be
     * deleted
     * @param o object which should be deleted from the backend
     * @return a future with the type VOID
     */
    CompletableFuture<Void> remove(@NotNull T o);

}
