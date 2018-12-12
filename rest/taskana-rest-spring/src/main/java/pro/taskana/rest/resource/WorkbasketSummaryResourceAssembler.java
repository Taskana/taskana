package pro.taskana.rest.resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.rest.WorkbasketController;

/**
 * @author HH
 */
@Component
public class WorkbasketSummaryResourceAssembler
    extends ResourceAssemblerSupport<WorkbasketSummary, WorkbasketSummaryResource> {

    @Autowired
    private WorkbasketService workbasketService;

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

    public WorkbasketSummary toModel(WorkbasketSummaryResource resource) {
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService
            .newWorkbasket(resource.getKey(), resource.getDomain());
        workbasket.setId(resource.getWorkbasketId());
        BeanUtils.copyProperties(resource, workbasket);
        return workbasket.asSummary();
    }

}
