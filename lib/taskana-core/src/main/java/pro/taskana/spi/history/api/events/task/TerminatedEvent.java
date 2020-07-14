package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is terminated. */
public class TerminatedEvent extends TaskEvent {

  public TerminatedEvent(String id, Task task, String userId) {
    super(id, task, userId, null);
    eventType = "TASK_TERMINATED";
    created = task.getModified();
  }
}
