package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.Task;

/** Event fired if a task is claimed. */
public class ClaimedEvent extends TaskEvent {

  public ClaimedEvent(Task task) {
    super(task);
    setEventType("TASK_CLAIMED");
    created = task.getClaimed();
  }
}
