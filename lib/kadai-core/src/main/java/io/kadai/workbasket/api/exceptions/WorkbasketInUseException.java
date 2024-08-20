package io.kadai.workbasket.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.workbasket.api.models.Workbasket;
import java.util.Map;

/**
 * This exception is thrown when a specific {@linkplain Workbasket} does have content and is tried
 * to be deleted.
 */
public class WorkbasketInUseException extends KadaiException {

  public static final String ERROR_KEY = "WORKBASKET_IN_USE";
  private final String workbasketId;

  public WorkbasketInUseException(String workbasketId) {
    super(
        String.format(
            "Workbasket '%s' contains non-completed Tasks and can't be marked for deletion.",
            workbasketId),
        ErrorCode.of(ERROR_KEY, Map.of("workbasketId", ensureNullIsHandled(workbasketId))));
    this.workbasketId = workbasketId;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }
}
