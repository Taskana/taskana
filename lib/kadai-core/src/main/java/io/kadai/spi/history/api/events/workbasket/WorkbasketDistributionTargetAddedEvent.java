package io.kadai.spi.history.api.events.workbasket;

import io.kadai.workbasket.api.models.Workbasket;
import java.time.Instant;

public class WorkbasketDistributionTargetAddedEvent extends WorkbasketHistoryEvent {

  public WorkbasketDistributionTargetAddedEvent(
      String id, Workbasket workbasket, String userId, String details) {
    super(id, workbasket, userId, details);
    eventType = WorkbasketHistoryEventType.DISTRIBUTION_TARGET_ADDED.getName();
    created = Instant.now();
  }
}
