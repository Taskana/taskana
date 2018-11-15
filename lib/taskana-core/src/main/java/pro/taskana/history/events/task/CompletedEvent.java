package pro.taskana.history.events.task;

import pro.taskana.Task;
import pro.taskana.TaskSummary;

/**
 * Event fired if a task is completed.
 */
public class CompletedEvent extends TaskEvent {

    public CompletedEvent(Task completedTask) {
        super(completedTask);
        type = "TASK_COMPLETED";
        created = completedTask.getCompleted();
    }

    public CompletedEvent(TaskSummary completedTask) {
        super(completedTask);
        type = "TASK_COMPLETED";
        created = completedTask.getCompleted();
    }

}
