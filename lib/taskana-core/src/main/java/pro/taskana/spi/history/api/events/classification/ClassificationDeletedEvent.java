package pro.taskana.spi.history.api.events.classification;

import java.time.Instant;

import pro.taskana.classification.api.models.Classification;

public class ClassificationDeletedEvent extends ClassificationHistoryEvent {

  public ClassificationDeletedEvent(
      String id, Classification classification, String userId, String details) {
    super(id, classification, userId, details);
    eventType = ClassificationHistoryEventType.DELETED.getName();
    created = Instant.now();
  }
}
