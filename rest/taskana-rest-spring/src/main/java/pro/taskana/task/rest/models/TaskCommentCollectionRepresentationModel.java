package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Collection;
import pro.taskana.common.rest.models.CollectionRepresentationModel;

public class TaskCommentCollectionRepresentationModel
    extends CollectionRepresentationModel<TaskCommentRepresentationModel> {

  @ConstructorProperties("taskComments")
  public TaskCommentCollectionRepresentationModel(
      Collection<TaskCommentRepresentationModel> content) {
    super(content);
  }

  @Schema(name = "taskComments", description = "The embedded task comments.")
  @JsonProperty("taskComments")
  @Override
  public Collection<TaskCommentRepresentationModel> getContent() {
    return super.getContent();
  }
}
