package pro.taskana.impl;

import pro.taskana.model.TaskState;

/**
 * A convenience class to represent pairs of task id and task state.
 */
public class MinimalTaskSummary {

    private String taskId;
    private String workbasketKey;
    private TaskState taskState;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getWorkbasketKey() {
        return workbasketKey;
    }

    public void setWorkbasketKey(String workbasketKey) {
        this.workbasketKey = workbasketKey;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

}
