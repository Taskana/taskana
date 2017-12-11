package pro.taskana.exceptions;

/**
 * Thrown if ConnectionManagementMode is CONNECTION_MANAGED_EXTERNALLY and an attempt is made to call an API method before the setConnection() method has been called.
 *
 */
public class ConnectionNotSetException extends TaskanaRuntimeException {

    public ConnectionNotSetException() {
        super("Connection not set");
    }

    private static final long serialVersionUID = 1L;
}
