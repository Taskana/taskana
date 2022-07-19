package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.TaskSummary;

/** The TaskTransferredEvent is fired if a task is transferred. */
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
