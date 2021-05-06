package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.workbasket.api.models.Workbasket;

/** Thrown if a {@linkplain Workbasket} does already exits, but wanted to create with same ID. */
public class WorkbasketAlreadyExistException extends TaskanaException {

  public WorkbasketAlreadyExistException(Workbasket workbasket) {
    super(
        "A workbasket with key '"
            + workbasket.getKey()
            + "' already exists in domain '"
            + workbasket.getDomain()
            + "'.");
  }
}
