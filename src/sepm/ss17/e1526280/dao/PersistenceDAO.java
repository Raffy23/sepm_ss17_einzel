package sepm.ss17.e1526280.dao;

import sepm.ss17.e1526280.dao.exceptions.ObjectDoesAlreadyExistException;
import sepm.ss17.e1526280.dao.exceptions.ObjectDoesNotExistException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Interface is the Base of all other DAO Classes and Interfaces and
 * provides a generic access to the most used and needed methods.
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public interface PersistenceDAO<T> {

    /**
     * This function issues a Query which does query all the Elements from the type T from
     * the Datasource
     * @return a List of all Elements
     */
    default List<T> queryAll() {
        return query(new HashMap<>());
    }

    /**
     * This function issues a Query which does query all the Elements T from the Datasource
     * which do match the param Objects
     * @param param a Map of Parameters for the Query
     * @return a List of all found Elements
     */
    List<T> query(Map<String,Object> param);

    /**
     * This function persists a Object in the Datasource, if the Object already exists a Exception is thrown
     * @param o object wich should be persisted
     * @throws ObjectDoesAlreadyExistException thrown if the object already exists
     */
    void persist(T o) throws ObjectDoesAlreadyExistException;

    /**
     * This function persists multiple Object in the Datasource, it makes use of the persist(T) function
     * @param o a List of Objects which should be persisted in the Datasource
     * @throws ObjectDoesAlreadyExistException thrown if a object in the list already exists
     */
    default void persist(List<T> o) throws ObjectDoesAlreadyExistException {
        for(T oo:o) this.persist(oo);
    }

    /**
     * This function does merge the current Object with the one in the Datasource where the Data in the
     * Datasource if overwritten
     * @param o current object
     * @throws ObjectDoesNotExistException thrown if the Object does not exist in the Database
     */
    void merge(T o) throws ObjectDoesNotExistException;

    /**
     * This function does remove the Object from the Datasource
     * @param o object which should be removed
     */
    void remove(T o) throws ObjectDoesNotExistException;

}
