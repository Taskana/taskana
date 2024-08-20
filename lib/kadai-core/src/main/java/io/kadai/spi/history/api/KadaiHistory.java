package io.kadai.spi.history.api;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import java.util.List;

/** Interface for KADAI History Service Provider. */
public interface KadaiHistory {

  /**
   * Initialize KadaiHistory service.
   *
   * @param kadaiEngine {@linkplain KadaiEngine} The Kadai engine for needed initialization.
   */
  void initialize(KadaiEngine kadaiEngine);

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
   *     io.kadai.common.api.KadaiRole#ADMIN}
   */
  void deleteHistoryEventsByTaskIds(List<String> taskIds)
      throws InvalidArgumentException, NotAuthorizedException;
}
