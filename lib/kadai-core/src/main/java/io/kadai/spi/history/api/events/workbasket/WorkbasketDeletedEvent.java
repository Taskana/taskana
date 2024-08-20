package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;

public class WorkbasketDeletedEvent extends WorkbasketHistoryEvent {

  public WorkbasketDeletedEvent(String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.DELETED.getName();
    created = workbasket.getModified();
  }
}
