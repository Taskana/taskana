package pro.taskana.rest.resource;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.links.PageLinks;

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
        return new WorkbasketSummaryResource(workbasketSummary);
    }

    @PageLinks(WorkbasketController.class)
    public PagedResources<WorkbasketSummaryResource> toResources(List<WorkbasketSummary> entities,
        PagedResources.PageMetadata pageMetadata) {
        return new PagedResources<>(toResources(entities), pageMetadata);
    }

    public WorkbasketSummary toModel(WorkbasketSummaryResource resource) {
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService
            .newWorkbasket(resource.getKey(), resource.getDomain());
        workbasket.setId(resource.getWorkbasketId());
        BeanUtils.copyProperties(resource, workbasket);
        return workbasket.asSummary();
    }

}
