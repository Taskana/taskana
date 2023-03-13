package pro.taskana.spi.history.api;

import java.util.List;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

/** Interface for TASKANA History Service Provider. */
public interface TaskanaHistory {

  /**
   * Initialize TaskanaHistory service.
   *
   * @param taskanaEngine {@linkplain TaskanaEngine} The Taskana engine for needed initialization.
   */
  void initialize(TaskanaEngine taskanaEngine);

  /**
   * Create a new {@linkplain TaskHistoryEvent}.
   *
   * @param event {@linkplain TaskHistoryEvent} The event to be created.
   */
  void create(TaskHistoryEvent event);

  /**
   * Create a new {@linkplain WorkbasketHistoryEvent}.
   *
   * @param event {@linkplain WorkbasketHistoryEvent} The event to be created.
   */
  void create(WorkbasketHistoryEvent event);

  /**
   * Create a new {@linkplain ClassificationHistoryEvent}.
   *
   * @param event {@linkplain ClassificationHistoryEvent} The event to be created.
   */
  void create(ClassificationHistoryEvent event);

  /**
   * Delete history events by taskIds. Invalid/non-existing taskIds will be ignored
   *
   * @param taskIds the task ids for which all history events must be deleted
   * @throws InvalidArgumentException If the list of taskIds is null
   * @throws NotAuthorizedException if the current user is not member of {@linkplain
   *     TaskanaRole#ADMIN}
   */
  void deleteHistoryEventsByTaskIds(List<String> taskIds)
      throws InvalidArgumentException, NotAuthorizedException;
}
