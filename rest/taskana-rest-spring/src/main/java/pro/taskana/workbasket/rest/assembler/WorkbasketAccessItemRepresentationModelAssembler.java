package pro.taskana.workbasket.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.ACCESSITEMS;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.resource.rest.PageLinks;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.rest.WorkbasketController;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;

/**
 * Transforms {@link WorkbasketAccessItem} to its resource counterpart {@link
 * WorkbasketAccessItemRepresentationModel} and vice versa.
 */
@Component
public class WorkbasketAccessItemRepresentationModelAssembler
    implements RepresentationModelAssembler<
            WorkbasketAccessItem, WorkbasketAccessItemRepresentationModel> {

  private final WorkbasketService workbasketService;

  @Autowired
  public WorkbasketAccessItemRepresentationModelAssembler(
      WorkbasketService workbasketService) {
    this.workbasketService = workbasketService;
  }

  @NonNull
  @Override
  public WorkbasketAccessItemRepresentationModel toModel(@NonNull WorkbasketAccessItem wbAccItem) {
    WorkbasketAccessItemRepresentationModel repModel
        = new WorkbasketAccessItemRepresentationModel();
    repModel.setAccessId(wbAccItem.getAccessId());
    repModel.setWorkbasketId(wbAccItem.getWorkbasketId());
    repModel.setWorkbasketKey(wbAccItem.getWorkbasketKey());
    repModel.setAccessItemId(wbAccItem.getId());
    repModel.setAccessName(wbAccItem.getAccessName());
    repModel.setPermRead(wbAccItem.isPermRead());
    repModel.setPermOpen(wbAccItem.isPermOpen());
    repModel.setPermAppend(wbAccItem.isPermAppend());
    repModel.setPermTransfer(wbAccItem.isPermTransfer());
    repModel.setPermDistribute(wbAccItem.isPermDistribute());
    repModel.setPermCustom1(wbAccItem.isPermCustom1());
    repModel.setPermCustom2(wbAccItem.isPermCustom2());
    repModel.setPermCustom3(wbAccItem.isPermCustom3());
    repModel.setPermCustom4(wbAccItem.isPermCustom4());
    repModel.setPermCustom5(wbAccItem.isPermCustom5());
    repModel.setPermCustom6(wbAccItem.isPermCustom6());
    repModel.setPermCustom7(wbAccItem.isPermCustom7());
    repModel.setPermCustom8(wbAccItem.isPermCustom8());
    repModel.setPermCustom9(wbAccItem.isPermCustom9());
    repModel.setPermCustom10(wbAccItem.isPermCustom10());
    repModel.setPermCustom11(wbAccItem.isPermCustom11());
    repModel.setPermCustom12(wbAccItem.isPermCustom12());
    return repModel;
  }

  public WorkbasketAccessItem toEntityModel(
      WorkbasketAccessItemRepresentationModel repModel) {
    WorkbasketAccessItemImpl wbAccItemModel =
        (WorkbasketAccessItemImpl)
            workbasketService.newWorkbasketAccessItem(
                repModel.getWorkbasketId(), repModel.getAccessId());
    wbAccItemModel.setWorkbasketKey(repModel.getWorkbasketKey());
    wbAccItemModel.setAccessName(repModel.getAccessName());
    wbAccItemModel.setPermRead(repModel.isPermRead());
    wbAccItemModel.setPermOpen(repModel.isPermOpen());
    wbAccItemModel.setPermAppend(repModel.isPermAppend());
    wbAccItemModel.setPermTransfer(repModel.isPermTransfer());
    wbAccItemModel.setPermDistribute(repModel.isPermDistribute());
    wbAccItemModel.setPermCustom1(repModel.isPermCustom1());
    wbAccItemModel.setPermCustom2(repModel.isPermCustom2());
    wbAccItemModel.setPermCustom3(repModel.isPermCustom3());
    wbAccItemModel.setPermCustom4(repModel.isPermCustom4());
    wbAccItemModel.setPermCustom5(repModel.isPermCustom5());
    wbAccItemModel.setPermCustom6(repModel.isPermCustom6());
    wbAccItemModel.setPermCustom7(repModel.isPermCustom7());
    wbAccItemModel.setPermCustom8(repModel.isPermCustom8());
    wbAccItemModel.setPermCustom9(repModel.isPermCustom9());
    wbAccItemModel.setPermCustom10(repModel.isPermCustom10());
    wbAccItemModel.setPermCustom11(repModel.isPermCustom11());
    wbAccItemModel.setPermCustom12(repModel.isPermCustom12());
    wbAccItemModel.setId(repModel.getAccessItemId());
    return wbAccItemModel;
  }

  public TaskanaPagedModel<WorkbasketAccessItemRepresentationModel> toPageModelForSingleWorkbasket(
      String workbasketId,
      List<WorkbasketAccessItem> workbasketAccessItems,
      PageMetadata pageMetadata)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    TaskanaPagedModel<WorkbasketAccessItemRepresentationModel> pageModel =
        toPageModel(workbasketAccessItems, pageMetadata);
    pageModel.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(workbasketId))
            .withSelfRel());
    pageModel.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
            .withRel("workbasket"));
    return pageModel;
  }

  @PageLinks(Mapping.URL_WORKBASKETACCESSITEMS)
  public TaskanaPagedModel<WorkbasketAccessItemRepresentationModel> toPageModel(
      List<WorkbasketAccessItem> workbasketAccessItems, PageMetadata pageMetadata) {
    return workbasketAccessItems.stream()
               .map(this::toModel)
               .collect(
                   Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> new TaskanaPagedModel<>(ACCESSITEMS, list, pageMetadata)));
  }
}
