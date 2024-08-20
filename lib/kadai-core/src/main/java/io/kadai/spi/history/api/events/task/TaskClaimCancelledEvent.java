package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.Task;

/** Event fired if a task is cancelled to be claimed. */
public class TaskClaimCancelledEvent extends TaskHistoryEvent {

  public TaskClaimCancelledEvent(String id, Task task, String userId, String details) {
    super(id, task, userId, details);
    eventType = TaskHistoryEventType.CLAIM_CANCELLED.getName();
    created = task.getModified();
  }
}
