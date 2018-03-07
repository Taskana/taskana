package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.DistributionTargetResource;

/**
 * Transforms WorkbasketSummary to its resource counterpart DistributionTargerResource and vice versa.
 */
@Component
public class DistributionTargetMapper {

    public DistributionTargetResource toResource(WorkbasketSummary summary) {
        DistributionTargetResource resource = new DistributionTargetResource();
        BeanUtils.copyProperties(summary, resource);
        // named different so needs to be set by hand
        resource.setWorkbasketId(summary.getId());

        return addLinks(resource, summary);
    }

    private DistributionTargetResource addLinks(DistributionTargetResource resource, WorkbasketSummary summary) {
        resource.add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(summary.getId())).withSelfRel());
        return resource;
    }
}
