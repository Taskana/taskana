package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is transferred. */
public class TaskTransferredEvent extends TaskHistoryEvent {

  public TaskTransferredEvent(
      String id, Task task, String oldWorkbasketId, String newWorkbasketId, String userId) {
    super(id, task, userId, null);
    eventType = TaskHistoryEventType.TRANSFERRED.getName();
    created = task.getModified();
    this.oldValue = oldWorkbasketId;
    this.newValue = newWorkbasketId;
  }
}
