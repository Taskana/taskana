package org.taskana.model;

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
}
