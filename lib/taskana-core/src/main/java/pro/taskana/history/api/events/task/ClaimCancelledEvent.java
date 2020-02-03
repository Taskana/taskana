package pro.taskana.history.api.events.task;

import pro.taskana.task.api.Task;

/** Event fired if a task is cancelled to be claimed. */
public class ClaimCancelledEvent extends TaskEvent {

  public ClaimCancelledEvent(Task task) {
    super(task);
    eventType = "TASK_CLAIM_CANCELLED";
    created = task.getModified();
  }
}
