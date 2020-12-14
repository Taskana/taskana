package pro.taskana.common.api.exceptions;

/** This exception will be thrown if a specific object is not in the database. */
public class NotFoundException extends TaskanaException {

  private final String id;

  public NotFoundException(String id, String message) {
    super(message);
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
