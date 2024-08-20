package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.TaskSummary;

/** Event fired if a task is transferred. */
public class TaskTransferredEvent extends TaskHistoryEvent {

  public TaskTransferredEvent(
      String id,
      TaskSummary task,
      String oldWorkbasketId,
      String newWorkbasketId,
      String userId,
      String details) {
    super(id, task, userId, details);
    eventType = TaskHistoryEventType.TRANSFERRED.getName();
    created = task.getModified();
    this.oldValue = oldWorkbasketId;
    this.newValue = newWorkbasketId;
  }
}
