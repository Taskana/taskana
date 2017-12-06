package pro.taskana.model;

/**
 * TaskStateCounter entity.
 */
public class TaskStateCounter {

    private TaskState state;
    private long counter;

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskStateCounter [state=");
        builder.append(state);
        builder.append(", counter=");
        builder.append(counter);
        builder.append("]");
        return builder.toString();
    }
}
