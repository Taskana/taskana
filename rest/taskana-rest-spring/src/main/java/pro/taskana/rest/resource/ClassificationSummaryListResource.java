package pro.taskana.rest.resource;

import java.util.Collection;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resource class for {@link ClassificationSummaryResource} with Pagination.
 */
public class ClassificationSummaryListResource extends PagedResources<ClassificationSummaryResource> {

    public ClassificationSummaryListResource() {
        super();
    }

    public ClassificationSummaryListResource(Collection<ClassificationSummaryResource> content, PageMetadata metadata,
        Iterable<Link> links) {
        super(content, metadata, links);
    }

    public ClassificationSummaryListResource(Collection<ClassificationSummaryResource> content, PageMetadata metadata,
        Link... links) {
        super(content, metadata, links);
    }

    @JsonProperty("classifications")
    public Collection<ClassificationSummaryResource> getContent() {
        return super.getContent();
    }
}
