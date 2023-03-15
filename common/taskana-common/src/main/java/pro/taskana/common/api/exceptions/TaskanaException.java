package pro.taskana.common.api.exceptions;

import java.io.Serializable;

/** common base class for TASKANA's checked exceptions. */
public class TaskanaException extends Exception {

  private final ErrorCode errorCode;

  protected TaskanaException(String message, ErrorCode errorCode) {
    this(message, errorCode, null);
  }

  protected TaskanaException(String message, ErrorCode errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  protected static Serializable ensureNullIsHandled(Serializable o) {
    return o == null ? "null" : o;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + " [errorCode="
        + errorCode
        + ", message="
        + getMessage()
        + "]";
  }
}
