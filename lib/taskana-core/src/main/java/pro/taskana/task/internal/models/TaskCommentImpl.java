package pro.taskana.task.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.task.api.models.TaskComment;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TaskCommentImpl implements TaskComment {

  private String id;
  private String taskId;
  private String textField;
  private String creator;
  private String creatorFullName;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant created;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant modified;

  public TaskCommentImpl(TaskCommentImpl copyFrom) {
    taskId = copyFrom.taskId;
    textField = copyFrom.textField;
    creator = copyFrom.creator;
    created = copyFrom.created;
    modified = copyFrom.modified;
  }

  @Override
  public Instant getCreated() {
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getModified() {
    return modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setModified(Instant modified) {
    this.modified = modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public TaskCommentImpl copy() {
    return new TaskCommentImpl(this);
  }
}
