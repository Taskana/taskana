package pro.taskana.common.api.exceptions;

/** The common base class for TASKANA's runtime exceptions. */
public class TaskanaRuntimeException extends RuntimeException {

  private final ErrorCode errorCode;

  protected TaskanaRuntimeException(String message, ErrorCode errorCode) {
    this(message, errorCode, null);
  }

  protected TaskanaRuntimeException(String message, ErrorCode errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  @Override
  public String toString() {
    return "TaskanaRuntimeException [errorCode=" + errorCode + "]";
  }
}
