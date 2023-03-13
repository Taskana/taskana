package pro.taskana.simplehistory.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;
import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.common.rest.models.PagedRepresentationModel;

public class TaskHistoryEventPagedRepresentationModel
    extends PagedRepresentationModel<TaskHistoryEventRepresentationModel> {

  @ConstructorProperties({"taskHistoryEvents", "page"})
  public TaskHistoryEventPagedRepresentationModel(
      Collection<TaskHistoryEventRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** the embedded task history events. */
  @JsonProperty("taskHistoryEvents")
  @Override
  public Collection<TaskHistoryEventRepresentationModel> getContent() {
    return super.getContent();
  }
}
