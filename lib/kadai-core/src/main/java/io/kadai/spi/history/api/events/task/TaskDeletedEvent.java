package io.kadai.spi.history.api.events.task;

import io.kadai.task.api.models.TaskSummary;
import java.time.Instant;

public class TaskDeletedEvent extends TaskHistoryEvent {

  public TaskDeletedEvent(String id, TaskSummary taskSummary, String taskId, String userId) {
    super(id, taskSummary, userId, null);
    eventType = TaskHistoryEventType.DELETED.getName();
    created = Instant.now();
    super.taskId = taskId;
  }
}
