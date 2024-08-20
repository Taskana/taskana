package io.kadai.spi.history.api.events.classification;

import io.kadai.classification.api.models.Classification;

public class ClassificationUpdatedEvent extends ClassificationHistoryEvent {

  public ClassificationUpdatedEvent(
      String id, Classification classification, String userId, String details) {
    super(id, classification, userId, details);
    eventType = ClassificationHistoryEventType.UPDATED.getName();
    created = classification.getModified();
  }
}
