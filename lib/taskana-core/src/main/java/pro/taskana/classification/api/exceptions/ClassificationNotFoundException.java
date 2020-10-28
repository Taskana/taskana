package pro.taskana.classification.api.exceptions;

import pro.taskana.common.api.exceptions.NotFoundException;

/** Thrown if a specific task is not in the database. */
public class ClassificationNotFoundException extends NotFoundException {

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
