package pro.taskana.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.TaskRepresentationModel.CustomAttribute;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;

/**
 * EntityModel assembler for {@link TaskRepresentationModel}.
 */
@Component
public class TaskRepresentationModelAssembler
    extends RepresentationModelAssemblerSupport<Task, TaskRepresentationModel> {

  private final TaskService taskService;

  private final ClassificationSummaryRepresentationModelAssembler classificationAssembler;

  private final WorkbasketSummaryRepresentationModelAssembler
      workbasketSummaryRepresentationModelAssembler;

  private final AttachmentRepresentationModelAssembler attachmentAssembler;

  @Autowired
  public TaskRepresentationModelAssembler(
      TaskService taskService,
      ClassificationSummaryRepresentationModelAssembler classificationAssembler,
      WorkbasketSummaryRepresentationModelAssembler workbasketSummaryRepresentationModelAssembler,
      AttachmentRepresentationModelAssembler attachmentAssembler) {
    super(TaskController.class, TaskRepresentationModel.class);
    this.taskService = taskService;
    this.classificationAssembler = classificationAssembler;
    this.workbasketSummaryRepresentationModelAssembler
        = workbasketSummaryRepresentationModelAssembler;
    this.attachmentAssembler = attachmentAssembler;
  }

  @NonNull
  @Override
  public TaskRepresentationModel toModel(@NonNull Task task) {
    TaskRepresentationModel resource;
    try {
      resource = new TaskRepresentationModel(task);
      resource.add(linkTo(methodOn(TaskController.class).getTask(task.getId())).withSelfRel());
    } catch (InvalidArgumentException | TaskNotFoundException | NotAuthorizedException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return resource;
  }

  public Task toEntityModel(TaskRepresentationModel resource) throws InvalidArgumentException {
    validateTaskResource(resource);
    TaskImpl task =
        (TaskImpl)
            taskService.newTask(
                resource.getWorkbasketSummary().getWorkbasketId());
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
        classificationAssembler.toEntityModel(
            resource.getClassificationSummary()));
    task.setWorkbasketSummary(
        workbasketSummaryRepresentationModelAssembler
            .toEntityModel(resource.getWorkbasketSummary()));
    task.setAttachments(attachmentAssembler.toAttachmentList(resource.getAttachments()));
    task.setCustomAttributes(
        resource.getCustomAttributes().stream()
            .filter(e -> Objects.nonNull(e.getKey()) && !e.getKey().isEmpty())
            .collect(Collectors.toMap(CustomAttribute::getKey, CustomAttribute::getValue)));
    task.setCallbackInfo(
        resource.getCallbackInfo().stream()
            .filter(e -> Objects.nonNull(e.getKey()) && !e.getKey().isEmpty())
            .collect(Collectors.toMap(CustomAttribute::getKey, CustomAttribute::getValue)));

    return task;
  }

  private void validateTaskResource(TaskRepresentationModel resource)
      throws InvalidArgumentException {
    if (resource.getWorkbasketSummary() == null
            || resource.getWorkbasketSummary().getWorkbasketId() == null
            || resource.getWorkbasketSummary().getWorkbasketId().isEmpty()) {
      throw new InvalidArgumentException(
          "TaskResource must have a workbasket summary with a valid workbasketId.");
    }
    if (resource.getClassificationSummary() == null
            || resource.getClassificationSummary().getKey() == null
            || resource.getClassificationSummary().getKey().isEmpty()) {
      throw new InvalidArgumentException(
          "TaskResource must have a classification summary with a valid classification key.");
    }
  }
}
