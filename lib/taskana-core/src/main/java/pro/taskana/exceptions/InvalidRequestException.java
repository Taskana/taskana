package pro.taskana.exceptions;

/**
 * This exception is thrown when a method is called in a context where it must not be called.
 *
 * @author bbr
 */
public class InvalidRequestException extends TaskanaException {

    public InvalidRequestException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
