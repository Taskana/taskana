package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

/**
 * Mapper to convert from a list of WorkbasketSummary to a workbasket list resource.
 */
@Component
public class WorkbasketListMapper {

    @Autowired
    private WorkbasketSummaryMapper workbasketSummaryMapper;

    public Resources<WorkbasketSummaryResource> toResource(Collection<WorkbasketSummary> workbasketSummaries) {
        List<WorkbasketSummaryResource> resourceList = workbasketSummaries.stream()
            .map(workbasket -> workbasketSummaryMapper.toResource(workbasket))
            .collect(Collectors.toList());
        Resources<WorkbasketSummaryResource> workbasketListResource = new Resources<>(resourceList);

        workbasketListResource.add(linkTo(WorkbasketController.class).withSelfRel());

        return workbasketListResource;
    }

}
