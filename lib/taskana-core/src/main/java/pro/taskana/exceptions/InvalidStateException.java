package pro.taskana.exceptions;

/**
 * This exception is thrown when the task state doesn't allow the requested operation.
 * @author bbr
 *
 */
public class InvalidStateException extends TaskanaException {
    public InvalidStateException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
