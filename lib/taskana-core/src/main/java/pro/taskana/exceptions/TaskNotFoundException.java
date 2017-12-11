package pro.taskana.exceptions;

/**
 * This exception will be thrown if a specific task is not in the database.
 */
public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException(String id) {
        super("Task '" + id + "' not found");
    }

    private static final long serialVersionUID = 1L;
}
