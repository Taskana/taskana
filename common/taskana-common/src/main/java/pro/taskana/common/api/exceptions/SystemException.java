package pro.taskana.common.api.exceptions;

/** This exception is thrown when a generic taskana problem is encountered. */
public class SystemException extends TaskanaRuntimeException {

  public SystemException(String msg) {
    super(msg);
  }

  public SystemException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
