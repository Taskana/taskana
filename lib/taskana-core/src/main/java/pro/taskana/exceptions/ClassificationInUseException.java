package pro.taskana.exceptions;

/** Thrown if a specific task is not in the database. */
public class ClassificationInUseException extends TaskanaException {

  private static final long serialVersionUID = 1L;

  public ClassificationInUseException(String msg) {
    super(msg);
  }

  public ClassificationInUseException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
