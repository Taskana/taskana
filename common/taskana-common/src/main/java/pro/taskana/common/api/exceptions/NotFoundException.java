package pro.taskana.common.api.exceptions;

/** This exception is thrown when a specific object is not in the database. */
public class NotFoundException extends TaskanaException {

  protected NotFoundException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}
