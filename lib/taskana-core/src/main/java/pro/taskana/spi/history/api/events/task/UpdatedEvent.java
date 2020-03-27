package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

public class UpdatedEvent extends TaskEvent {

  public UpdatedEvent(Task updatedTask, String userId, String details) {
    super(updatedTask, userId, details);
    eventType = "TASK_UPDATED";
    created = updatedTask.getModified();
  }

}
