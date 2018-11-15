package pro.taskana.history.events.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Task;
import pro.taskana.WorkbasketSummary;

/**
 * Event fired if a task is transferred.
 */
public class TransferredEvent extends TaskEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferredEvent.class);

    public TransferredEvent(Task task, WorkbasketSummary oldWorkbasket, WorkbasketSummary newWorkbasket) {
        super(task);
        type = "TASK_TRANSFERRED";
        created = task.getModified();
        this.oldValue = oldWorkbasket.getId();
        this.newValue = newWorkbasket.getId();
    }
}
