package pro.taskana.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

/**
 * Mapper to convert from a list of WorkbasketAccessItem to a WorkbasketAccessItemResource.
 */
@Component
public class WorkbasketAccessItemListAssembler {

    @Autowired
    private WorkbasketAccessItemAssembler workbasketAccessItemAssembler;

    public Resources<WorkbasketAccessItemResource> toResource(String workbasketId,
        Collection<WorkbasketAccessItem> accessItems) throws NotAuthorizedException, WorkbasketNotFoundException {
        List<WorkbasketAccessItemResource> resourceList = new ArrayList<>();
        for (WorkbasketAccessItem accessItem : accessItems) {
            resourceList.add(workbasketAccessItemAssembler.toResource(accessItem));
        }

        Resources<WorkbasketAccessItemResource> accessItemListResource = new Resources<>(resourceList);

        accessItemListResource
            .add(linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(workbasketId))
                .withSelfRel());
        accessItemListResource
            .add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
                .withRel("workbasket"));

        return accessItemListResource;
    }

}
