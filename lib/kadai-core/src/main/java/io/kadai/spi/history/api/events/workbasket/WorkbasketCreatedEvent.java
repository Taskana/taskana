package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;

public class WorkbasketCreatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketCreatedEvent(String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.CREATED.getName();
    created = workbasket.getCreated();
  }
}
