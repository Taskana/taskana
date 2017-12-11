package pro.taskana.exceptions;

/**
 * Thrown in ConnectionManagementMode AUTOCOMMIT when an attempt to commit fails.
 *
 */
public class AutocommitFailedException extends TaskanaRuntimeException {
    public AutocommitFailedException(Throwable cause) {
        super("Autocommit failed", cause);
    }
    private static final long serialVersionUID = 1L;
}
