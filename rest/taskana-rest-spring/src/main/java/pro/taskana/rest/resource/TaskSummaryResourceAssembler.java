package pro.taskana.rest.resource;

import java.util.List;

import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.links.PageLinks;

/**
 * Resource assembler for {@link TaskSummaryResource}.
 */
@Component
public class TaskSummaryResourceAssembler
    extends ResourceAssemblerSupport<TaskSummary, TaskSummaryResource> {

    public TaskSummaryResourceAssembler() {
        super(TaskController.class, TaskSummaryResource.class);
    }

    @Override
    public TaskSummaryResource toResource(TaskSummary taskSummary) {
        TaskSummaryResource resource = null;
        try {
            resource = new TaskSummaryResource(taskSummary);
        } catch (InvalidArgumentException e) {
            throw new SystemException("caught unexpected Exception.", e.getCause());
        } finally {
            return resource;
        }
    }

    @PageLinks(TaskController.class)
    public PagedResources<TaskSummaryResource> toResources(List<TaskSummary> taskSummaries,
        PagedResources.PageMetadata pageMetadata) {
        return new PagedResources<>(toResources(taskSummaries), pageMetadata);
    }

}
