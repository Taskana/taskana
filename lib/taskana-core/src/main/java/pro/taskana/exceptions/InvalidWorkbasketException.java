package pro.taskana.exceptions;

/**
 * This exception is thrown when a request is made to insert or update a workbasket that is missing a required property.
 *
 * @author bbr
 */

public class InvalidWorkbasketException extends TaskanaException {

    public InvalidWorkbasketException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
