package pro.taskana.exceptions;

/**
 * Thrown if a specific task is not in the database.
 */
public class ClassificationNotFoundException extends NotFoundException {

    public ClassificationNotFoundException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
