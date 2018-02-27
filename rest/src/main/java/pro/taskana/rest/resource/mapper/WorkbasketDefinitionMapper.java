package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.WorkbasketDefinitionController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;
import pro.taskana.rest.resource.WorkbasketDefinition;

@Component
public class WorkbasketDefinitionMapper {

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private WorkbasketMapper workbasketMapper;

    @Autowired
    private WorkbasketAccessItemMapper workbasketAccessItemMapper;

    /**
     * maps the distro targets to their id to remove overhead.
     *
     * @param basket
     *            {@link Workbasket} which will be converted
     * @return a {@link WorkbasketDefinition}, containing the {@code basket}, its ditribution targets and its
     *         authorizations
     * @throws NotAuthorizedException
     *             if the user is not authorized
     * @throws WorkbasketNotFoundException
     *             if {@code basket} is an unknown workbasket
     */
    public WorkbasketDefinition toResource(Workbasket basket)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        List<WorkbasketAccessItemResource> authorizations = new ArrayList<>();
        workbasketService.getWorkbasketAuthorizations(
            basket.getKey())
            .stream()
            .forEach(t -> {
                try {
                    authorizations.add(workbasketAccessItemMapper.toResource(t));
                } catch (NotAuthorizedException e) {
                    e.printStackTrace();
                }
            });
        Set<String> distroTargets = workbasketService.getDistributionTargets(basket.getId())
            .stream()
            .map(WorkbasketSummary::getId)
            .collect(Collectors.toSet());
        WorkbasketDefinition resource = new WorkbasketDefinition(workbasketMapper.toResource(basket), distroTargets,
            authorizations);
        return addLinks(resource, basket);
    }

    private WorkbasketDefinition addLinks(WorkbasketDefinition resource, Workbasket workbasket) {
        resource.add(
            linkTo(methodOn(WorkbasketDefinitionController.class).exportWorkbaskets(workbasket.getDomain()))
                .withRel("exportWorkbaskets"));
        resource.add(
            linkTo(methodOn(WorkbasketDefinitionController.class).importWorkbaskets(Arrays.asList(resource)))
                .withRel("importWorkbaskets"));
        return resource;
    }
}
