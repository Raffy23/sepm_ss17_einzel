package sepm.ss17.e1526280.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.PersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;
import sepm.ss17.e1526280.service.exception.DataException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Base class for the Services which implements the simple stuff
 * which might be needed by the simple Services
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public abstract class AbstractService<T> implements BasicService<T> {

    /**
     * Logger for logging ... duh
     **/
    protected final Logger LOG;

    /**
     * The persistence backend which should be used
     */
    protected final PersistenceDAO<T> dao;


    /**
     * {@inheritDoc}
     */
    public AbstractService(PersistenceDAO<T> dao, Class<?> loggerClass) {
        this.dao = dao;
        LOG = LoggerFactory.getLogger(loggerClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<T>> query(@NotNull Map<String, Object> param) {
        LOG.debug("query " + param);
        return CompletableFuture.supplyAsync(() -> dao.query(param));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("Duplicates") //Since its a List an the other one is a Object is must be a duplicate
    public CompletableFuture<List<T>> persist(@NotNull List<T> o) {
        LOG.debug("persist " + o);
        return CompletableFuture.supplyAsync(() -> {
            try {
                dao.persist(o);
            } catch (ObjectDoesAlreadyExistException e) {
                throw new DataException(e);
            }

            return o;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("Duplicates")  //Since its a List an the other one is a Object is must be a duplicate
    public CompletableFuture<T> persist(@NotNull T o) {
        LOG.debug("persist " + o);
        return CompletableFuture.supplyAsync(() -> {
            try {
                dao.persist(o);
            } catch (ObjectDoesAlreadyExistException e) {
                throw new DataException(e);
            }

            return o;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<T> update(@NotNull T o) {
        LOG.debug("update " + o);
        return CompletableFuture.supplyAsync(() -> {
            try {
                dao.merge(o);
            } catch (ObjectDoesNotExistException e) {
                throw new DataException(e);
            }

            return o;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> remove(@NotNull T o) {
        LOG.debug("remove " + o);
        return CompletableFuture.supplyAsync(() -> {
            try {
                dao.remove(o);
            } catch (ObjectDoesNotExistException e) {
                throw new DataException(e);
            }
            return null;
        });
    }
}
