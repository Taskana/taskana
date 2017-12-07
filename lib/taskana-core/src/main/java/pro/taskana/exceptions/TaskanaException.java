package pro.taskana.exceptions;

/**
 * common base class for Taskana's checked exceptions.
 * @author bbr
 *
 */
public class TaskanaException extends Exception {

    public TaskanaException() {
        super();
    }

    public TaskanaException(String message) {
        super(message);
    }

    public TaskanaException(Throwable cause) {
        super(cause);
    }

    public TaskanaException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskanaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
