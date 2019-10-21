package pro.taskana.rest.resource;

import java.util.Collection;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources.PageMetadata;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resource class for {@link TaskHistoryEventResource} with Pagination.
 */
public class TaskHistoryEventListResource extends PagedResources<TaskHistoryEventResource> {

    public TaskHistoryEventListResource() {
        super();
    }

    public TaskHistoryEventListResource(Collection<TaskHistoryEventResource> content, PageMetadata metadata,
        Link... links) {
        super(content, metadata, links);
    }

    public TaskHistoryEventListResource(Collection<TaskHistoryEventResource> content, PageMetadata metadata,
        Iterable<Link> links) {
        super(content, metadata, links);
    }

    @Override
    @JsonProperty("taskHistoryEvents")
    public Collection<TaskHistoryEventResource> getContent() {
        return super.getContent();
    }
}
