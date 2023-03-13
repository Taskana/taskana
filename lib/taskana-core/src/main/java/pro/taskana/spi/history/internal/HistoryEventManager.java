package pro.taskana.spi.history.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.CheckedConsumer;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

/** Creates and deletes events and emits them to the registered history service providers. */
public final class HistoryEventManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryEventManager.class);
  private final List<TaskanaHistory> taskanaHistories;

  public HistoryEventManager(TaskanaEngine taskanaEngine) {
    taskanaHistories = SpiLoader.load(TaskanaHistory.class);
    for (TaskanaHistory history : taskanaHistories) {
      history.initialize(taskanaEngine);
      LOGGER.info("Registered history provider: {}", history.getClass().getName());
    }
    if (taskanaHistories.isEmpty()) {
      LOGGER.info("No TaskanaHistory provider found. Running without History.");
    }
  }

  public boolean isEnabled() {
    return !taskanaHistories.isEmpty();
  }

  public void createEvent(TaskHistoryEvent event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending event to history service providers: {}", event);
    }
    taskanaHistories.forEach(
        CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void createEvent(WorkbasketHistoryEvent event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending event to history service providers: {}", event);
    }
    taskanaHistories.forEach(
        CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void createEvent(ClassificationHistoryEvent event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending event to history service providers: {}", event);
    }

    taskanaHistories.forEach(
        CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void deleteEvents(List<String> taskIds) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending taskIds to history service providers: {}", taskIds);
    }

    taskanaHistories.forEach(
        CheckedConsumer.wrap(
            historyProvider -> historyProvider.deleteHistoryEventsByTaskIds(taskIds)));
  }
}
