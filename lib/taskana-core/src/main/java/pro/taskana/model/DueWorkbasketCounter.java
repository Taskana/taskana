package pro.taskana.model;

import java.sql.Date;

/**
 * DueWorkbasketCounter entity.
 */
public class DueWorkbasketCounter {

    private Date due;
    private String workbasketId;
    private long taskCounter;

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
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
