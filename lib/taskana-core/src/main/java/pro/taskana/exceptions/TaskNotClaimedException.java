package pro.taskana.exceptions;

/**
 * Thrown when a Task should be completed, but it wasn´t
 * claimed before, which is required.
 */
public class TaskNotClaimedException extends TaskanaException {

    private static final long serialVersionUID = 2345893994458456182L;

    public TaskNotClaimedException(String message) {
        super(message);
    }
}
