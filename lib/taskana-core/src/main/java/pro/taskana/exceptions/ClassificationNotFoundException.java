package pro.taskana.exceptions;

/**
 * Thrown if a specific task is not in the database.
 */
@SuppressWarnings("serial")
public class ClassificationNotFoundException extends NotFoundException {

    public ClassificationNotFoundException(String id) {
        super("Classification '" + id + "' not found");
    }
}
