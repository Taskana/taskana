package pro.taskana.classification.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

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
