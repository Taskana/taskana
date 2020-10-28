package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

public class WorkbasketAccessItemAlreadyExistException extends TaskanaException {

  public WorkbasketAccessItemAlreadyExistException(WorkbasketAccessItem accessItem) {
    super(
        String.format(
            "WorkbasketAccessItem for accessId '%s' "
                + "and WorkbasketId '%s', WorkbasketKey '%s' exists already.",
            accessItem.getAccessId(), accessItem.getWorkbasketId(), accessItem.getWorkbasketKey()));
  }
}
