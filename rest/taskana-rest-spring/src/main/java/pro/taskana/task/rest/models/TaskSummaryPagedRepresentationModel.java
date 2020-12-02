package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;

import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.common.rest.models.PagedRepresentationModel;

public class TaskSummaryPagedRepresentationModel
    extends PagedRepresentationModel<TaskSummaryRepresentationModel> {

  @ConstructorProperties({"tasks", "page"})
  public TaskSummaryPagedRepresentationModel(
      Collection<TaskSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** The embedded tasks. */
  @JsonProperty("tasks")
  @Override
  public Collection<TaskSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
