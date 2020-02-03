package pro.taskana.task.internal;

import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskState;

/** A convenience class to represent pairs of task id and task state. */
public class MinimalTaskSummary {

  private String taskId;
  private String externalId;
  private String workbasketId;
  private TaskState taskState;
  private CallbackState callbackState;

  MinimalTaskSummary() {}

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
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

  public CallbackState getCallbackState() {
    return callbackState;
  }

  public void setCallbackState(CallbackState callbackState) {
    this.callbackState = callbackState;
  }

  @Override
  public String toString() {
    return "MinimalTaskSummary [taskId="
        + taskId
        + ", workbasketId="
        + workbasketId
        + ", taskState="
        + taskState
        + "]";
  }
}
