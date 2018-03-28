package pro.taskana.rest.resource.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import pro.taskana.Task;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.TaskResource;

/**
 * Resource assembler for {@link TaskResource}.
 */
public class TaskResourceAssembler
    extends ResourceAssemblerSupport<Task, TaskResource> {

    private WorkbasketSummaryResourceAssembler workbasketAssembler = new WorkbasketSummaryResourceAssembler();
    private ClassificationSummaryResourceAssembler classificationAssembler = new ClassificationSummaryResourceAssembler();

    public TaskResourceAssembler() {
        super(TaskController.class, TaskResource.class);
    }

    @Override
    public TaskResource toResource(Task task) {
        TaskResource resource = createResourceWithId(task.getId(), task);
        BeanUtils.copyProperties(task, resource);
        resource.setTaskId(task.getId());
        if (task.getCreated() != null) {
            resource.setCreated(task.getCreated().toString());
        }
        if (task.getModified() != null) {
            resource.setModified(task.getModified().toString());
        }
        if (task.getClaimed() != null) {
            resource.setClaimed(task.getClaimed().toString());
        }
        if (task.getCompleted() != null) {
            resource.setCompleted(task.getCompleted().toString());
        }
        if (task.getDue() != null) {
            resource.setDue(task.getDue().toString());
        }
        resource.setClassificationSummaryResource(
            classificationAssembler.toResource(task.getClassificationSummary()));
        resource.setWorkbasketSummaryResource(workbasketAssembler.toResource(task.getWorkbasketSummary()));
        return resource;
    }

}
