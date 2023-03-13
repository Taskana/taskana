package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;
import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketAccessItemsUpdatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketAccessItemsUpdatedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.ACCESS_ITEMS_UPDATED.getName();
    created = Instant.now();
  }
}
