package pro.taskana.task.rest.assembler;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import pro.taskana.common.rest.assembler.CollectionRepresentationModelAssembler;
import pro.taskana.common.rest.assembler.PagedRepresentationModelAssembler;
import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.task.rest.models.TaskSummaryCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryPagedRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryRepresentationModel;
import pro.taskana.workbasket.rest.assembler.WorkbasketSummaryRepresentationModelAssembler;

/** EntityModel assembler for {@link TaskSummaryRepresentationModel}. */
@Component
public class TaskSummaryRepresentationModelAssembler
    implements PagedRepresentationModelAssembler<
            TaskSummary, TaskSummaryRepresentationModel, TaskSummaryPagedRepresentationModel>,
        CollectionRepresentationModelAssembler<
            TaskSummary, TaskSummaryRepresentationModel, TaskSummaryCollectionRepresentationModel> {

  private final ClassificationSummaryRepresentationModelAssembler classificationAssembler;
  private final WorkbasketSummaryRepresentationModelAssembler workbasketAssembler;
  private final AttachmentSummaryRepresentationModelAssembler attachmentAssembler;
  private final ObjectReferenceRepresentationModelAssembler objectReferenceAssembler;
  private final TaskService taskService;

  @Autowired
  public TaskSummaryRepresentationModelAssembler(
      ClassificationSummaryRepresentationModelAssembler classificationAssembler,
      WorkbasketSummaryRepresentationModelAssembler workbasketAssembler,
      AttachmentSummaryRepresentationModelAssembler attachmentAssembler,
      ObjectReferenceRepresentationModelAssembler objectReferenceAssembler,
      TaskService taskService) {
    this.classificationAssembler = classificationAssembler;
    this.workbasketAssembler = workbasketAssembler;
    this.attachmentAssembler = attachmentAssembler;
    this.objectReferenceAssembler = objectReferenceAssembler;
    this.taskService = taskService;
  }

  @NonNull
  @Override
  public TaskSummaryRepresentationModel toModel(@NonNull TaskSummary taskSummary) {
    TaskSummaryRepresentationModel repModel = new TaskSummaryRepresentationModel();
    repModel.setTaskId(taskSummary.getId());
    repModel.setExternalId(taskSummary.getExternalId());
    repModel.setCreated(taskSummary.getCreated());
    repModel.setClaimed(taskSummary.getClaimed());
    repModel.setCompleted(taskSummary.getCompleted());
    repModel.setModified(taskSummary.getModified());
    repModel.setPlanned(taskSummary.getPlanned());
    repModel.setDue(taskSummary.getDue());
    repModel.setName(taskSummary.getName());
    repModel.setCreator(taskSummary.getCreator());
    repModel.setNote(taskSummary.getNote());
    repModel.setDescription(taskSummary.getDescription());
    repModel.setPriority(taskSummary.getPriority());
    repModel.setState(taskSummary.getState());
    repModel.setClassificationSummary(
        classificationAssembler.toModel(taskSummary.getClassificationSummary()));
    repModel.setWorkbasketSummary(workbasketAssembler.toModel(taskSummary.getWorkbasketSummary()));
    repModel.setBusinessProcessId(taskSummary.getBusinessProcessId());
    repModel.setParentBusinessProcessId(taskSummary.getParentBusinessProcessId());
    repModel.setOwner(taskSummary.getOwner());
    repModel.setPrimaryObjRef(objectReferenceAssembler.toModel(taskSummary.getPrimaryObjRef()));
    repModel.setRead(taskSummary.isRead());
    repModel.setTransferred(taskSummary.isTransferred());
    repModel.setAttachmentSummaries(
        taskSummary.getAttachmentSummaries().stream()
            .map(attachmentAssembler::toModel)
            .collect(Collectors.toList()));
    repModel.setCustom1(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_1));
    repModel.setCustom2(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_2));
    repModel.setCustom3(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_3));
    repModel.setCustom4(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_4));
    repModel.setCustom5(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_5));
    repModel.setCustom6(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_6));
    repModel.setCustom7(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_7));
    repModel.setCustom8(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_8));
    repModel.setCustom9(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_9));
    repModel.setCustom10(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_10));
    repModel.setCustom11(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_11));
    repModel.setCustom12(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_12));
    repModel.setCustom13(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_13));
    repModel.setCustom14(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_14));
    repModel.setCustom15(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_15));
    repModel.setCustom16(taskSummary.getCustomAttribute(TaskCustomField.CUSTOM_16));
    return repModel;
  }

  public TaskSummary toEntityModel(TaskSummaryRepresentationModel repModel) {
    TaskSummaryImpl taskSummary = (TaskSummaryImpl) taskService.newTask().asSummary();
    taskSummary.setId(repModel.getTaskId());
    taskSummary.setExternalId(repModel.getExternalId());
    taskSummary.setCreated(repModel.getCreated());
    taskSummary.setClaimed(repModel.getClaimed());
    taskSummary.setCompleted(repModel.getCompleted());
    taskSummary.setModified(repModel.getModified());
    taskSummary.setPlanned(repModel.getPlanned());
    taskSummary.setDue(repModel.getDue());
    taskSummary.setName(repModel.getName());
    taskSummary.setCreator(repModel.getCreator());
    taskSummary.setNote(repModel.getNote());
    taskSummary.setDescription(repModel.getDescription());
    taskSummary.setPriority(repModel.getPriority());
    taskSummary.setState(repModel.getState());
    taskSummary.setClassificationSummary(
        classificationAssembler.toEntityModel(repModel.getClassificationSummary()));
    if (repModel.getWorkbasketSummary() != null) {
      taskSummary.setWorkbasketSummary(
          workbasketAssembler.toEntityModel(repModel.getWorkbasketSummary()));
    }
    taskSummary.setBusinessProcessId(repModel.getBusinessProcessId());
    taskSummary.setParentBusinessProcessId(repModel.getParentBusinessProcessId());
    taskSummary.setOwner(repModel.getOwner());
    taskSummary.setPrimaryObjRef(objectReferenceAssembler.toEntity(repModel.getPrimaryObjRef()));
    taskSummary.setRead(repModel.isRead());
    taskSummary.setTransferred(repModel.isTransferred());
    taskSummary.setAttachmentSummaries(
        repModel.getAttachmentSummaries().stream()
            .map(attachmentAssembler::toEntityModel)
            .collect(Collectors.toList()));
    taskSummary.setCustom1(repModel.getCustom1());
    taskSummary.setCustom2(repModel.getCustom2());
    taskSummary.setCustom3(repModel.getCustom3());
    taskSummary.setCustom4(repModel.getCustom4());
    taskSummary.setCustom5(repModel.getCustom5());
    taskSummary.setCustom6(repModel.getCustom6());
    taskSummary.setCustom7(repModel.getCustom7());
    taskSummary.setCustom8(repModel.getCustom8());
    taskSummary.setCustom9(repModel.getCustom9());
    taskSummary.setCustom10(repModel.getCustom10());
    taskSummary.setCustom11(repModel.getCustom11());
    taskSummary.setCustom12(repModel.getCustom12());
    taskSummary.setCustom13(repModel.getCustom13());
    taskSummary.setCustom14(repModel.getCustom14());
    taskSummary.setCustom15(repModel.getCustom15());
    taskSummary.setCustom16(repModel.getCustom16());
    return taskSummary;
  }

  @Override
  public TaskSummaryPagedRepresentationModel buildPageableEntity(
      Collection<TaskSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    return new TaskSummaryPagedRepresentationModel(content, pageMetadata);
  }

  @Override
  public TaskSummaryCollectionRepresentationModel buildCollectionEntity(
      List<TaskSummaryRepresentationModel> content) {
    return new TaskSummaryCollectionRepresentationModel(content);
  }
}
