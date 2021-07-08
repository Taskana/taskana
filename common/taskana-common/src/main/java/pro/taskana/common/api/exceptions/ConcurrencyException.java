package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown when an attempt is made to update an object that has already been
 * updated by another user.
 */
public class ConcurrencyException extends TaskanaException {

  public static final String ERROR_KEY = "ENTITY_NOT_UP_TO_DATE";

  public ConcurrencyException() {
    super(
        "The current entity cannot be updated since it has been modified while editing.",
        ErrorCode.of(ERROR_KEY));
  }
}
