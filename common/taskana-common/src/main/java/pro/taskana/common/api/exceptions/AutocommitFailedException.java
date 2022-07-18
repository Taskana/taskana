package pro.taskana.common.api.exceptions;

/**
 * The AutocommitFailedException is thrown when using TASKANA with the AUTOCOMMIT
 * ConnectionManagementMode and an attempt to commit fails.
 */
public class AutocommitFailedException extends TaskanaRuntimeException {

  public static final String ERROR_KEY = "CONNECTION_AUTOCOMMIT_FAILED";

  public AutocommitFailedException(Throwable cause) {
    super("Autocommit failed", ErrorCode.of(ERROR_KEY), cause);
  }
}
