package pro.taskana.spi.history.internal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.CheckedConsumer;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

/** Creates and deletes events and emits them to the registered history service providers. */
@Slf4j
public final class HistoryEventManager {

  private final List<TaskanaHistory> taskanaHistories;

  public HistoryEventManager(TaskanaEngine taskanaEngine) {
    taskanaHistories = SpiLoader.load(TaskanaHistory.class);
    for (TaskanaHistory history : taskanaHistories) {
      history.initialize(taskanaEngine);
      log.info("Registered history provider: {}", history.getClass().getName());
    }
    if (taskanaHistories.isEmpty()) {
      log.info("No TaskanaHistory provider found. Running without History.");
    }
  }

  public boolean isEnabled() {
    return !taskanaHistories.isEmpty();
  }

  public void createEvent(TaskHistoryEvent event) {
    if (log.isDebugEnabled()) {
      log.debug("Sending event to history service providers: {}", event);
    }
    taskanaHistories.forEach(
        CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void createEvent(WorkbasketHistoryEvent event) {
    if (log.isDebugEnabled()) {
      log.debug("Sending event to history service providers: {}", event);
    }
    taskanaHistories.forEach(
        CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void createEvent(ClassificationHistoryEvent event) {
    if (log.isDebugEnabled()) {
      log.debug("Sending event to history service providers: {}", event);
    }

    taskanaHistories.forEach(
        CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void deleteEvents(List<String> taskIds) {
    if (log.isDebugEnabled()) {
      log.debug("Sending taskIds to history service providers: {}", taskIds);
    }

    taskanaHistories.forEach(
        CheckedConsumer.wrap(
            historyProvider -> historyProvider.deleteHistoryEventsByTaskIds(taskIds)));
  }
}
