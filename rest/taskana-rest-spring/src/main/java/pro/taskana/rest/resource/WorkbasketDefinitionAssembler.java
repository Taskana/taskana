package pro.taskana.rest.resource;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketImpl;

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
     * @param workbasket
     *            {@link Workbasket} which will be converted
     * @return a {@link WorkbasketDefinition}, containing the {@code basket}, its distribution targets and its
     *         authorizations
     * @throws NotAuthorizedException
     *             if the user is not authorized
     * @throws WorkbasketNotFoundException
     *             if {@code basket} is an unknown workbasket
     */
    public WorkbasketDefinition toDefinition(Workbasket workbasket)
        throws NotAuthorizedException, WorkbasketNotFoundException {

        WorkbasketResource basket = new WorkbasketResource();
        BeanUtils.copyProperties(workbasket, basket);
        basket.setWorkbasketId(workbasket.getId());
        basket.setModified(workbasket.getModified().toString());
        basket.setCreated(workbasket.getCreated().toString());

        List<WorkbasketAccessItem> authorizations = new ArrayList<>();
        for (WorkbasketAccessItem accessItem : workbasketService.getWorkbasketAccessItems(basket.getKey())) {
            authorizations.add(accessItem);
        }
        Set<String> distroTargets = workbasketService.getDistributionTargets(workbasket.getId())
            .stream()
            .map(WorkbasketSummary::getId)
            .collect(Collectors.toSet());
        return new WorkbasketDefinition(basket, distroTargets, authorizations);
    }

    public Workbasket toModel(WorkbasketResource wbResource) {
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket(wbResource.key, wbResource.domain);
        BeanUtils.copyProperties(wbResource, workbasket);

        workbasket.setId(wbResource.workbasketId);
        if (wbResource.getModified() != null) {
            workbasket.setModified(Instant.parse(wbResource.modified));
        }
        if (wbResource.getCreated() != null) {
            workbasket.setCreated(Instant.parse(wbResource.created));
        }
        return workbasket;
    }
}
