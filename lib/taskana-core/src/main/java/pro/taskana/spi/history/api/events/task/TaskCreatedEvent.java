package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** The TaskCreatedEvent is fired if a task is created. */
public class TaskCreatedEvent extends TaskHistoryEvent {

  public TaskCreatedEvent(String id, Task task, String userId, String details) {
    super(id, task, userId, details);
    eventType = TaskHistoryEventType.CREATED.getName();
    created = task.getCreated();
  }
}
