package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collections;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

/**
 * Transforms {@link WorkbasketAccessItem} to its resource counterpart {@link WorkbasketAccessItemResource} and vice
 * versa.
 */
@Component
public class WorkbasketAccessItemMapper {

    @Autowired
    private WorkbasketService workbasketService;

    public WorkbasketAccessItemResource toResource(WorkbasketAccessItem wbAccItem) throws NotAuthorizedException {
        WorkbasketAccessItemResource resource = new WorkbasketAccessItemResource();
        BeanUtils.copyProperties(wbAccItem, resource);
        // property is named different, so it needs to be set by hand
        resource.setAccessItemId(wbAccItem.getId());

        return addLinks(resource, wbAccItem);
    }

    public WorkbasketAccessItem toModel(WorkbasketAccessItemResource wbAccItemRecource) {
        WorkbasketAccessItemImpl wbAccItemModel = (WorkbasketAccessItemImpl) workbasketService.newWorkbasketAccessItem(
            wbAccItemRecource.workbasketId, wbAccItemRecource.accessId);
        BeanUtils.copyProperties(wbAccItemRecource, wbAccItemModel);

        wbAccItemModel.setId(wbAccItemRecource.accessItemId);
        return wbAccItemModel;
    }

    private WorkbasketAccessItemResource addLinks(WorkbasketAccessItemResource resource, WorkbasketAccessItem wbAccItem)
        throws NotAuthorizedException {
        resource.add(
            linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(wbAccItem.getWorkbasketId()))
                .withRel("getWorkbasketAccessItems"));
        resource.add(
            linkTo(methodOn(WorkbasketController.class).createWorkbasketAccessItem(resource))
                .withRel("createWorkbasketAccessItem"));
        resource.add(
            linkTo(methodOn(WorkbasketController.class).updateWorkbasketAccessItem(wbAccItem.getId(), resource))
                .withRel("updateWorkbasketAccessItem"));
        resource.add(
            linkTo(methodOn(WorkbasketController.class).setWorkbasketAccessItems(wbAccItem.getWorkbasketId(),
                Collections.singletonList(resource)))
                    .withRel("setWorkbasketAccessItems"));
        resource.add(
            linkTo(methodOn(WorkbasketController.class).deleteWorkbasketAccessItem(wbAccItem.getId()))
                .withRel("deleteWorkbasketAccessItem"));
        return resource;
    }
}
