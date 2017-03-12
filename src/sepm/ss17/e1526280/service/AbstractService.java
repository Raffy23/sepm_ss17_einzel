package sepm.ss17.e1526280.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.ss17.e1526280.dao.PersistenceDAO;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;

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
    protected final PersistenceDAO<T> dao;


    public AbstractService(PersistenceDAO<T> dao, Class<?> loggerClass) {
        this.dao = dao;
        LOG = LoggerFactory.getLogger(loggerClass);
    }

    @Override
    public CompletableFuture<List<T>> query(Map<String, Object> param) {
        LOG.debug("query " + param);
        return CompletableFuture.supplyAsync(() -> dao.query(param));
    }

    @Override
    public CompletableFuture<List<T>> persist(List<T> o) {
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

    @Override
    public CompletableFuture<T> persist(T o) {
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

    @Override
    public CompletableFuture<T> update(T o) {
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

    @Override
    public CompletableFuture<Void> remove(T o) {
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
