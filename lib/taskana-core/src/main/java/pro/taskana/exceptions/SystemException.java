package pro.taskana.exceptions;

/**
 * This exception is thrown when a generic taskana problem is encountered.
 */
public class SystemException extends TaskanaRuntimeException {

    public SystemException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
