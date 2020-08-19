package pro.taskana.spi.history.internal;

import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

/** Creates and deletes events and emits them to the registered history service providers. */
public final class HistoryEventManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryEventManager.class);
  private static HistoryEventManager singleton;
  private boolean enabled = false;
  private ServiceLoader<TaskanaHistory> serviceLoader;

  private HistoryEventManager(TaskanaEngine taskanaEngine) {
    serviceLoader = ServiceLoader.load(TaskanaHistory.class);
    for (TaskanaHistory history : serviceLoader) {
      history.initialize(taskanaEngine);
      LOGGER.info("Registered history provider: {}", history.getClass().getName());
      enabled = true;
    }
    if (!enabled) {
      LOGGER.info("No history provider found. Running without history.");
    }
  }

  public static synchronized HistoryEventManager getInstance(TaskanaEngine taskanaEngine) {
    if (singleton == null) {
      singleton = new HistoryEventManager(taskanaEngine);
    }
    return singleton;
  }

  public static boolean isHistoryEnabled() {
    return Objects.nonNull(singleton) && singleton.enabled;
  }

  public void createEvent(TaskHistoryEvent event) {
    LOGGER.debug("Sending event to history service providers: {}", event);
    serviceLoader.forEach(historyProvider -> historyProvider.create(event));
  }

  public void createEvent(WorkbasketHistoryEvent event) {
    LOGGER.debug("Sending event to history service providers: {}", event);
    serviceLoader.forEach(historyProvider -> historyProvider.create(event));
  }

  public void deleteEvents(List<String> taskIds) {
    LOGGER.debug("Sending taskIds to history service providers: {}", taskIds);
    serviceLoader.forEach(
        historyProvider -> {
          try {
            historyProvider.deleteHistoryEventsByTaskIds(taskIds);
          } catch (InvalidArgumentException | NotAuthorizedException e) {
            LOGGER.warn("Caught an exception while trying to delete HistoryEvents", e);
          }
        });
  }
}
