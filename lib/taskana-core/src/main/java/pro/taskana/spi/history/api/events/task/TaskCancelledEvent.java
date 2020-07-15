package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is cancelled. */
public class TaskCancelledEvent extends TaskHistoryEvent {

  public TaskCancelledEvent(String id, Task task, String userId) {
    super(id, task, userId, null);
    eventType = "TASK_CANCELLED";
    created = task.getCompleted();
  }
}
