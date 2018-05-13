package pro.taskana.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

/**
 * Transforms {@link WorkbasketAccessItem} to its resource counterpart {@link WorkbasketAccessItemResource} and vice
 * versa.
 */
@Component
public class WorkbasketAccessItemAssembler {

    @Autowired
    private WorkbasketService workbasketService;

    public WorkbasketAccessItemResource toResource(WorkbasketAccessItem wbAccItem)
        throws NotAuthorizedException, WorkbasketNotFoundException {
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
        throws NotAuthorizedException, WorkbasketNotFoundException {

        resource.add(
            linkTo(methodOn(WorkbasketController.class).getWorkbasket(wbAccItem.getWorkbasketId()))
                .withRel("workbasket"));
        return resource;
    }
}
