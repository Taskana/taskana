package pro.taskana.task.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.rest.TaskController;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel.CustomAttribute;
import pro.taskana.workbasket.rest.assembler.WorkbasketSummaryRepresentationModelAssembler;

/**
 * EntityModel assembler for {@link TaskRepresentationModel}.
 */
@Component
public class TaskRepresentationModelAssembler
    implements RepresentationModelAssembler<Task, TaskRepresentationModel> {

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

  public Task toEntityModel(TaskRepresentationModel resource) {
    TaskImpl task =
        (TaskImpl)
            taskService.newTask(
                resource.getWorkbasketSummary().getWorkbasketId());
    task.setId(resource.getTaskId());
    task.setExternalId(resource.getExternalId());
    BeanUtils.copyProperties(resource, task);

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
}
