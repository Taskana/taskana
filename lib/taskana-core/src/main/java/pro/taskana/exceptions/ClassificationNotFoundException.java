package pro.taskana.exceptions;

/** Thrown if a specific task is not in the database. */
public class ClassificationNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 1L;
  private String key;
  private String domain;

  public ClassificationNotFoundException(String id, String msg) {
    super(id, msg);
  }

  public ClassificationNotFoundException(String key, String domain, String msg) {
    super(null, msg);
    this.key = key;
    this.domain = domain;
  }

  public String getKey() {
    return key;
  }

  public String getDomain() {
    return domain;
  }
}
