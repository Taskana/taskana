package io.kadai.workbasket.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.workbasket.api.models.Workbasket;
import java.util.Map;

/**
 * This exception is thrown when an already existing {@linkplain Workbasket} was tried to be
 * created.
 */
public class WorkbasketAlreadyExistException extends KadaiException {

  public static final String ERROR_KEY = "WORKBASKET_ALREADY_EXISTS";
  private final String key;
  private final String domain;

  public WorkbasketAlreadyExistException(String key, String domain) {
    super(
        String.format("A Workbasket with key '%s' already exists in domain '%s'.", key, domain),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("workbasketKey", ensureNullIsHandled(key)),
                Map.entry("domain", ensureNullIsHandled(domain)))));
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
