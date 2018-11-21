package pro.taskana.history.events;

import pro.taskana.Task;
/**
 * Event fired if a task is transferred.
 */
public class TaskTransferredEvent extends TaskHistoryEvent {


    public TaskTransferredEvent(Task task, String previousWorkbasketId, String newWorkbasketId) {
        super(task);
        type = "TASK_TRANSFERRED";
        created = task.getModified();
        this.oldValue = previousWorkbasketId;
        this.newValue = newWorkbasketId;
    }
}
