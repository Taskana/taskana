package pro.taskana.rest.resource.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import pro.taskana.TaskSummary;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.TaskSummaryResource;

/**
 * Resource assembler for {@link TaskSummaryResource}.
 */
public class TaskSummaryResourceAssembler
    extends ResourceAssemblerSupport<TaskSummary, TaskSummaryResource> {

    private WorkbasketSummaryResourceAssembler workbasketAssembler = new WorkbasketSummaryResourceAssembler();
    private ClassificationSummaryResourceAssembler classificationAssembler = new ClassificationSummaryResourceAssembler();

    public TaskSummaryResourceAssembler() {
        super(TaskController.class, TaskSummaryResource.class);
    }

    @Override
    public TaskSummaryResource toResource(TaskSummary taskSummary) {
        TaskSummaryResource resource = createResourceWithId(taskSummary.getTaskId(), taskSummary);
        BeanUtils.copyProperties(taskSummary, resource);
        if (taskSummary.getCreated() != null) {
            resource.setCreated(taskSummary.getCreated().toString());
        }
        if (taskSummary.getModified() != null) {
            resource.setModified(taskSummary.getModified().toString());
        }
        if (taskSummary.getClaimed() != null) {
            resource.setClaimed(taskSummary.getClaimed().toString());
        }
        if (taskSummary.getCompleted() != null) {
            resource.setCompleted(taskSummary.getCompleted().toString());
        }
        if (taskSummary.getDue() != null) {
            resource.setDue(taskSummary.getDue().toString());
        }
        resource.setClassificationSummaryResource(
            classificationAssembler.toResource(taskSummary.getClassificationSummary()));
        resource.setWorkbasketSummaryResource(workbasketAssembler.toResource(taskSummary.getWorkbasketSummary()));
        return resource;
    }

}
