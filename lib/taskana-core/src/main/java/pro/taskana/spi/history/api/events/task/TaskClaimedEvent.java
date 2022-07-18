package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** The TaskClaimedEvent is fired if a task is claimed. */
public class TaskClaimedEvent extends TaskHistoryEvent {

  public TaskClaimedEvent(String id, Task task, String userId, String details) {
    super(id, task, userId, details);
    eventType = (TaskHistoryEventType.CLAIMED.getName());
    created = task.getClaimed();
  }
}
