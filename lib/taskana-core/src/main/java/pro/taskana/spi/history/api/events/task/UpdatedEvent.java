package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

public class UpdatedEvent extends TaskEvent {

  public UpdatedEvent(String id, Task updatedTask, String userId, String details) {
    super(id, updatedTask, userId, details);
    eventType = "TASK_UPDATED";
    created = updatedTask.getModified();
  }
}
