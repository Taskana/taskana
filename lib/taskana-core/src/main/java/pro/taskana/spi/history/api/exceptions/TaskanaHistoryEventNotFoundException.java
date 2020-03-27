package pro.taskana.spi.history.api.exceptions;

import pro.taskana.common.api.exceptions.NotFoundException;

public class TaskanaHistoryEventNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 1L;

  public TaskanaHistoryEventNotFoundException(String id, String msg) {
    super(id, msg);
  }
}
