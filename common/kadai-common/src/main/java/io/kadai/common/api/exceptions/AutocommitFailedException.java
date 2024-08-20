package io.kadai.common.api.exceptions;

/**
 * This exception is thrown when using KADAI with the AUTOCOMMIT ConnectionManagementMode and an
 * attempt to commit fails.
 */
public class AutocommitFailedException extends KadaiRuntimeException {

  public static final String ERROR_KEY = "CONNECTION_AUTOCOMMIT_FAILED";

  public AutocommitFailedException(Throwable cause) {
    super("Autocommit failed", ErrorCode.of(ERROR_KEY), cause);
  }
}
