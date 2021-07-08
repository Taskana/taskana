package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown when ConnectionManagementMode is CONNECTION_MANAGED_EXTERNALLY and an
 * attempt is made to call an API method before the setConnection() method has been called.
 */
public class ConnectionNotSetException extends TaskanaRuntimeException {

  public static final String ERROR_KEY = "CONNECTION_NOT_SET";

  public ConnectionNotSetException() {
    super("Connection not set", ErrorCode.of(ERROR_KEY));
  }
}
