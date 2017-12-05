package pro.taskana.exceptions;

/**
 * This exception is thrown when a method is called with invalid argument.
 * @author bbr
 *
 */
@SuppressWarnings("serial")
public class InvalidArgumentException extends TaskanaException {

    public InvalidArgumentException(String msg) {
        super(msg);
    }
}
