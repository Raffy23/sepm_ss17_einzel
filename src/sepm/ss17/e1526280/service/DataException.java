package sepm.ss17.e1526280.service;

/**
 * A Exception which is thrown by the DataServices if something goes wrong
 *
 * @author Raphael Ludwig
 * @version 12.03.17
 */
public class DataException extends RuntimeException {
    public DataException(Throwable throwable) {
        super(throwable);
    }
}
