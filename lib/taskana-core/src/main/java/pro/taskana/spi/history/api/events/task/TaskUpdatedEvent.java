package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

public class TaskUpdatedEvent extends TaskHistoryEvent {

  public TaskUpdatedEvent(String id, Task updatedTask, String userId, String details) {
    super(id, updatedTask, userId, details);
    eventType = TaskHistoryEventType.TASK_UPDATED.getName();
    created = updatedTask.getModified();
  }
}
