package pro.taskana.workbasket.rest.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
        new WorkbasketRepresentationModelWithoutLinks();

    basket.setKey(workbasket.getKey());
    basket.setModified(workbasket.getModified());
    basket.setCreated(workbasket.getModified());
    basket.setWorkbasketId(workbasket.getId());
    basket.setDescription(workbasket.getDescription());
    basket.setDomain(workbasket.getDomain());
    basket.setName(workbasket.getName());
    basket.setType(workbasket.getType());
    basket.setOwner(workbasket.getOwner());
    basket.setCustom1(workbasket.getCustom1());
    basket.setCustom2(workbasket.getCustom2());
    basket.setCustom3(workbasket.getCustom3());
    basket.setCustom4(workbasket.getCustom4());
    basket.setOrgLevel1(workbasket.getOrgLevel1());
    basket.setOrgLevel2(workbasket.getOrgLevel2());
    basket.setOrgLevel3(workbasket.getOrgLevel3());
    basket.setOrgLevel4(workbasket.getOrgLevel4());

    List<WorkbasketAccessItemImpl> authorizations = new ArrayList<>();
    Set<String> distroTargets;
    try {
      for (WorkbasketAccessItem accessItem :
          workbasketService.getWorkbasketAccessItems(basket.getWorkbasketId())) {
        authorizations.add((WorkbasketAccessItemImpl) accessItem);
      }
      distroTargets =
          workbasketService.getDistributionTargets(workbasket.getId()).stream()
              .map(WorkbasketSummary::getId)
              .collect(Collectors.toSet());
    } catch (NotAuthorizedException | WorkbasketNotFoundException e) {
      throw new SystemException("Caught Exception", e);
    }

    WorkbasketDefinitionRepresentationModel repModel =
        new WorkbasketDefinitionRepresentationModel();

    repModel.setWorkbasket(basket);
    repModel.setAuthorizations(authorizations);
    repModel.setDistributionTargets(distroTargets);
    return repModel;
  }

  public Workbasket toEntityModel(WorkbasketRepresentationModel repModel) {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.newWorkbasket(repModel.getKey(), repModel.getDomain());
    workbasket.setId(repModel.getWorkbasketId());
    workbasket.setName(repModel.getName());
    workbasket.setType(repModel.getType());
    workbasket.setDescription(repModel.getDescription());
    workbasket.setOwner(repModel.getOwner());
    workbasket.setMarkedForDeletion(repModel.getMarkedForDeletion());
    workbasket.setCustom1(repModel.getCustom1());
    workbasket.setCustom2(repModel.getCustom2());
    workbasket.setCustom3(repModel.getCustom3());
    workbasket.setCustom4(repModel.getCustom4());
    workbasket.setOrgLevel1(repModel.getOrgLevel1());
    workbasket.setOrgLevel2(repModel.getOrgLevel2());
    workbasket.setOrgLevel3(repModel.getOrgLevel3());
    workbasket.setOrgLevel4(repModel.getOrgLevel4());
    workbasket.setCreated(repModel.getCreated());
    workbasket.setModified(repModel.getModified());
    return workbasket;
  }

  @Override
  public TaskanaPagedModelKeys getProperty() {
    return TaskanaPagedModelKeys.WORKBASKET_DEFINITIONS;
  }
}
