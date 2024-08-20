package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.Task;

/** Event fired if a task is created. */
public class TaskCreatedEvent extends TaskHistoryEvent {

  public TaskCreatedEvent(String id, Task task, String userId, String details) {
    super(id, task, userId, details);
    eventType = TaskHistoryEventType.CREATED.getName();
    created = task.getCreated();
  }
}
