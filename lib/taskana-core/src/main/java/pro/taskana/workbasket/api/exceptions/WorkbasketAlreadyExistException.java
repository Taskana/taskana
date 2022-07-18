package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * The WorkbasketAlreadyExistException is thrown when an already existing {@linkplain Workbasket}
 * was tried to be created.
 */
public class WorkbasketAlreadyExistException extends TaskanaException {

  public static final String ERROR_KEY = "WORKBASKET_ALREADY_EXISTS";
  private final String key;
  private final String domain;

  public WorkbasketAlreadyExistException(String key, String domain) {
    super(
        String.format("A Workbasket with key '%s' already exists in domain '%s'.", key, domain),
        ErrorCode.of(ERROR_KEY, MapCreator.of("workbasketKey", key, "domain", domain)));
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
