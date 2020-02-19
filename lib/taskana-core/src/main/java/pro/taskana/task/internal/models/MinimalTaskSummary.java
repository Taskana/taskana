package pro.taskana.task.internal.models;

import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskState;

/** A convenience class to represent pairs of task id and task state. */
public class MinimalTaskSummary {

  private String taskId;
  private String externalId;
  private String workbasketId;
  private String owner;
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

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
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
        + ", externalId="
        + externalId
        + ", workbasketId="
        + workbasketId
        + ", owner="
        + owner
        + ", taskState="
        + taskState
        + ", callbackState="
        + callbackState
        + "]";
  }
}
