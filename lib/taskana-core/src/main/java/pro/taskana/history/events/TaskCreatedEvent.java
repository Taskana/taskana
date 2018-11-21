package pro.taskana.history.events;

import pro.taskana.Task;
/**
 * Event fired if a task is created.
 */
public class TaskCreatedEvent extends TaskHistoryEvent {

    public TaskCreatedEvent(Task task) {
        super(task);
        type = "TASK_CREATED";
        created = task.getCreated();
    }
}
