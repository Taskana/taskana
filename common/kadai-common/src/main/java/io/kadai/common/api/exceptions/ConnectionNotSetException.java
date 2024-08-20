package io.kadai.common.api.exceptions;

/**
 * This exception is thrown when using KADAI with the EXPLICIT ConnectionManagementMode and an
 * attempt is made to call an API method before the KadainEngine#setConnection() method has been
 * called.
 */
public class ConnectionNotSetException extends KadaiRuntimeException {

  public static final String ERROR_KEY = "CONNECTION_NOT_SET";

  public ConnectionNotSetException() {
    super("Connection not set", ErrorCode.of(ERROR_KEY));
  }
}
