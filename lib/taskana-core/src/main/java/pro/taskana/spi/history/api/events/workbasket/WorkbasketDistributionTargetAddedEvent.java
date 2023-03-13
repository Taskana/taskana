package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;
import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketDistributionTargetAddedEvent extends WorkbasketHistoryEvent {

  public WorkbasketDistributionTargetAddedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.DISTRIBUTION_TARGET_ADDED.getName();
    created = Instant.now();
  }
}
