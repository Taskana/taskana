package pro.taskana.workbasket.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.ACCESSITEMS;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
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
    return new WorkbasketAccessItemRepresentationModel(wbAccItem);
  }

  public WorkbasketAccessItem toEntityModel(
      WorkbasketAccessItemRepresentationModel wbAccItemResource) {
    WorkbasketAccessItemImpl wbAccItemModel =
        (WorkbasketAccessItemImpl)
            workbasketService.newWorkbasketAccessItem(
                wbAccItemResource.getWorkbasketId(), wbAccItemResource.getAccessId());
    BeanUtils.copyProperties(wbAccItemResource, wbAccItemModel);
    wbAccItemModel.setId(wbAccItemResource.getAccessItemId());
    return wbAccItemModel;
  }

  public TaskanaPagedModel<WorkbasketAccessItemRepresentationModel> toPageModel(
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
