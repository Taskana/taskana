package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketResource;

/**
 * Transforms {@link Workbasket} to its resource counterpart {@link WorkbasketResource} and vice versa.
 */
@Component
public class WorkbasketMapper {

    @Autowired
    private WorkbasketService workbasketService;

    public WorkbasketResource toResource(Workbasket wb) throws NotAuthorizedException {
        WorkbasketResource resource = new WorkbasketResource();
        BeanUtils.copyProperties(wb, resource);
        // need to be set by hand, since name or type is different
        resource.setWorkbasketId(wb.getId());
        resource.setModified(wb.getModified().toString());
        resource.setCreated(wb.getCreated().toString());

        return addLinks(resource, wb);
    }

    public Workbasket toModel(WorkbasketResource wbResource) throws NotAuthorizedException {
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket(wbResource.key, wbResource.domain);
        BeanUtils.copyProperties(wbResource, workbasket);

        workbasket.setId(wbResource.workbasketId);
        workbasket.setModified(Instant.parse(wbResource.modified));
        workbasket.setCreated(Instant.parse(wbResource.created));
        return workbasket;
    }

    private WorkbasketResource addLinks(WorkbasketResource resource, Workbasket wb) throws NotAuthorizedException {
        resource.add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(wb.getId())).withSelfRel());
        resource.add(linkTo(methodOn(WorkbasketController.class).getDistributionTargetsForWorkbasketId(wb.getId()))
            .withRel("distributionTargets"));
        resource.add(linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(wb.getId()))
            .withRel("accessItems"));
        return resource;
    }
}
