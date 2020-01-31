package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown when the task state doesn't allow the requested operation.
 *
 * @author bbr
 */
public class InvalidStateException extends TaskanaException {
  private static final long serialVersionUID = 1L;

  public InvalidStateException(String msg) {
    super(msg);
  }
}
