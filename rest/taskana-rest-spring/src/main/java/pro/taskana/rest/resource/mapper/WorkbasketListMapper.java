package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketListResource;

/**
 * Mapper to convert from {@link Collection WorkbasketSummary} to {@link WorkbasketListResource}.
 */
@Component
public class WorkbasketListMapper {

    @Autowired
    private WorkbasketSummaryMapper workbasketSummaryMapper;

    public WorkbasketListResource toResource(Collection<WorkbasketSummary> workbasketSummaries) {
        WorkbasketListResource workbasketListResource = new WorkbasketListResource(workbasketSummaries.stream()
            .map(workbasket -> workbasketSummaryMapper.toResource(workbasket))
            .collect(Collectors.toList()));

        workbasketListResource.add(linkTo(WorkbasketController.class).withSelfRel());

        return workbasketListResource;
    }

}
