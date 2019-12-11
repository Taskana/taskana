package pro.taskana.exceptions;

/**
 * This exception is used to communicate a not authorized user.
 */
public class NotAuthorizedException extends TaskanaException {

    private final String currentUserId;

    public NotAuthorizedException(String msg, String currentUserId) {
        super(msg + " - [CURRENT USER: {'" + currentUserId + "'}]");
        this.currentUserId = currentUserId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    private static final long serialVersionUID = 21235L;
}
