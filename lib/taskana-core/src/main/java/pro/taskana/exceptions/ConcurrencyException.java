package pro.taskana.exceptions;

/**
 * This exception is thrown when an attempt is made to update an object that has already been updated by another user.
 *
 * @author bbr
 */
public class ConcurrencyException extends TaskanaException {

    public ConcurrencyException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
