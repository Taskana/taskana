package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.rest.TaskController;

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

    @Autowired
    private AttachmentResourceAssembler attachmentAssembler;

    public TaskResourceAssembler() {
        super(TaskController.class, TaskResource.class);
    }

    @Override
    public TaskResource toResource(Task task) {
        TaskResource resource;
        try {
            resource = new TaskResource(task);
            resource.add(linkTo(TaskController.class).slash(task.getId()).withSelfRel());
        } catch (InvalidArgumentException e) {
            throw new SystemException("caught unexpected Exception.", e.getCause());
        }
        return resource;
    }

    public Task toModel(TaskResource resource) throws InvalidArgumentException {
        validateTaskResource(resource);
        TaskImpl task = (TaskImpl) taskService.newTask(resource.getWorkbasketSummaryResource().getWorkbasketId());
        task.setId(resource.getTaskId());
        task.setExternalId(resource.getExternalId());
        BeanUtils.copyProperties(resource, task);
        if (resource.getCreated() != null) {
            task.setCreated(Instant.parse(resource.getCreated()));
        }
        if (resource.getModified() != null) {
            task.setModified(Instant.parse(resource.getModified().toString()));
        }
        if (resource.getClaimed() != null) {
            task.setClaimed(Instant.parse(resource.getClaimed().toString()));
        }
        if (resource.getCompleted() != null) {
            task.setCompleted(Instant.parse(resource.getCompleted().toString()));
        }
        if (resource.getDue() != null) {
            task.setDue(Instant.parse(resource.getDue().toString()));
        }
        task.setClassificationSummary(classificationAssembler.toModel(resource.getClassificationSummaryResource()));
        task.setWorkbasketSummary(workbasketAssembler.toModel(resource.getWorkbasketSummaryResource()));
        task.setAttachments(attachmentAssembler.toModel(resource.getAttachments()));
        task.setCustomAttributes(resource.getCustomAttributes().stream()
            .filter(e -> Objects.nonNull(e.getKey()) && !e.getKey().isEmpty())
            .collect(Collectors.toMap(TaskResource.CustomAttribute::getKey, TaskResource.CustomAttribute::getValue)));
        task.setCallbackInfo(resource.getCallbackInfo().stream()
            .filter(e -> Objects.nonNull(e.getKey()) && !e.getKey().isEmpty())
            .collect(Collectors.toMap(TaskResource.CustomAttribute::getKey, TaskResource.CustomAttribute::getValue)));

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
