package pro.taskana.rest.resource;

import java.util.ArrayList;
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

/**
 * Transforms {@link Workbasket} into a {@link WorkbasketDefinition}
 * containing all additional information about that workbasket.
 */
@Component
public class WorkbasketDefinitionAssembler {

    @Autowired
    private WorkbasketService workbasketService;

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
    public WorkbasketDefinition toDefinition(Workbasket basket)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        List<WorkbasketAccessItem> authorizations = new ArrayList<>();
        for (WorkbasketAccessItem accessItem : workbasketService.getWorkbasketAccessItems(basket.getKey())) {
            authorizations.add(accessItem);
        }
        Set<String> distroTargets = workbasketService.getDistributionTargets(basket.getId())
            .stream()
            .map(WorkbasketSummary::getId)
            .collect(Collectors.toSet());
        return new WorkbasketDefinition(basket, distroTargets, authorizations);
    }
}
