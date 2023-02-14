package pro.taskana.task.rest.models;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.task.api.models.TaskComment;

/** EntityModel class for {@link TaskComment}. */
@Getter
@Setter
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
}
