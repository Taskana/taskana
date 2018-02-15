package pro.taskana.rest.resource.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.resource.WorkbasketDefinition;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkbasketDefinitionMapper {

    @Autowired
    private WorkbasketService workbasketService;

    /**
     * maps the distro targets to their id to remove overhead.
     * @param basket {@link Workbasket} which will be converted
     * @return a {@link WorkbasketDefinition}, containing the {@code basket},
     *         its ditribution targets and its authorizations
     * @throws NotAuthorizedException if the user is not authorized
     * @throws WorkbasketNotFoundException if {@code basket} is an unknown workbasket
     */
    public WorkbasketDefinition toResource(Workbasket basket) throws NotAuthorizedException, WorkbasketNotFoundException {
        List<WorkbasketAccessItem> authorizations = workbasketService.getWorkbasketAuthorizations(basket.getKey());
        Set<String> distroTargets = workbasketService.getDistributionTargets(basket.getId()).stream()
                .map(WorkbasketSummary::getId)
                .collect(Collectors.toSet());
        return new WorkbasketDefinition(basket,distroTargets,authorizations);

    }
}
