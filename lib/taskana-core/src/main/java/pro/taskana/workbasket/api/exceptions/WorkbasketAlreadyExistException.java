package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.workbasket.api.Workbasket;

/** Thrown, when a workbasket does already exits, but wanted to create with same ID. */
public class WorkbasketAlreadyExistException extends TaskanaException {

  private static final long serialVersionUID = 6115013L;

  public WorkbasketAlreadyExistException(Workbasket workbasket) {
    super(
        "ID='"
            + workbasket.getId()
            + "', KEY=' "
            + workbasket.getKey()
            + "', DOMAIN='"
            + workbasket.getDomain()
            + "';");
  }
}
