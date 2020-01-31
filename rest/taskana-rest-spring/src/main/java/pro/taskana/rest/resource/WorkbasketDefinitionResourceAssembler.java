package pro.taskana.rest.resource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketAccessItem;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketSummary;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.internal.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.internal.WorkbasketImpl;

/**
 * Transforms {@link Workbasket} into a {@link WorkbasketDefinitionResource} containing all
 * additional information about that workbasket.
 */
@Component
public class WorkbasketDefinitionResourceAssembler {

  @Autowired private WorkbasketService workbasketService;

  /**
   * maps the distro targets to their id to remove overhead.
   *
   * @param workbasket {@link Workbasket} which will be converted
   * @return a {@link WorkbasketDefinitionResource}, containing the {@code basket}, its distribution
   *     targets and its authorizations
   * @throws NotAuthorizedException if the user is not authorized
   * @throws WorkbasketNotFoundException if {@code basket} is an unknown workbasket
   */
  public WorkbasketDefinitionResource toResource(Workbasket workbasket)
      throws NotAuthorizedException, WorkbasketNotFoundException {

    WorkbasketResourceWithoutLinks basket = new WorkbasketResourceWithoutLinks(workbasket);

    List<WorkbasketAccessItemImpl> authorizations = new ArrayList<>();
    for (WorkbasketAccessItem accessItem :
        workbasketService.getWorkbasketAccessItems(basket.getWorkbasketId())) {
      authorizations.add((WorkbasketAccessItemImpl) accessItem);
    }
    Set<String> distroTargets =
        workbasketService.getDistributionTargets(workbasket.getId()).stream()
            .map(WorkbasketSummary::getId)
            .collect(Collectors.toSet());
    return new WorkbasketDefinitionResource(basket, distroTargets, authorizations);
  }

  public Workbasket toModel(WorkbasketResource wbResource) {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.newWorkbasket(wbResource.key, wbResource.domain);
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
