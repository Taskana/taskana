package pro.taskana.common.api.exceptions;

/** This exception is used to communicate a not authorized user. */
public class NotAuthorizedException extends TaskanaException {

  private static final long serialVersionUID = 21235L;
  private final String currentUserId;

  public NotAuthorizedException(String msg, String currentUserId) {
    super(msg + " - [CURRENT USER: {'" + currentUserId + "'}]");
    this.currentUserId = currentUserId;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }
}
