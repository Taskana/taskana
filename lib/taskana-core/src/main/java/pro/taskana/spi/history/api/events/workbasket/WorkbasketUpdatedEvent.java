package pro.taskana.spi.history.api.events.workbasket;

import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketUpdatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketUpdatedEvent(String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.UPDATED.getName();
    created = workbasket.getModified();
  }
}
