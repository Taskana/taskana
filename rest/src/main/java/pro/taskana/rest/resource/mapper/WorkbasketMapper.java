package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketResource;

@Component
public class WorkbasketMapper {

    @Autowired
    private WorkbasketService workbasketService;

    public WorkbasketResource toResource(Workbasket wb) {
        WorkbasketResource resource = new WorkbasketResource(wb.getId(), wb.getKey(), wb.getName(), wb.getDomain(),
            wb.getType(), wb.getCreated().toString(), wb.getModified().toString(), wb.getDescription(), wb.getOwner(),
            wb.getCustom1(),
            wb.getCustom2(), wb.getCustom3(),
            wb.getCustom4(),
            wb.getOrgLevel1(), wb.getOrgLevel2(), wb.getOrgLevel3(), wb.getOrgLevel4());

        // Add self-decription link to hateoas
        resource.add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(wb.getId())).withSelfRel());
        return resource;
    }

    public Workbasket toModel(WorkbasketResource wbResource) {
        WorkbasketImpl wbModel = (WorkbasketImpl) workbasketService.newWorkbasket(wbResource.key, wbResource.domain);
        wbModel.setId(wbResource.workbasketId);
        wbModel.setName(wbResource.name);
        wbModel.setType(wbResource.type);
        wbModel.setCreated(Instant.parse(wbResource.created));
        wbModel.setModified(Instant.parse(wbResource.modified));
        wbModel.setDescription(wbResource.description);
        wbModel.setOwner(wbResource.owner);
        wbModel.setCustom1(wbResource.custom1);
        wbModel.setCustom2(wbResource.custom2);
        wbModel.setCustom3(wbResource.custom3);
        wbModel.setCustom4(wbResource.custom4);
        wbModel.setOrgLevel1(wbResource.orgLevel1);
        wbModel.setOrgLevel2(wbResource.orgLevel2);
        wbModel.setOrgLevel3(wbResource.orgLevel3);
        wbModel.setOrgLevel4(wbResource.orgLevel4);
        return wbModel;
    }
}
