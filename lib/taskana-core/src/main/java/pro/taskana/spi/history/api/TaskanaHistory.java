package pro.taskana.spi.history.api;

import java.util.List;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;

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
   * Delete history events by taskIds.
   *
   * @param taskIds the task ids for which all history events must be deleted
   * @throws InvalidArgumentException If the list of taskIds is null
   * @throws NotAuthorizedException If the user has no permission to delete events
   */
  void deleteHistoryEventsByTaskIds(List<String> taskIds)
      throws InvalidArgumentException, NotAuthorizedException;
}
