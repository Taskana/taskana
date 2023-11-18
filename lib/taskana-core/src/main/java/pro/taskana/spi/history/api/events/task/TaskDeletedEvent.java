package pro.taskana.spi.history.api.events.task;

import java.time.Instant;
import pro.taskana.task.api.models.TaskSummary;

public class TaskDeletedEvent extends TaskHistoryEvent {

  public TaskDeletedEvent(
      String id,
      TaskSummary taskSummary,
      String taskId,
      String userId) {
    super(id, taskSummary, userId, null);
    eventType = TaskHistoryEventType.DELETED.getName();
    created = Instant.now();
    super.taskId = taskId;
  }
}
