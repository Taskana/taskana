package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

public class WorkbasketAccessItemMapper {

    public WorkbasketAccessItemResource toResource(WorkbasketAccessItem wbAccItem) {
        WorkbasketAccessItemResource resource = new WorkbasketAccessItemResource(wbAccItem.getId(),
            wbAccItem.getWorkbasketKey(),
            wbAccItem.getAccessId(), wbAccItem.isPermRead(), wbAccItem.isPermOpen(), wbAccItem.isPermAppend(),
            wbAccItem.isPermTransfer(),
            wbAccItem.isPermDistribute(), wbAccItem.isPermCustom1(), wbAccItem.isPermCustom2(),
            wbAccItem.isPermCustom3(), wbAccItem.isPermCustom4(),
            wbAccItem.isPermCustom5(), wbAccItem.isPermCustom6(), wbAccItem.isPermCustom7(), wbAccItem.isPermCustom8(),
            wbAccItem.isPermCustom9(),
            wbAccItem.isPermCustom10(), wbAccItem.isPermCustom11(), wbAccItem.isPermCustom12());

        // Add self-decription link to hateoas
        resource.add(
            linkTo(methodOn(WorkbasketController.class).getWorkbasketAuthorizations(wbAccItem.getWorkbasketKey()))
                .withSelfRel());
        return resource;
    }
}
