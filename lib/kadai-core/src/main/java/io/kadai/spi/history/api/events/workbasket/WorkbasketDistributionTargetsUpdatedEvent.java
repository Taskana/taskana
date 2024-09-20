package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;
import java.time.Instant;

public class WorkbasketDistributionTargetsUpdatedEvent extends WorkbasketHistoryEvent {

  public WorkbasketDistributionTargetsUpdatedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.DISTRIBUTION_TARGETS_UPDATED.getName();
    created = Instant.now();
  }
}
