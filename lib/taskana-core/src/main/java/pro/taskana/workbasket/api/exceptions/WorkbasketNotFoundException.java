package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.NotFoundException;

/**
 * Thrown if a specific {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbasket} is not
 * in the database.
 */
public class WorkbasketNotFoundException extends NotFoundException {

  private String key;
  private String domain;

  public WorkbasketNotFoundException(String id, String msg) {
    super(id, msg);
  }

  public WorkbasketNotFoundException(String key, String domain, String msg) {
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
