package io.kadai.spi.history.api.events.classification;

import io.kadai.classification.api.models.Classification;
import java.time.Instant;

public class ClassificationDeletedEvent extends ClassificationHistoryEvent {

  public ClassificationDeletedEvent(
      String id, Classification classification, String userId, String details) {
    super(id, classification, userId, details);
    eventType = ClassificationHistoryEventType.DELETED.getName();
    created = Instant.now();
  }
}
