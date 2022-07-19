package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.NotFoundException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * The WorkbasketNotFoundException is thrown when a specific {@linkplain Workbasket} is not in the
 * database.
 */
public class WorkbasketNotFoundException extends NotFoundException {

  public static final String ERROR_KEY_ID = "WORKBASKET_WITH_ID_NOT_FOUND";
  public static final String ERROR_KEY_KEY_DOMAIN = "WORKBASKET_WITH_KEY_NOT_FOUND";
  private final String id;
  private final String key;
  private final String domain;

  public WorkbasketNotFoundException(String id) {
    super(
        String.format("Workbasket with id '%s' was not found.", id),
        ErrorCode.of(ERROR_KEY_ID, MapCreator.of("workbasketId", id)));
    this.id = id;
    key = null;
    domain = null;
  }

  public WorkbasketNotFoundException(String key, String domain) {
    super(
        String.format("Workbasket with key '%s' and domain '%s' was not found.", key, domain),
        ErrorCode.of(ERROR_KEY_KEY_DOMAIN, MapCreator.of("workbasketKey", key, "domain", domain)));
    id = null;
    this.key = key;
    this.domain = domain;
  }

  public String getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public String getDomain() {
    return domain;
  }
}
