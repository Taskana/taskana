package pro.taskana.simplehistory.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel.PageMetadata;

import pro.taskana.common.rest.models.PagedResources;

/** Resource class for {@link TaskHistoryEventRepresentationModel} with Pagination. */
public class TaskHistoryEventListResource
    extends PagedResources<TaskHistoryEventRepresentationModel> {

  @SuppressWarnings("unused")
  private TaskHistoryEventListResource() {}

  public TaskHistoryEventListResource(
      Collection<TaskHistoryEventRepresentationModel> content,
      PageMetadata metadata,
      Link... links) {
    super(content, metadata, links);
  }

  @Override
  @JsonProperty("taskHistoryEvents")
  public Collection<TaskHistoryEventRepresentationModel> getContent() {
    return super.getContent();
  }
}
