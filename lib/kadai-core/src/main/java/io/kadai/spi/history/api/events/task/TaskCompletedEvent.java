package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;

/** Event fired if a task is completed. */
public class TaskCompletedEvent extends TaskHistoryEvent {

  public TaskCompletedEvent(String id, Task completedTask, String userId) {
    super(id, completedTask, userId, null);
    eventType = TaskHistoryEventType.COMPLETED.getName();
    created = completedTask.getCompleted();
  }

  public TaskCompletedEvent(String id, TaskSummary completedTask, String userId) {
    super(id, completedTask, userId, null);
    eventType = TaskHistoryEventType.COMPLETED.getName();
    created = completedTask.getCompleted();
  }
}
