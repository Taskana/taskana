package pro.taskana.task.rest.models;

import java.time.Instant;
import org.springframework.hateoas.RepresentationModel;
import pro.taskana.task.api.models.TaskComment;

/** EntityModel class for {@link TaskComment}. */
public class TaskCommentRepresentationModel
    extends RepresentationModel<TaskCommentRepresentationModel> {

  /** Unique Id. */
  private String taskCommentId;

  /** Task Id. Can identify the task the comment belongs to. */
  private String taskId;

  /** The content of the comment. */
  private String textField;

  /** The creator of the task comment. */
  private String creator;

  /** The long name of the task comment creator. */
  private String creatorFullName;

  /** The creation timestamp in the system. */
  private Instant created;

  /** Timestamp of the last task comment modification. */
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

  public String getCreatorFullName() {
    return creatorFullName;
  }

  public void setCreatorFullName(String creatorFullName) {
    this.creatorFullName = creatorFullName;
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
