package pro.taskana.spi.history.api;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;

/** Interface for TASKANA History Service Provider. */
public interface TaskanaHistory {

  /**
   * Initialize TaskanaHistory service.
   *
   * @param taskanaEngineConfiguration {@link TaskanaEngineConfiguration} The Taskana engine
   *     configuration for needed initialization.
   */
  void initialize(TaskanaEngineConfiguration taskanaEngineConfiguration);

  /**
   * Create a new history event.
   *
   * @param event {@link TaskanaHistoryEvent} The event to be created.
   */
  void create(TaskanaHistoryEvent event);

  /**
   * Get the details of a history event by Id.
   *
   * @param historyEventId the id of the history event
   * @return the history event
   * @throws TaskanaHistoryEventNotFoundException If the Id rfers to a not existing history event
   */
  TaskanaHistoryEvent getHistoryEvent(String historyEventId)
      throws TaskanaHistoryEventNotFoundException;
}
