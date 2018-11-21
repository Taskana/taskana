package pro.taskana.history.events;

import pro.taskana.Task;

/**
 * Event fired if a task is cancelled to be claimed.
 */
public class TaskClaimedCanceledEvent extends TaskHistoryEvent {

    public TaskClaimedCanceledEvent(Task task) {
        super(task);
        type = "TASK_CLAIM_CANCELLED";
        created = task.getModified();
    }
}
