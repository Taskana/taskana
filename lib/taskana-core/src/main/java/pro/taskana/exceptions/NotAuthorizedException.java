package pro.taskana.exceptions;

/**
 * This exception is used to communicate a not authorized user.
 */
public class NotAuthorizedException extends TaskanaException {

    public NotAuthorizedException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
