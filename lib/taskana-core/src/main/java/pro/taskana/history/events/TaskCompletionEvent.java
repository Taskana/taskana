package pro.taskana.history.events;

import pro.taskana.Task;

/**
 * Event fired if a task is completed.
 */
public class TaskCompletionEvent extends TaskHistoryEvent {

    public TaskCompletionEvent(Task completedTask) {
        super(completedTask);
        type = "TASK_COMPLETED";
        created = completedTask.getCompleted();
    }

}
