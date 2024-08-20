package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.Task;

public class TaskUpdatedEvent extends TaskHistoryEvent {

  public TaskUpdatedEvent(String id, Task updatedTask, String userId, String details) {
    super(id, updatedTask, userId, details);
    eventType = TaskHistoryEventType.UPDATED.getName();
    created = updatedTask.getModified();
  }
}
