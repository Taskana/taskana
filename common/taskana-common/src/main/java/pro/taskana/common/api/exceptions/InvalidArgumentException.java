package pro.taskana.common.api.exceptions;

/** This exception is thrown when a method is called with invalid argument. */
public class InvalidArgumentException extends TaskanaException {

  private static final long serialVersionUID = 1L;

  public InvalidArgumentException(String msg) {
    super(msg);
  }

  public InvalidArgumentException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
