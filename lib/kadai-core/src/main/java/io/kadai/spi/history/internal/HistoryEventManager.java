package io.kadai.spi.history.internal;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.util.CheckedConsumer;
import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.history.api.KadaiHistory;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Creates and deletes events and emits them to the registered history service providers. */
public final class HistoryEventManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryEventManager.class);
  private final List<KadaiHistory> kadaiHistories;

  public HistoryEventManager(KadaiEngine kadaiEngine) {
    kadaiHistories = SpiLoader.load(KadaiHistory.class);
    for (KadaiHistory history : kadaiHistories) {
      history.initialize(kadaiEngine);
      LOGGER.info("Registered history provider: {}", history.getClass().getName());
    }
    if (kadaiHistories.isEmpty()) {
      LOGGER.info("No KadaiHistory provider found. Running without History.");
    }
  }

  public boolean isEnabled() {
    return !kadaiHistories.isEmpty();
  }

  public void createEvent(TaskHistoryEvent event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending event to history service providers: {}", event);
    }
    kadaiHistories.forEach(CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void createEvent(WorkbasketHistoryEvent event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending event to history service providers: {}", event);
    }
    kadaiHistories.forEach(CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void createEvent(ClassificationHistoryEvent event) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending event to history service providers: {}", event);
    }

    kadaiHistories.forEach(CheckedConsumer.wrap(historyProvider -> historyProvider.create(event)));
  }

  public void deleteEvents(List<String> taskIds) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending taskIds to history service providers: {}", taskIds);
    }

    kadaiHistories.forEach(
        CheckedConsumer.wrap(
            historyProvider -> historyProvider.deleteHistoryEventsByTaskIds(taskIds)));
  }
}
