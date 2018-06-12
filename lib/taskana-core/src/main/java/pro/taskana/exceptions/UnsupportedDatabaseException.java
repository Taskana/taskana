package pro.taskana.exceptions;

/**
 * This exception will be thrown if the database name doesn't match to one of the desired databases.
 */
public class UnsupportedDatabaseException extends RuntimeException {

    public UnsupportedDatabaseException(String name) {
        super("Database with '" + name + "' not found");
    }

    private static final long serialVersionUID = 1L;
}
