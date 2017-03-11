package sepm.ss17.e1526280.dao.exceptions;

/**
 * @author Raphael Ludwig
 * @version 09.03.17
 */
public class DatabaseException extends RuntimeException {

    private final Exception cause;

    public DatabaseException(Exception cause) {
        this.cause = cause;
    }

    public Exception getCause() {
        return cause;
    }

}
