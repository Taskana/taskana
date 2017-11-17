package pro.taskana.exceptions;

/**
 * This exception will be thrown if a specific workbasket is not in the database.
 */
@SuppressWarnings("serial")
public class WorkbasketNotFoundException extends NotFoundException {

    public WorkbasketNotFoundException(String id) {
        super("Workbasket with '" + id + "' not found");
    }
}
