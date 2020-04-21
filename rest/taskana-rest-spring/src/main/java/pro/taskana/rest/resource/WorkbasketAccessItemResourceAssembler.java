package pro.taskana.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.resource.links.PageLinks;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

/**
 * Transforms {@link WorkbasketAccessItem} to its resource counterpart {@link
 * WorkbasketAccessItemResource} and vice versa.
 */
@Component
public class WorkbasketAccessItemResourceAssembler
    extends RepresentationModelAssemblerSupport<
        WorkbasketAccessItem, WorkbasketAccessItemResource> {

  @Autowired private WorkbasketService workbasketService;

  public WorkbasketAccessItemResourceAssembler() {
    super(WorkbasketController.class, WorkbasketAccessItemResource.class);
  }

  public WorkbasketAccessItemResource toModel(WorkbasketAccessItem wbAccItem) {
    return new WorkbasketAccessItemResource(wbAccItem);
  }

  public WorkbasketAccessItem toModel(WorkbasketAccessItemResource wbAccItemResource) {
    WorkbasketAccessItemImpl wbAccItemModel =
        (WorkbasketAccessItemImpl)
            workbasketService.newWorkbasketAccessItem(
                wbAccItemResource.getWorkbasketId(), wbAccItemResource.getAccessId());
    BeanUtils.copyProperties(wbAccItemResource, wbAccItemModel);
    wbAccItemModel.setId(wbAccItemResource.getAccessItemId());
    return wbAccItemModel;
  }

  @PageLinks(Mapping.URL_WORKBASKETACCESSITEMS)
  public WorkbasketAccessItemListResource toCollectionModel(
      List<WorkbasketAccessItem> entities, PageMetadata pageMetadata) {
    return new WorkbasketAccessItemListResource(
        toCollectionModel(entities).getContent(), pageMetadata);
  }

  public WorkbasketAccessItemListResource toCollectionModel(
      String workbasketId, List<WorkbasketAccessItem> entities)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketAccessItemListResource accessItemListResource =
        new WorkbasketAccessItemListResource(super.toCollectionModel(entities).getContent(), null);
    accessItemListResource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(workbasketId))
            .withSelfRel());
    accessItemListResource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
            .withRel("workbasket"));
    return accessItemListResource;
  }
}
