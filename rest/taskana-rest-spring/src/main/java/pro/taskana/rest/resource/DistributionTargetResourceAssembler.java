package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.WorkbasketController;

/**
 * Transforms WorkbasketSummary to its resource counterpart DistributionTargerResource and vice versa.
 */
@Component
public class DistributionTargetResourceAssembler extends
    ResourceAssemblerSupport<WorkbasketSummary, DistributionTargetResource> {

    public DistributionTargetResourceAssembler() {
        super(WorkbasketController.class, DistributionTargetResource.class);
    }

    public DistributionTargetResource toResource(WorkbasketSummary summary) {
        return new DistributionTargetResource(summary);
    }

    public DistributionTargetListResource toResources(String workbasketId,
        List<WorkbasketSummary> distributionTargets) throws WorkbasketNotFoundException, NotAuthorizedException {

        DistributionTargetListResource distributionTargetListResource = new DistributionTargetListResource(
            toResources(distributionTargets));
        distributionTargetListResource
            .add(linkTo(methodOn(WorkbasketController.class).getDistributionTargets(workbasketId))
                .withSelfRel());
        distributionTargetListResource
            .add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
                .withRel("workbasket"));

        return distributionTargetListResource;
    }

}
