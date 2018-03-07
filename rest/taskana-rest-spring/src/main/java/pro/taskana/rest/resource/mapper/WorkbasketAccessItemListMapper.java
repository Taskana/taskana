package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

/**
 * Mapper to convert from a list of WorkbasketAccessItem to a WorkbasketAccessItemResource.
 */
@Component
public class WorkbasketAccessItemListMapper {

    @Autowired
    private WorkbasketAccessItemMapper workbasketAccessItemMapper;

    public Resources<WorkbasketAccessItemResource> toResource(String workbasketId,
        Collection<WorkbasketAccessItem> accessItems) {
        List<WorkbasketAccessItemResource> resourceList = accessItems.stream()
            .map(accessItem -> {
                try {
                    return workbasketAccessItemMapper.toResource(accessItem);
                } catch (NotAuthorizedException e) {
                    return null;
                }
            })
            .collect(Collectors.toList());
        Resources<WorkbasketAccessItemResource> accessItemListResource = new Resources<>(resourceList);

        accessItemListResource
            .add(linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(workbasketId))
                .withSelfRel());
        accessItemListResource
            .add(linkTo(methodOn(WorkbasketController.class).getWorkbasketAccessItems(workbasketId))
                .withRel("setWorkbasketAccessItemResourceList"));
        accessItemListResource
            .add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
                .withRel("workbasket"));

        return accessItemListResource;
    }

}
