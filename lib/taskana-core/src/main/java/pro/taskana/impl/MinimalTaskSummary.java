package pro.taskana.impl;

import pro.taskana.TaskState;

/**
 * A convenience class to represent pairs of task id and task state.
 */
public class MinimalTaskSummary {

    private String taskId;
    private String workbasketId;
    private TaskState taskState;

    MinimalTaskSummary() {

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getWorkbasketId() {
        return workbasketId;
    }

    public void setWorkbasketId(String workbasketKey) {
        this.workbasketId = workbasketKey;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    @Override
    public String toString() {
        return "MinimalTaskSummary [" +
            "taskId= " + this.taskId +
            ", workbasketId= " + this.workbasketId +
            ", taskState= " + this.taskState +
            "]";
    }

}
