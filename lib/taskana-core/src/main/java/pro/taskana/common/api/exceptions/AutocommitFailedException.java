package pro.taskana.common.api.exceptions;

/** Thrown in ConnectionManagementMode AUTOCOMMIT when an attempt to commit fails. */
public class AutocommitFailedException extends TaskanaRuntimeException {
  private static final long serialVersionUID = 1L;

  public AutocommitFailedException(Throwable cause) {
    super("Autocommit failed", cause);
  }
}
