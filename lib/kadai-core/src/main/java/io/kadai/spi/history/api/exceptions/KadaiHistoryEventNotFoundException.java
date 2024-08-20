package io.kadai.spi.history.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import java.util.Map;

/**
 * This exception is thrown when the {@linkplain TaskHistoryEvent} with the specified {@linkplain
 * TaskHistoryEvent#getId() id} was not found.
 */
public class KadaiHistoryEventNotFoundException extends KadaiException {

  public static final String ERROR_KEY = "HISTORY_EVENT_NOT_FOUND";
  private final String historyEventId;

  public KadaiHistoryEventNotFoundException(String historyEventId) {
    super(
        String.format("TaskHistoryEvent with id '%s' was not found", historyEventId),
        ErrorCode.of(ERROR_KEY, Map.of("historyEventId", ensureNullIsHandled(historyEventId))));
    this.historyEventId = historyEventId;
  }

  public String getHistoryEventId() {
    return historyEventId;
  }
}
