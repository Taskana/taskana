package io.kadai.workbasket.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.workbasket.api.models.Workbasket;
import java.util.Map;

/**
 * This exception is thrown when a {@linkplain Workbasket}, which was {@linkplain
 * Workbasket#isMarkedForDeletion() marked for deletion}, could not be deleted.
 */
public class WorkbasketMarkedForDeletionException extends KadaiException {

  public static final String ERROR_KEY = "WORKBASKET_MARKED_FOR_DELETION";
  private final String workbasketId;

  public WorkbasketMarkedForDeletionException(String workbasketId) {
    super(
        String.format(
            "Workbasket with id '%s' could not be deleted, but was marked for deletion.",
            workbasketId),
        ErrorCode.of(ERROR_KEY, Map.of("workbasketId", ensureNullIsHandled(workbasketId))));
    this.workbasketId = workbasketId;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }
}
