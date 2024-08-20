package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;
import java.time.Instant;

public class WorkbasketAccessItemUpdatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketAccessItemUpdatedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.ACCESS_ITEM_UPDATED.getName();
    created = Instant.now();
  }
}
