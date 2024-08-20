package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.Task;

/** Event fired if a task is cancelled. */
public class TaskCancelledEvent extends TaskHistoryEvent {

  public TaskCancelledEvent(String id, Task task, String userId) {
    super(id, task, userId, null);
    eventType = TaskHistoryEventType.CANCELLED.getName();
    created = task.getCompleted();
  }
}
