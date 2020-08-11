package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is claimed. */
public class TaskClaimedEvent extends TaskHistoryEvent {

  public TaskClaimedEvent(String id, Task task, String userId) {
    super(id, task, userId, null);
    eventType = (TaskHistoryEventType.CLAIMED.getName());
    created = task.getClaimed();
  }
}
