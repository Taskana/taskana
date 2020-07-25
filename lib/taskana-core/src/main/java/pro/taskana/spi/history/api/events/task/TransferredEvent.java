package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Event fired if a task is transferred. */
public class TransferredEvent extends TaskEvent {

  public TransferredEvent(
      String id,
      Task task,
      WorkbasketSummary oldWorkbasket,
      WorkbasketSummary newWorkbasket,
      String userId) {
    super(id, task, userId, null);
    eventType = "TASK_TRANSFERRED";
    created = task.getModified();
    this.oldValue = oldWorkbasket.getId();
    this.newValue = newWorkbasket.getId();
  }
}
