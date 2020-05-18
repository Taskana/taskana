package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is claimed. */
public class ClaimedEvent extends TaskEvent {

  public ClaimedEvent(String id, Task task, String userId) {
    super(id, task, userId, null);
    setEventType("TASK_CLAIMED");
    created = task.getClaimed();
  }
}
