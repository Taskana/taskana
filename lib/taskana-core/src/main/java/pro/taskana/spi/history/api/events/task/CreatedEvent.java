package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is created. */
public class CreatedEvent extends TaskEvent {

  public CreatedEvent(Task task, String userId, String details) {
    super(task, userId, details);
    eventType = "TASK_CREATED";
    created = task.getCreated();
  }
}
