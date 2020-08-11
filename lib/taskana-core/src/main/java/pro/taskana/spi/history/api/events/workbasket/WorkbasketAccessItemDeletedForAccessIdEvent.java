package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;

import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketAccessItemDeletedForAccessIdEvent extends WorkbasketHistoryEvent {

  public WorkbasketAccessItemDeletedForAccessIdEvent(
      String id, Workbasket workbasket, String userId) {
    super(id, workbasket, userId, null);
    eventType = WorkbasketHistoryEventType.ACCESS_ITEM_DELETED_FOR_ACCESS_ID.getName();
    created = Instant.now();
  }
}
