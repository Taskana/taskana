package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/**
 * This exception is thrown when an already existing {@linkplain WorkbasketAccessItem} was tried to
 * be created.
 */
public class WorkbasketAccessItemAlreadyExistException extends TaskanaException {

  public static final String ERROR_KEY = "WORKBASKET_ACCESS_ITEM_ALREADY_EXISTS";
  private final String accessId;
  private final String workbasketId;

  public WorkbasketAccessItemAlreadyExistException(String accessId, String workbasketId) {
    super(
        String.format(
            "WorkbasketAccessItem with access id '%s' and workbasket id '%s' already exists.",
            accessId, workbasketId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("accessId", accessId, "workbasketId", workbasketId)));
    this.accessId = accessId;
    this.workbasketId = workbasketId;
  }

  public String getAccessId() {
    return accessId;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }
}
