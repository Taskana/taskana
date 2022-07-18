package pro.taskana.common.api.exceptions;

/** The NotAuthorizedException is thrown when a user is not authorized. */
public class NotAuthorizedException extends TaskanaException {

  protected NotAuthorizedException(String msg, ErrorCode errorCode) {
    super(msg, errorCode);
  }
}
