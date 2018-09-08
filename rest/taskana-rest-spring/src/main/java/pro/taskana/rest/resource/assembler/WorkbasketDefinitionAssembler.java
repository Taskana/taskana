package pro.taskana.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.WorkbasketDefinitionController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;
import pro.taskana.rest.resource.WorkbasketDefinition;

/**
 * Transforms {@link Workbasket} into a {@link WorkbasketDefinition}
 * containing all additional information about that workbasket.
 */
@Component
public class WorkbasketDefinitionAssembler {

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private WorkbasketResourceAssembler workbasketResourceAssembler;

    @Autowired
    private WorkbasketAccessItemAssembler workbasketAccessItemAssembler;

    /**
     * maps the distro targets to their id to remove overhead.
     *
     * @param basket
     *            {@link Workbasket} which will be converted
     * @return a {@link WorkbasketDefinition}, containing the {@code basket}, its distribution targets and its
     *         authorizations
     * @throws NotAuthorizedException
     *             if the user is not authorized
     * @throws WorkbasketNotFoundException
     *             if {@code basket} is an unknown workbasket
     */
    public WorkbasketDefinition toResource(Workbasket basket)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        List<WorkbasketAccessItemResource> authorizations = new ArrayList<>();
        for (WorkbasketAccessItem accessItem : workbasketService.getWorkbasketAccessItems(basket.getKey())) {
            authorizations.add(workbasketAccessItemAssembler.toResource(accessItem));
        }
        Set<String> distroTargets = workbasketService.getDistributionTargets(basket.getId())
            .stream()
            .map(WorkbasketSummary::getId)
            .collect(Collectors.toSet());
        WorkbasketDefinition resource = new WorkbasketDefinition(workbasketResourceAssembler.toResource(basket), distroTargets,
            authorizations);
        return addLinks(resource, basket);
    }

    private WorkbasketDefinition addLinks(WorkbasketDefinition resource, Workbasket workbasket) {
        resource.add(
            linkTo(methodOn(WorkbasketDefinitionController.class).exportWorkbaskets(workbasket.getDomain()))
                .withRel("exportWorkbaskets"));
        resource.add(
            linkTo(
                methodOn(WorkbasketDefinitionController.class).importWorkbaskets(Collections.singletonList(resource)))
                .withRel("importWorkbaskets"));
        return resource;
    }
}
