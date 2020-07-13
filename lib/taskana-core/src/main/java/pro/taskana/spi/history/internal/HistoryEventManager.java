package pro.taskana.spi.history.internal;

import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;

/**
 * Creates and deletes events and emits them to the registered history service providers.
 */
public final class HistoryEventManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryEventManager.class);
  private static HistoryEventManager singleton;
  private boolean enabled = false;
  private ServiceLoader<TaskanaHistory> serviceLoader;

  private HistoryEventManager(TaskanaEngineConfiguration taskanaEngineConfiguration) {
    serviceLoader = ServiceLoader.load(TaskanaHistory.class);
    for (TaskanaHistory history : serviceLoader) {
      history.initialize(taskanaEngineConfiguration);
      LOGGER.info("Registered history provider: {}", history.getClass().getName());
      enabled = true;
    }
    if (!enabled) {
      LOGGER.info("No history provider found. Running without history.");
    }
  }

  public static synchronized HistoryEventManager getInstance(
      TaskanaEngineConfiguration taskanaEngineConfiguration) {
    if (singleton == null) {
      singleton = new HistoryEventManager(taskanaEngineConfiguration);
    }
    return singleton;
  }

  public static boolean isHistoryEnabled() {
    return Objects.nonNull(singleton) && singleton.enabled;
  }

  public void createEvent(TaskanaHistoryEvent event) {
    LOGGER.debug("Sending event to history service providers: {}", event);
    serviceLoader.forEach(historyProvider -> historyProvider.create(event));
  }

  public void deleteEvents(List<String> taskIds) {
    LOGGER.debug("Sending taskIds to history service providers: {}", taskIds);
    serviceLoader.forEach(historyProvider -> {
      try {
        historyProvider.deleteHistoryEventsByTaskIds(taskIds);
      } catch (InvalidArgumentException | NotAuthorizedException e) {
        LOGGER.warn("Caught an exception while trying to delete HistoryEvents", e);
      }
    });
  }
}
