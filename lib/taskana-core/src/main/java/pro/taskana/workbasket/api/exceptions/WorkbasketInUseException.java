package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * This exception is thrown when a specific {@linkplain Workbasket} does have content and is tried
 * to be deleted.
 */
public class WorkbasketInUseException extends TaskanaException {

  public static final String ERROR_KEY = "WORKBASKET_IN_USE";
  private final String workbasketId;

  public WorkbasketInUseException(String workbasketId) {
    super(
        String.format(
            "Workbasket '%s' contains non-completed Tasks and can't be marked for deletion.",
            workbasketId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("workbasketId", workbasketId)));
    this.workbasketId = workbasketId;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }
}
