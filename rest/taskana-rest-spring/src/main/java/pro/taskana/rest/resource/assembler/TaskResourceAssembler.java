package pro.taskana.rest.resource.assembler;

import java.time.Instant;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.TaskResource;

/**
 * Resource assembler for {@link TaskResource}.
 */
@Component
public class TaskResourceAssembler
    extends ResourceAssemblerSupport<Task, TaskResource> {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClassificationSummaryResourceAssembler classificationAssembler;

    @Autowired
    private WorkbasketSummaryResourceAssembler workbasketAssembler;

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
        if (task.getPlanned() != null) {
            resource.setPlanned(task.getPlanned().toString());
        }
        resource.setClassificationSummaryResource(
            classificationAssembler.toResource(task.getClassificationSummary()));
        resource.setWorkbasketSummaryResource(workbasketAssembler.toResource(task.getWorkbasketSummary()));
        return resource;
    }

    public Task toModel(TaskResource resource) throws InvalidArgumentException {
        validateTaskResource(resource);
        TaskImpl task = (TaskImpl) taskService.newTask(resource.getWorkbasketSummaryResource().getWorkbasketId());
        task.setId(resource.getTaskId());
        BeanUtils.copyProperties(resource, task);
        if (resource.getCreated() != null) {
            task.setCreated(Instant.parse(resource.getCreated()));
        }
        if (resource.getModified() != null) {
            task.setModified(Instant.parse(resource.getModified()));
        }
        if (resource.getClaimed() != null) {
            task.setClaimed(Instant.parse(resource.getClaimed()));
        }
        if (resource.getCompleted() != null) {
            task.setCompleted(Instant.parse(resource.getCompleted()));
        }
        if (resource.getDue() != null) {
            task.setDue(Instant.parse(resource.getDue()));
        }
        if (resource.getPlanned() != null) {
            task.setPlanned(Instant.parse(resource.getPlanned()));
        }
        task.setClassificationSummary(classificationAssembler.toModel(resource.getClassificationSummaryResource()));
        task.setWorkbasketSummary(workbasketAssembler.toModel(resource.getWorkbasketSummaryResource()));
        return task;
    }

    private void validateTaskResource(TaskResource resource) throws InvalidArgumentException {
        if (resource.getWorkbasketSummaryResource() == null
            || resource.getWorkbasketSummaryResource().getWorkbasketId() == null
            || resource.getWorkbasketSummaryResource().getWorkbasketId().isEmpty()) {
            throw new InvalidArgumentException(
                "TaskResource must have a workbasket summary with a valid workbasketId.");
        }
        if (resource.getClassificationSummaryResource() == null
            || resource.getClassificationSummaryResource().getKey() == null
            || resource.getClassificationSummaryResource().getKey().isEmpty()) {
            throw new InvalidArgumentException(
                "TaskResource must have a classification summary with a valid classification key.");
        }
    }

}
