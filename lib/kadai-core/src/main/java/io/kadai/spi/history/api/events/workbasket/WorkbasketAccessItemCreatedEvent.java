package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;
import java.time.Instant;

public class WorkbasketAccessItemCreatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketAccessItemCreatedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.ACCESS_ITEM_CREATED.getName();
    created = Instant.now();
  }
}
