package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;

import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketDistributionTargetRemovedEvent extends WorkbasketHistoryEvent {

  public WorkbasketDistributionTargetRemovedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.DISTRIBUTION_TARGET_REMOVED.getName();
    created = Instant.now();
  }
}
