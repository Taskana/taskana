package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.Task;

/** The TaskRequestReviewEvent is fired after a review on a {@linkplain Task} has been requested. */
public class TaskRequestReviewEvent extends TaskHistoryEvent {

  public TaskRequestReviewEvent(String id, Task task, String userId, String details) {
    super(id, task, userId, details);
    eventType = (TaskHistoryEventType.REQUESTED_REVIEW.getName());
    created = task.getModified();
  }
}
