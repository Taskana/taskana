package pro.taskana.spi.history.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.NotFoundException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/**
 * This exception is thrown when the {@linkplain TaskHistoryEvent} with the specified {@linkplain
 * TaskHistoryEvent#getId() id} was not found.
 */
public class TaskanaHistoryEventNotFoundException extends NotFoundException {

  public static final String ERROR_KEY = "HISTORY_EVENT_NOT_FOUND";
  private final String historyEventId;

  public TaskanaHistoryEventNotFoundException(String historyEventId) {
    super(
        String.format("TaskHistoryEvent with id '%s' was not found", historyEventId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("historyEventId", historyEventId)));
    this.historyEventId = historyEventId;
  }

  public String getHistoryEventId() {
    return historyEventId;
  }
}
