package pro.taskana.task.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskState;

/** A convenience class to represent pairs of task id and task state. */
public class MinimalTaskSummary {

  private String taskId;
  private String externalId;
  private String workbasketId;
  private String classificationId;
  private String owner;
  private TaskState taskState;
  private Instant planned;
  private Instant due;
  private Instant modified;
  private CallbackState callbackState;
  private int manualPriority;

  MinimalTaskSummary() {}

  public Instant getPlanned() {
    return planned != null ? planned.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setPlanned(Instant planned) {
    this.planned = planned != null ? planned.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public Instant getDue() {
    return due != null ? due.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setDue(Instant due) {
    this.due = due != null ? due.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public Instant getModified() {
    return modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setModified(Instant modified) {
    this.modified = modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

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

  public String getClassificationId() {
    return classificationId;
  }

  public void setClassificationId(String classificationId) {
    this.classificationId = classificationId;
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

  public int getManualPriority() {
    return manualPriority;
  }

  public void setManualPriority(int manualPriority) {
    this.manualPriority = manualPriority;
  }

  public boolean isManualPriorityActive() {
    return manualPriority >= 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        taskId,
        externalId,
        workbasketId,
        classificationId,
        owner,
        taskState,
        planned,
        due,
        modified,
        callbackState,
        manualPriority);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MinimalTaskSummary)) {
      return false;
    }
    MinimalTaskSummary other = (MinimalTaskSummary) obj;
    return Objects.equals(taskId, other.taskId)
        && Objects.equals(externalId, other.externalId)
        && Objects.equals(workbasketId, other.workbasketId)
        && Objects.equals(classificationId, other.classificationId)
        && Objects.equals(owner, other.owner)
        && taskState == other.taskState
        && Objects.equals(planned, other.planned)
        && Objects.equals(due, other.due)
        && Objects.equals(modified, other.modified)
        && callbackState == other.callbackState
        && manualPriority == other.manualPriority;
  }

  @Override
  public String toString() {
    return "MinimalTaskSummary [taskId="
        + taskId
        + ", externalId="
        + externalId
        + ", workbasketId="
        + workbasketId
        + ", classificationId="
        + classificationId
        + ", owner="
        + owner
        + ", taskState="
        + taskState
        + ", planned="
        + planned
        + ", due="
        + due
        + ", modified="
        + modified
        + ", callbackState="
        + callbackState
        + ", manualPriority="
        + manualPriority
        + "]";
  }
}
