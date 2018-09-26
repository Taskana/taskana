package pro.taskana.history.api;

/**
 * Interface for TASKANA History Service Provider.
 */
public interface TaskanaHistory {

    /**
     * Create a new history event.
     *
     * @param event
     *            {@link TaskanaHistoryEvent} The event to be created.
     */
    void create(TaskanaHistoryEvent event);

}
