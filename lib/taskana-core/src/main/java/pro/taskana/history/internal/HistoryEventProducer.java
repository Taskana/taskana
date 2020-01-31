package pro.taskana.history.internal;

import java.util.Objects;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.history.api.TaskanaHistory;
import pro.taskana.history.api.TaskanaHistoryEvent;

/** Creates events and emits them to the registered history service providers. */
public final class HistoryEventProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryEventProducer.class);
  private static HistoryEventProducer singleton;
  private boolean enabled = false;
  private ServiceLoader<TaskanaHistory> serviceLoader;

  private HistoryEventProducer(TaskanaEngineConfiguration taskanaEngineConfiguration) {
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

  public static synchronized HistoryEventProducer getInstance(
      TaskanaEngineConfiguration taskanaEngineConfiguration) {
    if (singleton == null) {
      singleton = new HistoryEventProducer(taskanaEngineConfiguration);
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
}
