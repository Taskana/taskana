package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import org.springframework.hateoas.Link;

/** Resource class for {@link WorkbasketAccessItemResource} with Pagination. */
public class WorkbasketAccessItemListResource extends PagedResources<WorkbasketAccessItemResource> {

  public WorkbasketAccessItemListResource() {
    super();
  }

  public WorkbasketAccessItemListResource(
      Collection<WorkbasketAccessItemResource> content, PageMetadata metadata, Link... links) {
    super(content, metadata, links);
  }

  @Override
  @JsonProperty("accessItems")
  public Collection<WorkbasketAccessItemResource> getContent() {
    return super.getContent();
  }
}
