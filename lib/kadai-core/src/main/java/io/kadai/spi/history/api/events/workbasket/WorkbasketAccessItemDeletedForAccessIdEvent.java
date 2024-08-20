package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;
import java.time.Instant;

public class WorkbasketAccessItemDeletedForAccessIdEvent extends WorkbasketHistoryEvent {

  public WorkbasketAccessItemDeletedForAccessIdEvent(
      String id, Workbasket workbasket, String userId) {
    super(id, workbasket, userId, null);
    eventType = WorkbasketHistoryEventType.ACCESS_ITEM_DELETED_FOR_ACCESS_ID.getName();
    created = Instant.now();
  }
}
