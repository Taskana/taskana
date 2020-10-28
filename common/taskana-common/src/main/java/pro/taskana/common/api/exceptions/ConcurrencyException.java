package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown when an attempt is made to update an object that has already been
 * updated by another user.
 *
 * @author bbr
 */
public class ConcurrencyException extends TaskanaException {

  public ConcurrencyException(String msg) {
    super(msg);
  }
}
