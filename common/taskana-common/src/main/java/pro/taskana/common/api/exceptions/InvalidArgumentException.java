package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown when a method is called with invalid argument.
 *
 * @author bbr
 */
public class InvalidArgumentException extends TaskanaException {

  public InvalidArgumentException(String msg) {
    super(msg);
  }

  public InvalidArgumentException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
