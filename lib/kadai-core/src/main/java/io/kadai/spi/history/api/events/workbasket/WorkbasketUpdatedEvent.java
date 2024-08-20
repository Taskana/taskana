package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;

public class WorkbasketUpdatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketUpdatedEvent(String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.UPDATED.getName();
    created = workbasket.getModified();
  }
}
