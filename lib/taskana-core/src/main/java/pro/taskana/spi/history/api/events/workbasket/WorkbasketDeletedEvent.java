package pro.taskana.spi.history.api.events.workbasket;

import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketDeletedEvent extends WorkbasketHistoryEvent {

  public WorkbasketDeletedEvent(String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.WORKBASKET_DELETED.getName();
    created = workbasket.getModified();
  }
}
