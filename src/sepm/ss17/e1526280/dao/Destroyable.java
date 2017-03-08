package sepm.ss17.e1526280.dao;

/**
 * This Class provides a interface to all classes which should clean up some Data after they are not
 * used anymore. This is done with calling the destroy() Method.
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public interface Destroyable {
    /**
     * This Method should be called of the class is not accessed anymore
     * and there is some Data which should be cleaned up
     */
    void destroy();
}
