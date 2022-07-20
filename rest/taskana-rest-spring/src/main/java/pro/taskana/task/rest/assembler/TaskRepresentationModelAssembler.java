package pro.taskana.task.rest.assembler;

import static java.util.function.Predicate.not;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
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
  private final ObjectReferenceRepresentationModelAssembler objectReferenceAssembler;

  @Autowired
  public TaskRepresentationModelAssembler(
      TaskService taskService,
      ClassificationSummaryRepresentationModelAssembler classificationAssembler,
      WorkbasketSummaryRepresentationModelAssembler workbasketAssembler,
      AttachmentRepresentationModelAssembler attachmentAssembler,
      ObjectReferenceRepresentationModelAssembler objectReferenceAssembler) {
    this.taskService = taskService;
    this.classificationAssembler = classificationAssembler;
    this.workbasketAssembler = workbasketAssembler;
    this.attachmentAssembler = attachmentAssembler;
    this.objectReferenceAssembler = objectReferenceAssembler;
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
    repModel.setReceived(task.getReceived());
    repModel.setDue(task.getDue());
    repModel.setName(task.getName());
    repModel.setCreator(task.getCreator());
    repModel.setNote(task.getNote());
    repModel.setDescription(task.getDescription());
    repModel.setPriority(task.getPriority());
    repModel.setManualPriority(task.getManualPriority());
    repModel.setState(task.getState());
    repModel.setClassificationSummary(
        classificationAssembler.toModel(task.getClassificationSummary()));
    repModel.setWorkbasketSummary(workbasketAssembler.toModel(task.getWorkbasketSummary()));
    repModel.setBusinessProcessId(task.getBusinessProcessId());
    repModel.setParentBusinessProcessId(task.getParentBusinessProcessId());
    repModel.setOwner(task.getOwner());
    repModel.setOwnerLongName(task.getOwnerLongName());
    repModel.setPrimaryObjRef(objectReferenceAssembler.toModel(task.getPrimaryObjRef()));
    repModel.setSecondaryObjectReferences(
        task.getSecondaryObjectReferences().stream()
            .map(objectReferenceAssembler::toModel)
            .collect(Collectors.toList()));
    repModel.setRead(task.isRead());
    repModel.setTransferred(task.isTransferred());
    repModel.setAttachments(
        task.getAttachments().stream()
            .map(attachmentAssembler::toModel)
            .collect(Collectors.toList()));
    repModel.setCustomAttributes(
        task.getCustomAttributeMap().entrySet().stream()
            .map(CustomAttribute::of)
            .collect(Collectors.toList()));
    repModel.setCallbackInfo(
        task.getCallbackInfo().entrySet().stream()
            .map(CustomAttribute::of)
            .collect(Collectors.toList()));
    repModel.setCustom1(task.getCustomField(TaskCustomField.CUSTOM_1));
    repModel.setCustom2(task.getCustomField(TaskCustomField.CUSTOM_2));
    repModel.setCustom3(task.getCustomField(TaskCustomField.CUSTOM_3));
    repModel.setCustom4(task.getCustomField(TaskCustomField.CUSTOM_4));
    repModel.setCustom5(task.getCustomField(TaskCustomField.CUSTOM_5));
    repModel.setCustom6(task.getCustomField(TaskCustomField.CUSTOM_6));
    repModel.setCustom7(task.getCustomField(TaskCustomField.CUSTOM_7));
    repModel.setCustom8(task.getCustomField(TaskCustomField.CUSTOM_8));
    repModel.setCustom9(task.getCustomField(TaskCustomField.CUSTOM_9));
    repModel.setCustom10(task.getCustomField(TaskCustomField.CUSTOM_10));
    repModel.setCustom11(task.getCustomField(TaskCustomField.CUSTOM_11));
    repModel.setCustom12(task.getCustomField(TaskCustomField.CUSTOM_12));
    repModel.setCustom13(task.getCustomField(TaskCustomField.CUSTOM_13));
    repModel.setCustom14(task.getCustomField(TaskCustomField.CUSTOM_14));
    repModel.setCustom15(task.getCustomField(TaskCustomField.CUSTOM_15));
    repModel.setCustom16(task.getCustomField(TaskCustomField.CUSTOM_16));
    repModel.setCustomInt1(task.getCustomIntField(TaskCustomIntField.CUSTOM_INT_1));
    repModel.setCustomInt2(task.getCustomIntField(TaskCustomIntField.CUSTOM_INT_2));
    repModel.setCustomInt3(task.getCustomIntField(TaskCustomIntField.CUSTOM_INT_3));
    repModel.setCustomInt4(task.getCustomIntField(TaskCustomIntField.CUSTOM_INT_4));
    repModel.setCustomInt5(task.getCustomIntField(TaskCustomIntField.CUSTOM_INT_5));
    repModel.setCustomInt6(task.getCustomIntField(TaskCustomIntField.CUSTOM_INT_6));
    repModel.setCustomInt7(task.getCustomIntField(TaskCustomIntField.CUSTOM_INT_7));
    repModel.setCustomInt8(task.getCustomIntField(TaskCustomIntField.CUSTOM_INT_8));
    try {
      repModel.add(linkTo(methodOn(TaskController.class).getTask(task.getId())).withSelfRel());
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return repModel;
  }

  public Task toEntityModel(TaskRepresentationModel repModel) throws InvalidArgumentException {
    verifyCorrectCustomAttributesFormat(repModel);
    TaskImpl task = (TaskImpl) taskService.newTask();
    task.setId(repModel.getTaskId());
    task.setExternalId(repModel.getExternalId());
    task.setCreated(repModel.getCreated());
    task.setClaimed(repModel.getClaimed());
    task.setCompleted(repModel.getCompleted());
    task.setModified(repModel.getModified());
    task.setPlanned(repModel.getPlanned());
    task.setReceived(repModel.getReceived());
    task.setDue(repModel.getDue());
    task.setName(repModel.getName());
    task.setCreator(repModel.getCreator());
    task.setNote(repModel.getNote());
    task.setDescription(repModel.getDescription());
    task.setPriority(repModel.getPriority());
    task.setManualPriority(repModel.getManualPriority());
    task.setState(repModel.getState());
    if (repModel.getClassificationSummary() != null) {
      task.setClassificationSummary(
          classificationAssembler.toEntityModel(repModel.getClassificationSummary()));
    }
    if (repModel.getWorkbasketSummary() != null) {
      task.setWorkbasketSummary(workbasketAssembler.toEntityModel(repModel.getWorkbasketSummary()));
    }
    task.setBusinessProcessId(repModel.getBusinessProcessId());
    task.setParentBusinessProcessId(repModel.getParentBusinessProcessId());
    task.setOwner(repModel.getOwner());
    task.setOwnerLongName(repModel.getOwnerLongName());
    task.setPrimaryObjRef(objectReferenceAssembler.toEntity(repModel.getPrimaryObjRef()));
    task.setRead(repModel.isRead());
    task.setTransferred(repModel.isTransferred());
    task.setCustomField(TaskCustomField.CUSTOM_1, repModel.getCustom1());
    task.setCustomField(TaskCustomField.CUSTOM_2, repModel.getCustom2());
    task.setCustomField(TaskCustomField.CUSTOM_3, repModel.getCustom3());
    task.setCustomField(TaskCustomField.CUSTOM_4, repModel.getCustom4());
    task.setCustomField(TaskCustomField.CUSTOM_5, repModel.getCustom5());
    task.setCustomField(TaskCustomField.CUSTOM_6, repModel.getCustom6());
    task.setCustomField(TaskCustomField.CUSTOM_7, repModel.getCustom7());
    task.setCustomField(TaskCustomField.CUSTOM_8, repModel.getCustom8());
    task.setCustomField(TaskCustomField.CUSTOM_9, repModel.getCustom9());
    task.setCustomField(TaskCustomField.CUSTOM_10, repModel.getCustom10());
    task.setCustomField(TaskCustomField.CUSTOM_11, repModel.getCustom11());
    task.setCustomField(TaskCustomField.CUSTOM_12, repModel.getCustom12());
    task.setCustomField(TaskCustomField.CUSTOM_13, repModel.getCustom13());
    task.setCustomField(TaskCustomField.CUSTOM_14, repModel.getCustom14());
    task.setCustomField(TaskCustomField.CUSTOM_15, repModel.getCustom15());
    task.setCustomField(TaskCustomField.CUSTOM_16, repModel.getCustom16());
    task.setCustomIntField(TaskCustomIntField.CUSTOM_INT_1, repModel.getCustomInt1());
    task.setCustomIntField(TaskCustomIntField.CUSTOM_INT_2, repModel.getCustomInt2());
    task.setCustomIntField(TaskCustomIntField.CUSTOM_INT_3, repModel.getCustomInt3());
    task.setCustomIntField(TaskCustomIntField.CUSTOM_INT_4, repModel.getCustomInt4());
    task.setCustomIntField(TaskCustomIntField.CUSTOM_INT_5, repModel.getCustomInt5());
    task.setCustomIntField(TaskCustomIntField.CUSTOM_INT_6, repModel.getCustomInt6());
    task.setCustomIntField(TaskCustomIntField.CUSTOM_INT_7, repModel.getCustomInt7());
    task.setCustomIntField(TaskCustomIntField.CUSTOM_INT_8, repModel.getCustomInt8());
    task.setAttachments(
        repModel.getAttachments().stream()
            .map(attachmentAssembler::toEntityModel)
            .collect(Collectors.toList()));
    task.setSecondaryObjectReferences(
        repModel.getSecondaryObjectReferences().stream()
            .map(objectReferenceAssembler::toEntity)
            .collect(Collectors.toList()));
    task.setCustomAttributeMap(
        repModel.getCustomAttributes().stream()
            .collect(Collectors.toMap(CustomAttribute::getKey, CustomAttribute::getValue)));
    task.setCallbackInfo(
        repModel.getCallbackInfo().stream()
            .filter(e -> Objects.nonNull(e.getKey()))
            .filter(not(e -> e.getKey().isEmpty()))
            .collect(Collectors.toMap(CustomAttribute::getKey, CustomAttribute::getValue)));
    return task;
  }

  private void verifyCorrectCustomAttributesFormat(TaskRepresentationModel repModel)
      throws InvalidArgumentException {

    if (repModel.getCustomAttributes().stream()
        .anyMatch(
            customAttribute ->
                customAttribute.getKey() == null
                    || customAttribute.getKey().isEmpty()
                    || customAttribute.getValue() == null)) {
      throw new InvalidArgumentException(
          "Format of custom attributes is not valid. Please provide the following format: "
              + "\"customAttributes\": [{\"key\": \"someKey\",\"value\": \"someValue\"},{...}])");
    }
  }
}
