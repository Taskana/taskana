package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.Task;
import pro.taskana.task.api.TaskSummary;

/** Event fired if a task is completed. */
public class CompletedEvent extends TaskEvent {

  public CompletedEvent(Task completedTask, String userId) {
    super(completedTask, userId);
    eventType = "TASK_COMPLETED";
    created = completedTask.getCompleted();
  }

  public CompletedEvent(TaskSummary completedTask, String userId) {
    super(completedTask, userId);
    eventType = "TASK_COMPLETED";
    created = completedTask.getCompleted();
  }
}
