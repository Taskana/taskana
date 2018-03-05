package pro.taskana.exceptions;

/**
 * This exception will be thrown if a specific task is not in the database.
 */
public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException(String id, String msg) {
        super(id, msg);
    }

    private static final long serialVersionUID = 1L;
}
