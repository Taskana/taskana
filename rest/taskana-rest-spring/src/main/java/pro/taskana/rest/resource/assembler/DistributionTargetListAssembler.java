package pro.taskana.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.DistributionTargetResource;

/**
 * Mapper to convert from a list of WorkbasketSummary to a workbasket list resource.
 */
@Component
public class DistributionTargetListAssembler {

    @Autowired
    private DistributionTargetResourceAssembler distributionTargetResourceAssembler;

    public Resources<DistributionTargetResource> toResource(String workbasketId,
        Collection<WorkbasketSummary> distributionTargets) throws WorkbasketNotFoundException, NotAuthorizedException {
        List<DistributionTargetResource> resourceList = new ArrayList<>();
        for (WorkbasketSummary wb : distributionTargets) {
            resourceList.add(distributionTargetResourceAssembler.toResource(wb));
        }
        Resources<DistributionTargetResource> distributionTargetListResource = new Resources<>(resourceList);

        distributionTargetListResource
            .add(linkTo(methodOn(WorkbasketController.class).getDistributionTargets(workbasketId))
                .withSelfRel());
        distributionTargetListResource
            .add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
                .withRel("workbasket"));

        return distributionTargetListResource;
    }

}
