package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.DistributionTargetResource;

/**
 * Mapper to convert from a list of WorkbasketSummary to a workbasket list resource.
 */
@Component
public class DistributionTargetListMapper {

    @Autowired
    private DistributionTargetMapper distributionTargetMapper;

    public Resources<DistributionTargetResource> toResource(String workbasketId,
        Collection<WorkbasketSummary> distributionTargets) {
        List<DistributionTargetResource> resourceList = distributionTargets.stream()
            .map(workbasket -> distributionTargetMapper.toResource(workbasket))
            .collect(Collectors.toList());
        Resources<DistributionTargetResource> distributionTargetListResource = new Resources<>(resourceList);

        distributionTargetListResource
            .add(linkTo(methodOn(WorkbasketController.class).getDistributionTargets(workbasketId))
                .withSelfRel());
        distributionTargetListResource
            .add(linkTo(methodOn(WorkbasketController.class).getDistributionTargets(workbasketId))
                .withRel("distributionTargetResourceList"));
        distributionTargetListResource
            .add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
                .withRel("workbasket"));

        return distributionTargetListResource;
    }

}
