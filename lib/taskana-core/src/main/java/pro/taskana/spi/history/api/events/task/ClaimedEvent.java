package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;

/** Event fired if a task is claimed. */
public class ClaimedEvent extends TaskEvent {

  public ClaimedEvent(Task task, String userId) {
    super(task, userId,"");
    setEventType("TASK_CLAIMED");
    created = task.getClaimed();
  }
}
