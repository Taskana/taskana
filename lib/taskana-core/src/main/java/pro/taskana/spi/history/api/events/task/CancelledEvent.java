package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is cancelled. */
public class CancelledEvent extends TaskEvent {

  public CancelledEvent(String id, Task task, String userId) {
    super(id, task, userId, null);
    eventType = "TASK_CANCELLED";
    created = task.getCompleted();
  }
}
