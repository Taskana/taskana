package pro.taskana.rest.resource;

import java.util.Collection;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources.PageMetadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resource class for {@link DistributionTargetResource} with Pagination.
 */
public class DistributionTargetListResource extends PagedResources<DistributionTargetResource> {

    public DistributionTargetListResource() {
        super();
    }

    public DistributionTargetListResource(Collection<DistributionTargetResource> content, Link... links) {
        super(content, null, links);
    }

    @Override
    @JsonProperty("distributionTargets")
    public Collection<DistributionTargetResource> getContent() {
        return super.getContent();
    }

    @Override
    @JsonIgnore
    public PageMetadata getMetadata() {
        return super.getMetadata();
    }
}
