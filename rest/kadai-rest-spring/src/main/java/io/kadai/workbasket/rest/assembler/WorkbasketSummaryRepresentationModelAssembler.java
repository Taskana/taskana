package io.kadai.workbasket.rest.assembler;

import io.kadai.common.rest.assembler.CollectionRepresentationModelAssembler;
import io.kadai.common.rest.assembler.PagedRepresentationModelAssembler;
import io.kadai.common.rest.models.PageMetadata;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import io.kadai.workbasket.internal.models.WorkbasketSummaryImpl;
import io.kadai.workbasket.rest.models.DistributionTargetsCollectionRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketSummaryPagedRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketSummaryRepresentationModel;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/** EntityModel assembler for {@link WorkbasketSummaryRepresentationModel}. */
@Component
public class WorkbasketSummaryRepresentationModelAssembler
    implements PagedRepresentationModelAssembler<
            WorkbasketSummary,
            WorkbasketSummaryRepresentationModel,
            WorkbasketSummaryPagedRepresentationModel>,
        CollectionRepresentationModelAssembler<
            WorkbasketSummary,
            WorkbasketSummaryRepresentationModel,
            DistributionTargetsCollectionRepresentationModel> {

  private WorkbasketService workbasketService;

  public WorkbasketSummaryRepresentationModelAssembler() {}

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
    repModel.setCustom1(workbasketSummary.getCustomField(WorkbasketCustomField.CUSTOM_1));
    repModel.setCustom2(workbasketSummary.getCustomField(WorkbasketCustomField.CUSTOM_2));
    repModel.setCustom3(workbasketSummary.getCustomField(WorkbasketCustomField.CUSTOM_3));
    repModel.setCustom4(workbasketSummary.getCustomField(WorkbasketCustomField.CUSTOM_4));
    repModel.setCustom5(workbasketSummary.getCustomField(WorkbasketCustomField.CUSTOM_5));
    repModel.setCustom6(workbasketSummary.getCustomField(WorkbasketCustomField.CUSTOM_6));
    repModel.setCustom7(workbasketSummary.getCustomField(WorkbasketCustomField.CUSTOM_7));
    repModel.setCustom8(workbasketSummary.getCustomField(WorkbasketCustomField.CUSTOM_8));
    repModel.setOrgLevel1(workbasketSummary.getOrgLevel1());
    repModel.setOrgLevel2(workbasketSummary.getOrgLevel2());
    repModel.setOrgLevel3(workbasketSummary.getOrgLevel3());
    repModel.setOrgLevel4(workbasketSummary.getOrgLevel4());
    return repModel;
  }

  public WorkbasketSummary toEntityModel(WorkbasketSummaryRepresentationModel repModel) {
    WorkbasketSummaryImpl workbasket =
        (WorkbasketSummaryImpl)
            workbasketService.newWorkbasket(repModel.getKey(), repModel.getDomain()).asSummary();
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
    return workbasket;
  }

  @Override
  public WorkbasketSummaryPagedRepresentationModel buildPageableEntity(
      Collection<WorkbasketSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    return new WorkbasketSummaryPagedRepresentationModel(content, pageMetadata);
  }

  @Override
  public DistributionTargetsCollectionRepresentationModel buildCollectionEntity(
      List<WorkbasketSummaryRepresentationModel> content) {
    return new DistributionTargetsCollectionRepresentationModel(content);
  }
}
