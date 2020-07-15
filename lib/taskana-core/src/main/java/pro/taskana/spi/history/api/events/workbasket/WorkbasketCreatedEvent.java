package pro.taskana.spi.history.api.events.workbasket;

import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketCreatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketCreatedEvent(String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.WORKBASKET_CREATED.getName();
    created = workbasket.getCreated();
  }
}
