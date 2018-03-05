package pro.taskana.exceptions;

/**
 * This exception will be thrown if a specific object is not in the database.
 */
public class NotFoundException extends TaskanaException {

    String id;

    public NotFoundException(String id, String message) {
        super(message);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private static final long serialVersionUID = 1L;
}
