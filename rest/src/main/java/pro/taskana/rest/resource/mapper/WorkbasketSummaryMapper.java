package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

@Component
public class WorkbasketSummaryMapper {

    public WorkbasketSummaryResource toResource(WorkbasketSummary summary) {
        WorkbasketSummaryResource resource = new WorkbasketSummaryResource(
            summary.getId(), summary.getKey(), summary.getName(), summary.getDescription(), summary.getOwner(),
            summary.getDomain(), summary.getType(), summary.getOrgLevel1(), summary.getOrgLevel2(),
            summary.getOrgLevel3(), summary.getOrgLevel4());

        // Add self reference
        resource.add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(summary.getId())).withSelfRel());

        return resource;
    }

}
