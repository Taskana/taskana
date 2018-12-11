package pro.taskana.history.events.task;

import pro.taskana.Task;
/**
 * Event fired if a task is claimed.
 */
public class ClaimedEvent extends TaskEvent {

    public ClaimedEvent(Task task) {
        super(task);
        setEventType("TASK_CLAIMED");
        created = task.getClaimed();
    }
}
