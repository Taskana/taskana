package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** The TaskCancelledEvent is fired if a task is cancelled. */
public class TaskCancelledEvent extends TaskHistoryEvent {

  public TaskCancelledEvent(String id, Task task, String userId) {
    super(id, task, userId, null);
    eventType = TaskHistoryEventType.CANCELLED.getName();
    created = task.getCompleted();
  }
}
