package pro.taskana.history.api;

import pro.taskana.TaskanaEngineConfiguration;

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
}
