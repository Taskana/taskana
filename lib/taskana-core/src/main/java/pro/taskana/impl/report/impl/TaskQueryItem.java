package pro.taskana.impl.report.impl;

import pro.taskana.TaskState;
import pro.taskana.impl.report.QueryItem;

/**
 * The TaskQueryItem entity contains the number of tasks for a domain which have a specific state.
 */
public class TaskQueryItem implements QueryItem {

    private String domain;
    private TaskState state;
    private int count;

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    @Override
    public String getKey() {
        return domain;
    }

    @Override
    public int getValue() {
        return count;
    }
}
