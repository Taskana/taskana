package pro.taskana.exceptions;

/**
 * This exception will be thrown if a specific workbasket is not in the database.
 */
public class WorkbasketNotFoundException extends NotFoundException {

    public WorkbasketNotFoundException(String id) {
        super("Workbasket with '" + id + "' not found");
    }

    private static final long serialVersionUID = 1L;
}
