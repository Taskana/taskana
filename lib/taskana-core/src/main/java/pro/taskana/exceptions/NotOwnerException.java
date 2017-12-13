package pro.taskana.exceptions;

/**
 * Thrown when a user want to perform actions which only can be
 * done by administrator or the object-owner.
 */
public class NotOwnerException extends TaskanaException {

    private static final long serialVersionUID = 5212541393104879870L;

    public NotOwnerException(String message) {
        super(message);
    }
}
