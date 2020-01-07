package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import org.springframework.hateoas.Link;

/** Resource class for {@link WorkbasketAccessItemResource} without Pagination. */
public class WorkbasketAccessItemListResource extends WorkbasketAccessItemPaginatedListResource {

  public WorkbasketAccessItemListResource() {
    super();
  }

  public WorkbasketAccessItemListResource(
      Collection<WorkbasketAccessItemResource> content, Link... links) {
    super(content, null, links);
  }

  public WorkbasketAccessItemListResource(
      Collection<WorkbasketAccessItemResource> content, Iterable<Link> links) {
    super(content, null, links);
  }

  @Override
  @JsonIgnore
  public PageMetadata getMetadata() {
    return super.getMetadata();
  }
}
