package pro.taskana.exceptions;

/**
 * This exception is used to communicate that a user is not authorized to query a Workbasket.
 */
public class NotAuthorizedToQueryWorkbasketException extends TaskanaRuntimeException {

    public NotAuthorizedToQueryWorkbasketException(String msg) {
        super(msg);
    }

    public NotAuthorizedToQueryWorkbasketException(String msg, Throwable cause) {
        super(msg, cause);
    }

    private static final long serialVersionUID = 1L;

}
