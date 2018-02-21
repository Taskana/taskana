package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import pro.taskana.Workbasket;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketResource;

public class WorkbasketMapper {

    public WorkbasketResource toResource(Workbasket wb) {
        WorkbasketResource resource = new WorkbasketResource(wb.getId(), wb.getKey(), wb.getName(), wb.getDomain(),
            wb.getType(), wb.getCreated(),
            wb.getModified(), wb.getDescription(), wb.getOwner(), wb.getCustom1(), wb.getCustom2(), wb.getCustom3(),
            wb.getCustom4(),
            wb.getOrgLevel1(), wb.getOrgLevel2(), wb.getOrgLevel3(), wb.getOrgLevel4());

        // Add self-decription link to hateoas
        resource.add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(wb.getId())).withSelfRel());
        return resource;
    }
}
