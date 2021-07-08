package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown in ConnectionManagementMode AUTOCOMMIT when an attempt to commit fails.
 */
public class AutocommitFailedException extends TaskanaRuntimeException {

  public static final String ERROR_KEY = "CONNECTION_AUTOCOMMIT_FAILED";

  public AutocommitFailedException(Throwable cause) {
    super("Autocommit failed", ErrorCode.of(ERROR_KEY), cause);
  }
}
