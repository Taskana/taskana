package pro.taskana.task.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.rest.TaskController;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel.CustomAttribute;
import pro.taskana.workbasket.rest.assembler.WorkbasketSummaryRepresentationModelAssembler;

/** EntityModel assembler for {@link TaskRepresentationModel}. */
@Component
public class TaskRepresentationModelAssembler
    implements RepresentationModelAssembler<Task, TaskRepresentationModel> {

  private final TaskService taskService;
  private final ClassificationSummaryRepresentationModelAssembler classificationAssembler;
  private final WorkbasketSummaryRepresentationModelAssembler workbasketAssembler;
  private final AttachmentRepresentationModelAssembler attachmentAssembler;

  @Autowired
  public TaskRepresentationModelAssembler(
      TaskService taskService,
      ClassificationSummaryRepresentationModelAssembler classificationAssembler,
      WorkbasketSummaryRepresentationModelAssembler workbasketAssembler,
      AttachmentRepresentationModelAssembler attachmentAssembler) {
    this.taskService = taskService;
    this.classificationAssembler = classificationAssembler;
    this.workbasketAssembler = workbasketAssembler;
    this.attachmentAssembler = attachmentAssembler;
  }

  @NonNull
  @Override
  public TaskRepresentationModel toModel(@NonNull Task task) {
    TaskRepresentationModel repModel = new TaskRepresentationModel();
    repModel.setTaskId(task.getId());
    repModel.setExternalId(task.getExternalId());
    repModel.setCreated(task.getCreated());
    repModel.setClaimed(task.getClaimed());
    repModel.setCompleted(task.getCompleted());
    repModel.setModified(task.getModified());
    repModel.setPlanned(task.getPlanned());
    repModel.setDue(task.getDue());
    repModel.setName(task.getName());
    repModel.setCreator(task.getCreator());
    repModel.setNote(task.getNote());
    repModel.setDescription(task.getDescription());
    repModel.setPriority(task.getPriority());
    repModel.setState(task.getState());
    repModel.setClassificationSummary(
        classificationAssembler.toModel(task.getClassificationSummary()));
    repModel.setWorkbasketSummary(workbasketAssembler.toModel(task.getWorkbasketSummary()));
    repModel.setBusinessProcessId(task.getBusinessProcessId());
    repModel.setParentBusinessProcessId(task.getParentBusinessProcessId());
    repModel.setOwner(task.getOwner());
    repModel.setPrimaryObjRef(task.getPrimaryObjRef());
    repModel.setRead(task.isRead());
    repModel.setTransferred(task.isTransferred());
    repModel.setAttachments(
        task.getAttachments().stream()
            .map(attachmentAssembler::toModel)
            .collect(Collectors.toList()));
    repModel.setCustomAttributes(
        task.getCustomAttributes().entrySet().stream()
            .map(CustomAttribute::of)
            .collect(Collectors.toList()));
    repModel.setCallbackInfo(
        task.getCallbackInfo().entrySet().stream()
            .map(CustomAttribute::of)
            .collect(Collectors.toList()));
    try {
      repModel.setCustom1(task.getCustomAttribute("1"));
      repModel.setCustom2(task.getCustomAttribute("2"));
      repModel.setCustom3(task.getCustomAttribute("3"));
      repModel.setCustom4(task.getCustomAttribute("4"));
      repModel.setCustom5(task.getCustomAttribute("5"));
      repModel.setCustom6(task.getCustomAttribute("6"));
      repModel.setCustom7(task.getCustomAttribute("7"));
      repModel.setCustom8(task.getCustomAttribute("8"));
      repModel.setCustom9(task.getCustomAttribute("9"));
      repModel.setCustom10(task.getCustomAttribute("10"));
      repModel.setCustom11(task.getCustomAttribute("11"));
      repModel.setCustom12(task.getCustomAttribute("12"));
      repModel.setCustom13(task.getCustomAttribute("13"));
      repModel.setCustom14(task.getCustomAttribute("14"));
      repModel.setCustom15(task.getCustomAttribute("15"));
      repModel.setCustom16(task.getCustomAttribute("16"));
      repModel.add(linkTo(methodOn(TaskController.class).getTask(task.getId())).withSelfRel());
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return repModel;
  }

  public Task toEntityModel(TaskRepresentationModel repModel) {
    TaskImpl task =
        (TaskImpl) taskService.newTask(repModel.getWorkbasketSummary().getWorkbasketId());
    task.setId(repModel.getTaskId());
    task.setExternalId(repModel.getExternalId());
    task.setCreated(repModel.getCreated());
    task.setClaimed(repModel.getClaimed());
    task.setCompleted(repModel.getCompleted());
    task.setModified(repModel.getModified());
    task.setPlanned(repModel.getPlanned());
    task.setDue(repModel.getDue());
    task.setName(repModel.getName());
    task.setCreator(repModel.getCreator());
    task.setNote(repModel.getNote());
    task.setDescription(repModel.getDescription());
    task.setPriority(repModel.getPriority());
    task.setState(repModel.getState());
    task.setClassificationSummary(
        classificationAssembler.toEntityModel(repModel.getClassificationSummary()));
    task.setWorkbasketSummary(workbasketAssembler.toEntityModel(repModel.getWorkbasketSummary()));
    task.setBusinessProcessId(repModel.getBusinessProcessId());
    task.setParentBusinessProcessId(repModel.getParentBusinessProcessId());
    task.setOwner(repModel.getOwner());
    task.setPrimaryObjRef(repModel.getPrimaryObjRef());
    task.setRead(repModel.isRead());
    task.setTransferred(repModel.isTransferred());
    task.setCustom1(repModel.getCustom1());
    task.setCustom2(repModel.getCustom2());
    task.setCustom3(repModel.getCustom3());
    task.setCustom4(repModel.getCustom4());
    task.setCustom5(repModel.getCustom5());
    task.setCustom6(repModel.getCustom6());
    task.setCustom7(repModel.getCustom7());
    task.setCustom8(repModel.getCustom8());
    task.setCustom9(repModel.getCustom9());
    task.setCustom10(repModel.getCustom10());
    task.setCustom11(repModel.getCustom11());
    task.setCustom12(repModel.getCustom12());
    task.setCustom13(repModel.getCustom13());
    task.setCustom14(repModel.getCustom14());
    task.setCustom15(repModel.getCustom15());
    task.setCustom16(repModel.getCustom16());
    task.setAttachments(
        repModel.getAttachments().stream()
            .map(attachmentAssembler::toEntityModel)
            .collect(Collectors.toList()));
    task.setCustomAttributes(
        repModel.getCustomAttributes().stream()
            .filter(e -> Objects.nonNull(e.getKey()) && !e.getKey().isEmpty())
            .collect(Collectors.toMap(CustomAttribute::getKey, CustomAttribute::getValue)));
    task.setCallbackInfo(
        repModel.getCallbackInfo().stream()
            .filter(e -> Objects.nonNull(e.getKey()) && !e.getKey().isEmpty())
            .collect(Collectors.toMap(CustomAttribute::getKey, CustomAttribute::getValue)));
    return task;
  }
}
