package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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

/** Resource assembler for {@link TaskResource}. */
@Component
public class TaskResourceAssembler extends ResourceAssemblerSupport<Task, TaskResource> {

  private final TaskService taskService;

  private final ClassificationSummaryResourceAssembler classificationAssembler;

  private final WorkbasketSummaryResourceAssembler workbasketAssembler;

  private final AttachmentResourceAssembler attachmentAssembler;

  @Autowired
  public TaskResourceAssembler(
      TaskService taskService,
      ClassificationSummaryResourceAssembler classificationAssembler,
      WorkbasketSummaryResourceAssembler workbasketAssembler,
      AttachmentResourceAssembler attachmentAssembler) {
    super(TaskController.class, TaskResource.class);
    this.taskService = taskService;
    this.classificationAssembler = classificationAssembler;
    this.workbasketAssembler = workbasketAssembler;
    this.attachmentAssembler = attachmentAssembler;
  }

  @Override
  public TaskResource toResource(Task task) {
    TaskResource resource;
    try {
      resource = new TaskResource(task);
      resource.add(linkTo(methodOn(TaskController.class).getTask(task.getId())).withSelfRel());
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return resource;
  }

  public Task toModel(TaskResource resource) throws InvalidArgumentException {
    validateTaskResource(resource);
    TaskImpl task =
        (TaskImpl) taskService.newTask(resource.getWorkbasketSummaryResource().getWorkbasketId());
    task.setId(resource.getTaskId());
    task.setExternalId(resource.getExternalId());
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
    task.setClassificationSummary(
        classificationAssembler.toModel(resource.getClassificationSummaryResource()));
    task.setWorkbasketSummary(workbasketAssembler.toModel(resource.getWorkbasketSummaryResource()));
    task.setAttachments(attachmentAssembler.toModel(resource.getAttachments()));
    task.setCustomAttributes(
        resource.getCustomAttributes().stream()
            .filter(e -> Objects.nonNull(e.getKey()) && !e.getKey().isEmpty())
            .collect(
                Collectors.toMap(
                    TaskResource.CustomAttribute::getKey, TaskResource.CustomAttribute::getValue)));
    task.setCallbackInfo(
        resource.getCallbackInfo().stream()
            .filter(e -> Objects.nonNull(e.getKey()) && !e.getKey().isEmpty())
            .collect(
                Collectors.toMap(
                    TaskResource.CustomAttribute::getKey, TaskResource.CustomAttribute::getValue)));

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
