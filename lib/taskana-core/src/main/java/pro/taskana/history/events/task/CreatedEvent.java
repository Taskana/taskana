package pro.taskana.history.events.task;

import pro.taskana.Task;
/** Event fired if a task is created. */
public class CreatedEvent extends TaskEvent {

  public CreatedEvent(Task task) {
    super(task);
    eventType = "TASK_CREATED";
    created = task.getCreated();
  }
}
