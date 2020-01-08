package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.resource.links.PageLinks;

/**
 * Transforms {@link WorkbasketAccessItem} to its resource counterpart {@link
 * WorkbasketAccessItemResource} and vice versa.
 */
@Component
public class WorkbasketAccessItemResourceAssembler
    extends ResourceAssemblerSupport<WorkbasketAccessItem, WorkbasketAccessItemResource> {

  @Autowired private WorkbasketService workbasketService;

  public WorkbasketAccessItemResourceAssembler() {
    super(WorkbasketController.class, WorkbasketAccessItemResource.class);
  }

  public WorkbasketAccessItemResource toResource(WorkbasketAccessItem wbAccItem) {
    return new WorkbasketAccessItemResource(wbAccItem);
  }

  public WorkbasketAccessItem toModel(WorkbasketAccessItemResource wbAccItemResource) {
    WorkbasketAccessItemImpl wbAccItemModel =
        (WorkbasketAccessItemImpl)
            workbasketService.newWorkbasketAccessItem(
                wbAccItemResource.workbasketId, wbAccItemResource.accessId);
    BeanUtils.copyProperties(wbAccItemResource, wbAccItemModel);

    wbAccItemModel.setId(wbAccItemResource.accessItemId);
    return wbAccItemModel;
  }

  @PageLinks(Mapping.URL_WORKBASKETACCESSITEMS)
  public WorkbasketAccessItemPaginatedListResource toResources(
      List<WorkbasketAccessItem> entities, PageMetadata pageMetadata) {
    return new WorkbasketAccessItemPaginatedListResource(toResources(entities), pageMetadata);
  }

  public WorkbasketAccessItemPaginatedListResource toResources(
      String workbasketId, List<WorkbasketAccessItem> entities)
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketAccessItemPaginatedListResource accessItemListResource =
        new WorkbasketAccessItemPaginatedListResource(super.toResources(entities), null);
    accessItemListResource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(workbasketId))
            .withSelfRel());
    accessItemListResource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
            .withRel("workbasket"));
    return accessItemListResource;
  }
}
