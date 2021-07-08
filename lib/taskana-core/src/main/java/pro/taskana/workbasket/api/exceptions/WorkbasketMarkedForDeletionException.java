package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * This exception is thrown when a {@linkplain Workbasket}, which was {@linkplain
 * Workbasket#isMarkedForDeletion() marked for deletion}, could not be deleted.
 */
public class WorkbasketMarkedForDeletionException extends TaskanaException {

  public static final String ERROR_KEY = "WORKBASKET_MARKED_FOR_DELETION";
  private final String workbasketId;

  public WorkbasketMarkedForDeletionException(String workbasketId) {
    super(
        String.format(
            "Workbasket with id '%s' could not be deleted, but was marked for deletion.",
            workbasketId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("workbasketId", workbasketId)));
    this.workbasketId = workbasketId;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }
}
