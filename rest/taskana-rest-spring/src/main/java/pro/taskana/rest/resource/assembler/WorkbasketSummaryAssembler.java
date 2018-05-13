package pro.taskana.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

/**
 * Transforms {@link WorkbasketSummary} to its resource counterpart {@link WorkbasketSummaryResource} and vice versa.
 */
@Component
public class WorkbasketSummaryAssembler {

    public WorkbasketSummaryResource toResource(WorkbasketSummary summary)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        WorkbasketSummaryResource resource = new WorkbasketSummaryResource();
        BeanUtils.copyProperties(summary, resource);
        // named different so needs to be set by hand
        resource.setWorkbasketId(summary.getId());

        return addLinks(resource, summary);
    }

    private WorkbasketSummaryResource addLinks(WorkbasketSummaryResource resource, WorkbasketSummary summary)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        resource.add(linkTo(methodOn(WorkbasketController.class).getWorkbasket(summary.getId())).withSelfRel());
        return resource;
    }
}
