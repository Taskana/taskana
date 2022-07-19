package pro.taskana.common.api.exceptions;

/** The TaskanaException is the common base class for TASKANA's checked exceptions. */
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

  @Override
  public String toString() {
    return "TaskanaException [errorCode=" + errorCode + ", message=" + getMessage() + "]";
  }
}
