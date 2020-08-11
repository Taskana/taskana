package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;

import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketAccessItemDeletedEvent extends WorkbasketHistoryEvent {

  public WorkbasketAccessItemDeletedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.ACCESS_ITEM_DELETED.getName();
    created = Instant.now();
  }
}
