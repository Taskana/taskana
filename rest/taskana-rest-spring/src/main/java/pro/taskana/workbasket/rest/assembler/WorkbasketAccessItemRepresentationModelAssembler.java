package pro.taskana.workbasket.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pro.taskana.common.rest.assembler.CollectionRepresentationModelAssembler;
import pro.taskana.common.rest.assembler.PagedRepresentationModelAssembler;
import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.rest.WorkbasketController;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemCollectionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemPagedRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;

/**
 * Transforms {@link WorkbasketAccessItem} to its resource counterpart {@link
 * WorkbasketAccessItemRepresentationModel} and vice versa.
 */
@Component
public class WorkbasketAccessItemRepresentationModelAssembler
    implements CollectionRepresentationModelAssembler<
            WorkbasketAccessItem,
            WorkbasketAccessItemRepresentationModel,
            WorkbasketAccessItemCollectionRepresentationModel>,
        PagedRepresentationModelAssembler<
            WorkbasketAccessItem,
            WorkbasketAccessItemRepresentationModel,
            WorkbasketAccessItemPagedRepresentationModel> {

  private final WorkbasketService workbasketService;

  @Autowired
  public WorkbasketAccessItemRepresentationModelAssembler(WorkbasketService workbasketService) {
    this.workbasketService = workbasketService;
  }

  @NonNull
  @Override
  public WorkbasketAccessItemRepresentationModel toModel(@NonNull WorkbasketAccessItem wbAccItem) {
    WorkbasketAccessItemRepresentationModel repModel =
        new WorkbasketAccessItemRepresentationModel();
    repModel.setAccessId(wbAccItem.getAccessId());
    repModel.setWorkbasketId(wbAccItem.getWorkbasketId());
    repModel.setWorkbasketKey(wbAccItem.getWorkbasketKey());
    repModel.setAccessItemId(wbAccItem.getId());
    repModel.setAccessName(wbAccItem.getAccessName());
    repModel.setPermRead(wbAccItem.getPermission(WorkbasketPermission.READ));
    repModel.setPermOpen(wbAccItem.getPermission(WorkbasketPermission.OPEN));
    repModel.setPermAppend(wbAccItem.getPermission(WorkbasketPermission.APPEND));
    repModel.setPermTransfer(wbAccItem.getPermission(WorkbasketPermission.TRANSFER));
    repModel.setPermDistribute(wbAccItem.getPermission(WorkbasketPermission.DISTRIBUTE));
    repModel.setPermCustom1(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_1));
    repModel.setPermCustom2(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_2));
    repModel.setPermCustom3(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_3));
    repModel.setPermCustom4(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_4));
    repModel.setPermCustom5(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_5));
    repModel.setPermCustom6(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_6));
    repModel.setPermCustom7(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_7));
    repModel.setPermCustom8(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_8));
    repModel.setPermCustom9(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_9));
    repModel.setPermCustom10(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_10));
    repModel.setPermCustom11(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_11));
    repModel.setPermCustom12(wbAccItem.getPermission(WorkbasketPermission.CUSTOM_12));
    return repModel;
  }

  public WorkbasketAccessItem toEntityModel(WorkbasketAccessItemRepresentationModel repModel) {
    WorkbasketAccessItemImpl wbAccItemModel =
        (WorkbasketAccessItemImpl)
            workbasketService.newWorkbasketAccessItem(
                repModel.getWorkbasketId(), repModel.getAccessId());
    wbAccItemModel.setWorkbasketKey(repModel.getWorkbasketKey());
    wbAccItemModel.setAccessName(repModel.getAccessName());
    wbAccItemModel.setPermission(WorkbasketPermission.READ, repModel.isPermRead());
    wbAccItemModel.setPermission(WorkbasketPermission.OPEN, repModel.isPermOpen());
    wbAccItemModel.setPermission(WorkbasketPermission.APPEND, repModel.isPermAppend());
    wbAccItemModel.setPermission(WorkbasketPermission.TRANSFER, repModel.isPermTransfer());
    wbAccItemModel.setPermission(WorkbasketPermission.DISTRIBUTE, repModel.isPermDistribute());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_1, repModel.isPermCustom1());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_2, repModel.isPermCustom2());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_3, repModel.isPermCustom3());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_4, repModel.isPermCustom4());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_5, repModel.isPermCustom5());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_6, repModel.isPermCustom6());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_7, repModel.isPermCustom7());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_8, repModel.isPermCustom8());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_9, repModel.isPermCustom9());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_10, repModel.isPermCustom10());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_11, repModel.isPermCustom11());
    wbAccItemModel.setPermission(WorkbasketPermission.CUSTOM_12, repModel.isPermCustom12());
    wbAccItemModel.setId(repModel.getAccessItemId());
    return wbAccItemModel;
  }

  public WorkbasketAccessItemCollectionRepresentationModel
      toTaskanaCollectionModelForSingleWorkbasket(
          String workbasketId, List<WorkbasketAccessItem> workbasketAccessItems)
          throws WorkbasketNotFoundException, NotAuthorizedOnWorkbasketException {
    WorkbasketAccessItemCollectionRepresentationModel pageModel =
        toTaskanaCollectionModel(workbasketAccessItems);
    pageModel.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
            .withRel("workbasket"));
    return pageModel;
  }

  @Override
  public WorkbasketAccessItemCollectionRepresentationModel buildCollectionEntity(
      List<WorkbasketAccessItemRepresentationModel> content) {
    return new WorkbasketAccessItemCollectionRepresentationModel(content);
  }

  @Override
  public WorkbasketAccessItemPagedRepresentationModel buildPageableEntity(
      Collection<WorkbasketAccessItemRepresentationModel> content, PageMetadata pageMetadata) {
    return new WorkbasketAccessItemPagedRepresentationModel(content, pageMetadata);
  }
}
