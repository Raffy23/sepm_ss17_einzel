package sepm.ss17.e1526280.dao.exceptions;

/**
 * This class is only a named Exception for the Database System which must be
 * checked
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class CheckedDatabaseException extends Exception {

    public CheckedDatabaseException(Exception cause) {
        super(cause);
    }

}
