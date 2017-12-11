package pro.taskana.exceptions;

/**
 * This exception will be thrown if a specific object is not in the database.
 */
public class NotFoundException extends TaskanaException {

    public NotFoundException(String id) {
        super(id);
    }

    private static final long serialVersionUID = 1L;
}
