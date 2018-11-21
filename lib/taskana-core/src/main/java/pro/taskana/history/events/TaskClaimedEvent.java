package pro.taskana.history.events;

import pro.taskana.Task;
/**
 * Event fired if a task is claimed.
 */
public class TaskClaimedEvent extends TaskHistoryEvent {

    public TaskClaimedEvent(Task task) {
        super(task);
        setType("TASK_CLAIMED");
        created = task.getClaimed();
    }
}
