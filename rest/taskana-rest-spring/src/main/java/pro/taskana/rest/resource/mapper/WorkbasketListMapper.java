package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

/**
 * Mapper to convert from a list of WorkbasketSummary to a workbasket list resource.
 */
@Component
public class WorkbasketListMapper {

    @Autowired
    private WorkbasketSummaryMapper workbasketSummaryMapper;

    public Resources<WorkbasketSummaryResource> toResource(Collection<WorkbasketSummary> workbasketSummaries)
        throws WorkbasketNotFoundException, NotAuthorizedException {

        List<WorkbasketSummaryResource> resourceList = new ArrayList<>();
        for (WorkbasketSummary workbasket : workbasketSummaries) {
            resourceList.add(workbasketSummaryMapper.toResource(workbasket));
        }

        Resources<WorkbasketSummaryResource> workbasketListResource = new Resources<>(resourceList);

        workbasketListResource.add(linkTo(WorkbasketController.class).withSelfRel());

        return workbasketListResource;
    }

}
