package pro.taskana.exceptions;

/**
 * This exception is used to communicate a not authorized user.
 */
@SuppressWarnings("serial")
public class NotAuthorizedException extends Exception {

    public NotAuthorizedException(String msg) {
        super(msg);
    }
}
