package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import org.springframework.hateoas.Link;

/** EntityModel class for {@link ClassificationSummaryResource} with Pagination. */
public class ClassificationSummaryListResource
    extends PagedResources<ClassificationSummaryResource> {

  public ClassificationSummaryListResource() {
    super();
  }

  public ClassificationSummaryListResource(
      Collection<ClassificationSummaryResource> content, PageMetadata metadata, Link... links) {
    super(content, metadata, links);
  }

  @JsonProperty("classifications")
  public Collection<ClassificationSummaryResource> getContent() {
    return super.getContent();
  }
}
