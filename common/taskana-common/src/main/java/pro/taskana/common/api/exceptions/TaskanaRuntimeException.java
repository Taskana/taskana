package pro.taskana.common.api.exceptions;

/**
 * Common base class for Taskana's runtime exceptions.
 *
 * @author bbr
 */
public class TaskanaRuntimeException extends RuntimeException {

  public TaskanaRuntimeException() {
    super();
  }

  public TaskanaRuntimeException(String message) {
    super(message);
  }

  public TaskanaRuntimeException(Throwable cause) {
    super(cause);
  }

  public TaskanaRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public TaskanaRuntimeException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
