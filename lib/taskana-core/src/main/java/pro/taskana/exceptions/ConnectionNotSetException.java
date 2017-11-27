package pro.taskana.exceptions;

/**
 * Thrown if ConnectionManagementMode is CONNECTION_MANAGED_EXTERNALLY and an attempt is made to call an API method before the setConnection() method has been called.
 *
 */
@SuppressWarnings("serial")
public class ConnectionNotSetException extends RuntimeException {

    public ConnectionNotSetException() {
        super("Connection not set");
    }

}
