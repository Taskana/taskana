package pro.taskana.common.api.exceptions;

/** This exception is thrown when a generic TASKANA problem is encountered. */
public class SystemException extends TaskanaRuntimeException {

  public static final String ERROR_KEY = "CRITICAL_SYSTEM_ERROR";

  public SystemException(String msg) {
    this(msg, null);
  }

  public SystemException(String msg, Throwable cause) {
    super(msg, ErrorCode.of(ERROR_KEY), cause);
  }

}
