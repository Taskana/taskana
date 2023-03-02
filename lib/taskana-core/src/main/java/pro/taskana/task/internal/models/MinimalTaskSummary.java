package pro.taskana.task.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskState;

/** A convenience class to represent pairs of task id and task state. */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MinimalTaskSummary {

  private String taskId;
  private String externalId;
  private String workbasketId;
  private String classificationId;
  private String owner;
  private TaskState taskState;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant planned;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant due;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant modified;

  private CallbackState callbackState;
  private int manualPriority;

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

  public boolean isManualPriorityActive() {
    return manualPriority >= 0;
  }
}
