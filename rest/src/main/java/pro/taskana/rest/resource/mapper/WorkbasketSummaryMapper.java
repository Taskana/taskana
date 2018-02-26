package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

@Component
public class WorkbasketSummaryMapper {

    public WorkbasketSummaryResource toResource(WorkbasketSummary summary) {
        WorkbasketSummaryResource resource = new WorkbasketSummaryResource();
        BeanUtils.copyProperties(summary, resource);
        //named different so needs to be set by hand
        resource.setWorkbasketId(summary.getId());

        // Add self reference
        resource.add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(summary.getId())).withSelfRel());
        return resource;
    }

}
