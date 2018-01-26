package pro.taskana.model;

import java.time.Instant;

/**
 * DueWorkbasketCounter entity.
 */
public class DueWorkbasketCounter {

    private Instant due;
    private String workbasketId;
    private long taskCounter;

    public Instant getDue() {
        return due;
    }

    public void setDue(Instant due) {
        this.due = due;
    }

    public String getWorkbasketId() {
        return workbasketId;
    }

    public void setWorkbasketId(String workbasketId) {
        this.workbasketId = workbasketId;
    }

    public long getTaskCounter() {
        return taskCounter;
    }

    public void setTaskCounter(long taskCounter) {
        this.taskCounter = taskCounter;
    }
}
