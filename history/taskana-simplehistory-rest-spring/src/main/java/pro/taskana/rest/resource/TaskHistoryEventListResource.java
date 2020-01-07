package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import org.springframework.hateoas.Link;

/** Resource class for {@link TaskHistoryEventResource} with Pagination. */
public class TaskHistoryEventListResource extends PagedResources<TaskHistoryEventResource> {

  public TaskHistoryEventListResource() {
    super();
  }

  public TaskHistoryEventListResource(
      Collection<TaskHistoryEventResource> content, PageMetadata metadata, Link... links) {
    super(content, metadata, links);
  }

  public TaskHistoryEventListResource(
      Collection<TaskHistoryEventResource> content, PageMetadata metadata, Iterable<Link> links) {
    super(content, metadata, links);
  }

  @Override
  @JsonProperty("taskHistoryEvents")
  public Collection<TaskHistoryEventResource> getContent() {
    return super.getContent();
  }
}
