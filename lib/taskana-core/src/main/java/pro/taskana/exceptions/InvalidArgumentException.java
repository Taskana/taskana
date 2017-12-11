package pro.taskana.exceptions;

/**
 * This exception is thrown when a method is called with invalid argument.
 *
 * @author bbr
 */
public class InvalidArgumentException extends TaskanaException {

    public InvalidArgumentException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
