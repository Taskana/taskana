package pro.taskana.exceptions;

import pro.taskana.WorkbasketAccessItem;

public class WorkbasketAccessItemAlreadyExistException extends TaskanaException {
  private static final long serialVersionUID = 4716611657569005013L;

  public WorkbasketAccessItemAlreadyExistException(WorkbasketAccessItem workbasketAccessItem) {
    super(
        "WorkbasketAccessItem for accessId "
            + workbasketAccessItem.getAccessId()
            + " and WorkbasketId "
            + workbasketAccessItem.getWorkbasketId()
            + ", WorkbasketKey "
            + workbasketAccessItem.getWorkbasketKey()
            + " exists already.");
  }
}
