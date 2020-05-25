package pro.taskana.workbasket.rest.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.assembler.TaskanaPagingAssembler;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModelWithoutLinks;

/**
 * Transforms {@link Workbasket} into a {@link WorkbasketDefinitionRepresentationModel} containing
 * all additional information about that workbasket.
 */
@Component
public class WorkbasketDefinitionRepresentationModelAssembler
    implements TaskanaPagingAssembler<Workbasket, WorkbasketDefinitionRepresentationModel> {

  private final WorkbasketService workbasketService;

  @Autowired
  public WorkbasketDefinitionRepresentationModelAssembler(WorkbasketService workbasketService) {
    this.workbasketService = workbasketService;
  }

  @NonNull
  public WorkbasketDefinitionRepresentationModel toModel(@NonNull Workbasket workbasket) {

    WorkbasketRepresentationModelWithoutLinks basket =
        new WorkbasketRepresentationModelWithoutLinks(workbasket);

    List<WorkbasketAccessItemImpl> authorizations = new ArrayList<>();
    try {
      for (WorkbasketAccessItem accessItem :
          workbasketService.getWorkbasketAccessItems(basket.getWorkbasketId())) {
        authorizations.add((WorkbasketAccessItemImpl) accessItem);
      }
    } catch (NotAuthorizedException e) {
      throw new SystemException("Caught Exception", e);
    }
    Set<String> distroTargets = null;
    try {
      distroTargets =
          workbasketService.getDistributionTargets(workbasket.getId()).stream()
              .map(WorkbasketSummary::getId)
              .collect(Collectors.toSet());
    } catch (NotAuthorizedException | WorkbasketNotFoundException e) {
      throw new SystemException("Caught Exception", e);
    }
    return new WorkbasketDefinitionRepresentationModel(basket, distroTargets, authorizations);
  }

  public Workbasket toEntityModel(WorkbasketRepresentationModel wbResource) {
    WorkbasketImpl workbasket =
        (WorkbasketImpl)
            workbasketService.newWorkbasket(wbResource.getKey(), wbResource.getDomain());
    BeanUtils.copyProperties(wbResource, workbasket);

    workbasket.setId(wbResource.getWorkbasketId());
    workbasket.setModified(wbResource.getModified());
    workbasket.setCreated(wbResource.getCreated());
    return workbasket;
  }

  @Override
  public TaskanaPagedModelKeys getProperty() {
    return TaskanaPagedModelKeys.WORKBASKET_DEFINITIONS;
  }
}
