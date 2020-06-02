package pro.taskana.task.rest.models;

import org.springframework.hateoas.RepresentationModel;

import pro.taskana.task.api.models.TaskComment;

/** EntityModel class for {@link TaskComment}. */
public class TaskCommentRepresentationModel
    extends RepresentationModel<TaskCommentRepresentationModel> {

  private String taskCommentId;
  private String taskId;
  private String textField;
  private String creator;
  private String created;
  private String modified;

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

}
