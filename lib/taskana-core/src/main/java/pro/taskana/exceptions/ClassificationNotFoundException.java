package pro.taskana.exceptions;

/**
<<<<<<< HEAD
 * This exception will be thrown if a specific task is not in the database.
 */
@SuppressWarnings("serial")
public class ClassificationNotFoundException extends NotFoundException {

    public ClassificationNotFoundException(String id) {
        super("Classification '" + id + "' not found");
    }
}
