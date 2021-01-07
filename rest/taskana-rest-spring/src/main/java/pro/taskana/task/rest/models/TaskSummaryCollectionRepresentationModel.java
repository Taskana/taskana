package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;

import pro.taskana.common.rest.models.CollectionRepresentationModel;

public class TaskSummaryCollectionRepresentationModel
    extends CollectionRepresentationModel<TaskSummaryRepresentationModel> {

  @ConstructorProperties("tasks")
  public TaskSummaryCollectionRepresentationModel(
      Collection<TaskSummaryRepresentationModel> content) {
    super(content);
  }

  /** The embedded tasks. */
  @JsonProperty("tasks")
  @Override
  public Collection<TaskSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
