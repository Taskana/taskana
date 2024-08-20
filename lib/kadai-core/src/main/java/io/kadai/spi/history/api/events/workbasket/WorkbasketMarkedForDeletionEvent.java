package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;

public class WorkbasketMarkedForDeletionEvent extends WorkbasketHistoryEvent {

  public WorkbasketMarkedForDeletionEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.MARKED_FOR_DELETION.getName();
    created = workbasket.getModified();
  }
}
