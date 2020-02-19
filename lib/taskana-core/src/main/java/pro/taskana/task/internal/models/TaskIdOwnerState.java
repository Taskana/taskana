package pro.taskana.task.internal.models;

import pro.taskana.task.api.TaskState;

public class TaskIdOwnerState {
  private String taskId;
  private String owner;
  private TaskState taskState;

  TaskIdOwnerState() {}

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
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

  @Override
  public String toString() {
    return "TaskIdOwnerState [taskId="
               + taskId
               + ", owner="
               + owner
               + ", taskState="
               + taskState
               + "]";
  }
}
