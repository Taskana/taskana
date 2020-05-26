package pro.taskana.workbasket.rest.assembler;

import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.DISTRIBUTION_TARGETS;
import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.WORKBASKETS;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.resource.rest.PageLinks;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

/**
 * EntityModel assembler for {@link WorkbasketSummaryRepresentationModel}.
 */
@Component
public class WorkbasketSummaryRepresentationModelAssembler implements
    RepresentationModelAssembler<WorkbasketSummary, WorkbasketSummaryRepresentationModel> {

  private WorkbasketService workbasketService;

  public WorkbasketSummaryRepresentationModelAssembler() {
  }

  @Autowired
  public WorkbasketSummaryRepresentationModelAssembler(WorkbasketService workbasketService) {
    this.workbasketService = workbasketService;
  }

  @NonNull
  @Override
  public WorkbasketSummaryRepresentationModel toModel(
      @NonNull WorkbasketSummary workbasketSummary) {
    WorkbasketSummaryRepresentationModel repModel = new WorkbasketSummaryRepresentationModel();
    repModel.setWorkbasketId(workbasketSummary.getId());
    repModel.setKey(workbasketSummary.getKey());
    repModel.setName(workbasketSummary.getName());
    repModel.setDomain(workbasketSummary.getDomain());
    repModel.setType(workbasketSummary.getType());
    repModel.setDescription(workbasketSummary.getDescription());
    repModel.setOwner(workbasketSummary.getOwner());
    repModel.setMarkedForDeletion(workbasketSummary.isMarkedForDeletion());
    repModel.setCustom1(workbasketSummary.getCustom1());
    repModel.setCustom2(workbasketSummary.getCustom2());
    repModel.setCustom3(workbasketSummary.getCustom3());
    repModel.setCustom4(workbasketSummary.getCustom4());
    repModel.setOrgLevel1(workbasketSummary.getOrgLevel1());
    repModel.setOrgLevel2(workbasketSummary.getOrgLevel2());
    repModel.setOrgLevel3(workbasketSummary.getOrgLevel3());
    repModel.setOrgLevel4(workbasketSummary.getOrgLevel4());
    return repModel;
  }

  public WorkbasketSummary toEntityModel(WorkbasketSummaryRepresentationModel repModel) {
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
    return workbasket;
  }

  @PageLinks(Mapping.URL_WORKBASKET)
  public TaskanaPagedModel<WorkbasketSummaryRepresentationModel> toPageModel(
      List<WorkbasketSummary> workbasketSummaries, PageMetadata pageMetadata) {
    return workbasketSummaries.stream()
               .map(this::toModel)
               .collect(
                   Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> new TaskanaPagedModel<>(WORKBASKETS, list, pageMetadata)));
  }

  @PageLinks(Mapping.URL_WORKBASKET_ID_DISTRIBUTION)
  public TaskanaPagedModel<WorkbasketSummaryRepresentationModel> toDistributionTargetPageModel(
      List<WorkbasketSummary> workbasketSummaries, PageMetadata pageMetadata) {
    return workbasketSummaries.stream()
               .map(this::toModel)
               .collect(
                   Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> new TaskanaPagedModel<>(DISTRIBUTION_TARGETS, list, pageMetadata)));
  }
}
