package pro.taskana.history.internal.events.task;

import pro.taskana.task.api.Task;
import pro.taskana.task.api.TaskSummary;

/** Event fired if a task is completed. */
public class CompletedEvent extends TaskEvent {

  public CompletedEvent(Task completedTask) {
    super(completedTask);
    eventType = "TASK_COMPLETED";
    created = completedTask.getCompleted();
  }

  public CompletedEvent(TaskSummary completedTask) {
    super(completedTask);
    eventType = "TASK_COMPLETED";
    created = completedTask.getCompleted();
  }
}
