package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import org.springframework.hateoas.Link;

/** Resource class for {@link WorkbasketSummaryResource} with Pagination. */
public class WorkbasketSummaryListResource extends PagedResources<WorkbasketSummaryResource> {

  public WorkbasketSummaryListResource() {
    super();
  }

  public WorkbasketSummaryListResource(
      Collection<WorkbasketSummaryResource> content, PageMetadata metadata, Link... links) {
    super(content, metadata, links);
  }

  public WorkbasketSummaryListResource(
      Collection<WorkbasketSummaryResource> content, PageMetadata metadata, Iterable<Link> links) {
    super(content, metadata, links);
  }

  @Override
  @JsonProperty("workbaskets")
  public Collection<WorkbasketSummaryResource> getContent() {
    return super.getContent();
  }
}
