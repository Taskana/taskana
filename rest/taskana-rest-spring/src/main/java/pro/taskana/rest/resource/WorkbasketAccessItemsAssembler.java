package pro.taskana.rest.resource;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.rest.WorkbasketAccessItemController;

/**
 * Mapper to convert from a list of WorkbasketAccessItem to a WorkbasketAccessItemResource.
 */
public class WorkbasketAccessItemsAssembler
    extends ResourceAssemblerSupport<WorkbasketAccessItem, WorkbasketAccessItemResource> {

    public WorkbasketAccessItemsAssembler() {
        super(WorkbasketAccessItemController.class, WorkbasketAccessItemResource.class);
    }

    @Override
    public WorkbasketAccessItemResource toResource(WorkbasketAccessItem workbasketAccessItem) {
        WorkbasketAccessItemResource resource = createResourceWithId(workbasketAccessItem.getId(),
            workbasketAccessItem);
        resource.removeLinks();
        BeanUtils.copyProperties(workbasketAccessItem, resource);
        resource.setAccessItemId(workbasketAccessItem.getId());
        return resource;
    }

}
