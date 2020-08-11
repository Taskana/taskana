package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is terminated. */
public class TaskTerminatedEvent extends TaskHistoryEvent {

  public TaskTerminatedEvent(String id, Task task, String userId) {
    super(id, task, userId, null);
    eventType = TaskHistoryEventType.TERMINATED.getName();
    created = task.getCompleted();
  }
}
