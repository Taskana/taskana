package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Arrays;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

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

    WorkbasketAccessItemResource addLinks(WorkbasketAccessItemResource resource, WorkbasketAccessItem wbAccItem)
        throws NotAuthorizedException {
        resource.add(
            linkTo(methodOn(WorkbasketController.class).getWorkbasketAuthorizations(wbAccItem.getWorkbasketId()))
                .withRel("getWorkbasketAuthorizations"));
        resource.add(
            linkTo(methodOn(WorkbasketController.class).createWorkbasketAuthorization(resource))
                .withRel("createWorkbasketAuthorization"));
        resource.add(
            linkTo(methodOn(WorkbasketController.class).updateWorkbasketAuthorization(wbAccItem.getId(), resource))
                .withRel("updateWorkbasketAuthorization"));
        resource.add(
            linkTo(methodOn(WorkbasketController.class).setWorkbasketAuthorizations(wbAccItem.getWorkbasketId(),
                Arrays.asList(resource)))
                    .withRel("setWorkbasketAuthorizations"));
        resource.add(
            linkTo(methodOn(WorkbasketController.class).deleteWorkbasketAuthorization(wbAccItem.getId()))
                .withRel("deleteWorkbasketAuthorization"));
        return resource;
    }
}
