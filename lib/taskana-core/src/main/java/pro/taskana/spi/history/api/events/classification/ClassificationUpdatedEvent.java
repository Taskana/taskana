package pro.taskana.spi.history.api.events.classification;

import pro.taskana.classification.api.models.Classification;

public class ClassificationUpdatedEvent extends ClassificationHistoryEvent {

  public ClassificationUpdatedEvent(
      String id, Classification classification, String userId, String details) {
    super(id, classification, userId, details);
    eventType = ClassificationHistoryEventType.UPDATED.getName();
    created = classification.getModified();
  }
}
