package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;

import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketDistributionTargetsUpdatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketDistributionTargetsUpdatedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.DISTRIBUTION_TARGETS_UPDATED.getName();
    created = Instant.now();
  }

}
