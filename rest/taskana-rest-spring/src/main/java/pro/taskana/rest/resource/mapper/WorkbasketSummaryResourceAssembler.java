package pro.taskana.rest.resource.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

/**
 * @author HH
 */
public class WorkbasketSummaryResourceAssembler
    extends ResourceAssemblerSupport<WorkbasketSummary, WorkbasketSummaryResource> {

    public WorkbasketSummaryResourceAssembler() {
        super(WorkbasketController.class, WorkbasketSummaryResource.class);
    }

    @Override
    public WorkbasketSummaryResource toResource(WorkbasketSummary workbasketSummary) {
        WorkbasketSummaryResource resource = createResourceWithId(workbasketSummary.getId(), workbasketSummary);
        BeanUtils.copyProperties(workbasketSummary, resource);
        // named different so needs to be set by hand
        resource.setWorkbasketId(workbasketSummary.getId());
        return resource;
    }

}
