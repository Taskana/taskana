package pro.taskana.spi.history.api.events.classification;

import pro.taskana.classification.api.models.Classification;

public class ClassificationCreatedEvent extends ClassificationHistoryEvent {

  public ClassificationCreatedEvent(
      String id, Classification classification, String userId, String details) {
    super(id, classification, userId, details);
    eventType = ClassificationHistoryEventType.CREATED.getName();
    created = classification.getCreated();
  }
}
