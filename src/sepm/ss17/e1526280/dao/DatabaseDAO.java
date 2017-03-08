package sepm.ss17.e1526280.dao;

import java.sql.Connection;

/**
 * This interface defines the Base of any Database driven DAO Class.
 * It extends the PersistenceDAO in a generic approach and also implements
 * the Destroyable Interface due the fact that any DatabaseDAO should close
 * any preparedStatements after the DAO is not needed anymore
 *
 * @author Raphael Ludwig
 * @version 04.03.17
 */
public interface DatabaseDAO<T> extends PersistenceDAO<T>, Destroyable {

    /**
     * @return The connection from the Database
     */
    Connection getConnection();

}
