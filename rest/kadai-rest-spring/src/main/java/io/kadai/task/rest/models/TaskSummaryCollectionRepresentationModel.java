package io.kadai.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class TaskSummaryCollectionRepresentationModel
    extends CollectionRepresentationModel<TaskSummaryRepresentationModel> {

  @ConstructorProperties("tasks")
  public TaskSummaryCollectionRepresentationModel(
      Collection<TaskSummaryRepresentationModel> content) {
    super(content);
  }

  @Schema(name = "tasks", description = "The embedded tasks.")
  @JsonProperty("tasks")
  @Override
  public Collection<TaskSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
