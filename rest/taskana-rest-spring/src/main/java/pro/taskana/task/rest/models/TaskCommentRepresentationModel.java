package pro.taskana.task.rest.models;

import java.time.Instant;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.task.api.models.TaskComment;

/** EntityModel class for {@link TaskComment}. */
public class TaskCommentRepresentationModel
    extends RepresentationModel<TaskCommentRepresentationModel> {

  /** Unique ID. */
  private String taskCommentId;
  /** Task ID. Can identify the task to which the comment belongs. */
  private String taskId;
  /** The content of the comment. */
  private String textField;
  /** The creator of the task comment. */
  private String creator;
  /** The creation timestamp of the task comment in the system. */
  private Instant created;
  /** Timestamp of the last modification of the task comment. */
  private Instant modified;

  public String getTaskCommentId() {
    return taskCommentId;
  }

  public void setTaskCommentId(String taskCommentId) {
    this.taskCommentId = taskCommentId;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getTextField() {
    return textField;
  }

  public void setTextField(String textField) {
    this.textField = textField;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }
}
