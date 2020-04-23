package pro.taskana.spi.history.api.events.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Event fired if a task is transferred. */
public class TransferredEvent extends TaskEvent {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferredEvent.class);

  public TransferredEvent(
      Task task, WorkbasketSummary oldWorkbasket, WorkbasketSummary newWorkbasket, String userId) {
    super(task, userId, null);
    eventType = "TASK_TRANSFERRED";
    created = task.getModified();
    this.oldValue = oldWorkbasket.getId();
    this.newValue = newWorkbasket.getId();
  }
}
