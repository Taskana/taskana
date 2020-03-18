package pro.taskana.rest.resource;

import org.springframework.hateoas.ResourceSupport;

import pro.taskana.task.api.models.TaskComment;

/** Resource class for {@link TaskComment}. */
public class TaskCommentResource extends ResourceSupport {

  private String taskCommentId;
  private String taskId;
  private String textField;
  private String creator;
  private String created;
  private String modified;

  public TaskCommentResource() {}

  public TaskCommentResource(TaskComment taskComment) {
    this.taskCommentId = taskComment.getId();
    this.taskId = taskComment.getTaskId();
    this.textField = taskComment.getTextField();
    this.creator = taskComment.getCreator();
    this.created = taskComment.getCreated().toString();
    this.modified = taskComment.getModified().toString();
  }

  public String getTaskCommentId() {
    return taskCommentId;
  }

  public void setTaskCommentId(String id) {
    this.taskCommentId = id;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getTextField() {
    return textField;
  }

  public void setTextField(String textField) {
    this.textField = textField;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  @Override
  public String toString() {
    return "TaskCommentResource [taskCommentId="
        + taskCommentId
        + ", taskId="
        + taskId
        + ", textField="
        + textField
        + ", creator="
        + creator
        + ", created="
        + created
        + ", modified="
        + modified
        + "]";
  }
}
