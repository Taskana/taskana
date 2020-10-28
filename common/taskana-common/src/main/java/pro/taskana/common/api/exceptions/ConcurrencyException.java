package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown when an attempt is made to update an object that has already been
 * updated by another user.
 */
public class ConcurrencyException extends TaskanaException {

  private static final long serialVersionUID = 1L;

  public ConcurrencyException(String msg) {
    super(msg);
  }
}
