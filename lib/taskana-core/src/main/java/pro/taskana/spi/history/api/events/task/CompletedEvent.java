package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/** Event fired if a task is completed. */
public class CompletedEvent extends TaskEvent {

  public CompletedEvent(Task completedTask, String userId) {
    super(completedTask, userId, null);
    eventType = "TASK_COMPLETED";
    created = completedTask.getCompleted();
  }

  public CompletedEvent(TaskSummary completedTask, String userId) {
    super(completedTask, userId, null);
    eventType = "TASK_COMPLETED";
    created = completedTask.getCompleted();
  }
}
