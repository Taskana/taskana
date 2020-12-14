package pro.taskana.spi.history.api.exceptions;

import pro.taskana.common.api.exceptions.NotFoundException;

public class TaskanaHistoryEventNotFoundException extends NotFoundException {

  public TaskanaHistoryEventNotFoundException(String id, String msg) {
    super(id, msg);
  }
}
