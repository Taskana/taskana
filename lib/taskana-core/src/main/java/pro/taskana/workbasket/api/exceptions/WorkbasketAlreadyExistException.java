package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.workbasket.api.models.Workbasket;

/** Thrown, when a workbasket does already exits, but wanted to create with same ID. */
public class WorkbasketAlreadyExistException extends TaskanaException {

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
