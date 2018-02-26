package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

@Component
public class WorkbasketAccessItemMapper {

    @Autowired
    private WorkbasketService workbasketService;

    public WorkbasketAccessItemResource toResource(WorkbasketAccessItem wbAccItem) {
        WorkbasketAccessItemResource resource = new WorkbasketAccessItemResource();
        BeanUtils.copyProperties(wbAccItem, resource);
        //property is named different, so it needs to be set by hand
        resource.setAccessItemId(wbAccItem.getId());

        // Add self-decription link to hateoas
        resource.add(
            linkTo(methodOn(WorkbasketController.class).getWorkbasketAuthorizations(wbAccItem.getWorkbasketId()))
                .withSelfRel());
        return resource;
    }

    public WorkbasketAccessItem toModel(WorkbasketAccessItemResource wbAccItemRecource) {
        WorkbasketAccessItemImpl wbAccItemModel = (WorkbasketAccessItemImpl) workbasketService.newWorkbasketAccessItem(
            wbAccItemRecource.workbasketId, wbAccItemRecource.accessId);
        BeanUtils.copyProperties(wbAccItemRecource, wbAccItemModel);

        wbAccItemModel.setId(wbAccItemRecource.accessItemId);
        return wbAccItemModel;
    }

}
