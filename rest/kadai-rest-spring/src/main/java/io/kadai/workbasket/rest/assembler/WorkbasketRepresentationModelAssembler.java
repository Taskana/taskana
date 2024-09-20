package io.kadai.workbasket.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import io.kadai.workbasket.rest.WorkbasketController;
import io.kadai.workbasket.rest.models.WorkbasketRepresentationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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
    repModel.setCustom1(workbasket.getCustomField(WorkbasketCustomField.CUSTOM_1));
    repModel.setCustom2(workbasket.getCustomField(WorkbasketCustomField.CUSTOM_2));
    repModel.setCustom3(workbasket.getCustomField(WorkbasketCustomField.CUSTOM_3));
    repModel.setCustom4(workbasket.getCustomField(WorkbasketCustomField.CUSTOM_4));
    repModel.setCustom5(workbasket.getCustomField(WorkbasketCustomField.CUSTOM_5));
    repModel.setCustom6(workbasket.getCustomField(WorkbasketCustomField.CUSTOM_6));
    repModel.setCustom7(workbasket.getCustomField(WorkbasketCustomField.CUSTOM_7));
    repModel.setCustom8(workbasket.getCustomField(WorkbasketCustomField.CUSTOM_8));
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
    workbasket.setCustom5(repModel.getCustom5());
    workbasket.setCustom6(repModel.getCustom6());
    workbasket.setCustom7(repModel.getCustom7());
    workbasket.setCustom8(repModel.getCustom8());
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
      throws WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          NotAuthorizedException {
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasket(wb.getId())).withSelfRel());
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getDistributionTargets(wb.getId()))
            .withRel("distributionTargets"));
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(wb.getId()))
            .withRel("accessItems"));
    resource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbaskets(null, null, null, null))
            .withRel("allWorkbaskets"));
    resource.add(
        linkTo(
                methodOn(WorkbasketController.class)
                    .removeDistributionTargetForWorkbasketId(wb.getId()))
            .withRel("removeDistributionTargets"));
    return resource;
  }
}
