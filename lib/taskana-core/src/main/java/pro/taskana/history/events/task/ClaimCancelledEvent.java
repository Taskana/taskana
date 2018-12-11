package pro.taskana.history.events.task;

import pro.taskana.Task;

/**
 * Event fired if a task is cancelled to be claimed.
 */
public class ClaimCancelledEvent extends TaskEvent {

    public ClaimCancelledEvent(Task task) {
        super(task);
        eventType = "TASK_CLAIM_CANCELLED";
        created = task.getModified();
    }
}
