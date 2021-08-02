package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown when using TASKANA with the CONNECTION_MANAGED_EXTERNALLY
 * ConnectionManagementMode and an attempt is made to call an API method before the
 * TaskananEngine#setConnection() method has been called.
 */
public class ConnectionNotSetException extends TaskanaRuntimeException {

  public static final String ERROR_KEY = "CONNECTION_NOT_SET";

  public ConnectionNotSetException() {
    super("Connection not set", ErrorCode.of(ERROR_KEY));
  }

  @Override
  public String toString() {
    return "ConnectionNotSetException{}";
  }
}
