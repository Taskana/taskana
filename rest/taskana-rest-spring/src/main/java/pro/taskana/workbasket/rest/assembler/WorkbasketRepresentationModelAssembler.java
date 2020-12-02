package pro.taskana.workbasket.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.rest.WorkbasketController;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;

/**
 * Transforms {@link Workbasket} to its resource counterpart {@link WorkbasketRepresentationModel}
 * and vice versa.
 */
@Component
public class WorkbasketRepresentationModelAssembler
    implements RepresentationModelAssembler<Workbasket, WorkbasketRepresentationModel> {

  private final WorkbasketService workbasketService;

  @Autowired
  public WorkbasketRepresentationModelAssembler(WorkbasketService workbasketService) {
    this.workbasketService = workbasketService;
  }

  @NonNull
  @Override
  public WorkbasketRepresentationModel toModel(@NonNull Workbasket workbasket) {
    WorkbasketRepresentationModel repModel = new WorkbasketRepresentationModel();
    repModel.setWorkbasketId(workbasket.getId());
    repModel.setKey(workbasket.getKey());
    repModel.setName(workbasket.getName());
    repModel.setDomain(workbasket.getDomain());
    repModel.setType(workbasket.getType());
    repModel.setDescription(workbasket.getDescription());
    repModel.setOwner(workbasket.getOwner());
    repModel.setMarkedForDeletion(workbasket.isMarkedForDeletion());
    repModel.setCustom1(workbasket.getCustomAttribute(WorkbasketCustomField.CUSTOM_1));
    repModel.setCustom2(workbasket.getCustomAttribute(WorkbasketCustomField.CUSTOM_2));
    repModel.setCustom3(workbasket.getCustomAttribute(WorkbasketCustomField.CUSTOM_3));
    repModel.setCustom4(workbasket.getCustomAttribute(WorkbasketCustomField.CUSTOM_4));
    repModel.setOrgLevel1(workbasket.getOrgLevel1());
    repModel.setOrgLevel2(workbasket.getOrgLevel2());
    repModel.setOrgLevel3(workbasket.getOrgLevel3());
    repModel.setOrgLevel4(workbasket.getOrgLevel4());
    repModel.setCreated(workbasket.getCreated());
    repModel.setModified(workbasket.getModified());
    try {
      return addLinks(repModel, workbasket);
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
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

  private WorkbasketRepresentationModel addLinks(
      WorkbasketRepresentationModel resource, Workbasket wb)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasket(wb.getId())).withSelfRel());
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getDistributionTargets(wb.getId()))
            .withRel("distributionTargets"));
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(wb.getId()))
            .withRel("accessItems"));
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbaskets(null, null, null))
            .withRel("allWorkbaskets"));
    resource.add(
        linkTo(
                methodOn(WorkbasketController.class)
                    .removeDistributionTargetForWorkbasketId(wb.getId()))
            .withRel("removeDistributionTargets"));
    return resource;
  }
}
