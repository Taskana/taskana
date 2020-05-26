package pro.taskana.task.rest.assembler;

import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.TASKS;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.resource.rest.PageLinks;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.rest.models.TaskSummaryRepresentationModel;
import pro.taskana.workbasket.rest.assembler.WorkbasketSummaryRepresentationModelAssembler;

/**
 * EntityModel assembler for {@link TaskSummaryRepresentationModel}.
 */
@Component
public class TaskSummaryRepresentationModelAssembler
    implements RepresentationModelAssembler<TaskSummary, TaskSummaryRepresentationModel> {

  private final ClassificationSummaryRepresentationModelAssembler classificationAssembler;
  private final WorkbasketSummaryRepresentationModelAssembler workbasketAssembler;
  private final AttachmentSummaryRepresentationModelAssembler attachmentAssembler;
  private final TaskService taskService;

  @Autowired
  public TaskSummaryRepresentationModelAssembler(
      ClassificationSummaryRepresentationModelAssembler classificationAssembler,
      WorkbasketSummaryRepresentationModelAssembler workbasketAssembler,
      AttachmentSummaryRepresentationModelAssembler attachmentAssembler,
      TaskService taskService) {
    this.classificationAssembler = classificationAssembler;
    this.workbasketAssembler = workbasketAssembler;
    this.attachmentAssembler = attachmentAssembler;
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
    repModel.setPrimaryObjRef(taskSummary.getPrimaryObjRef());
    repModel.setRead(taskSummary.isRead());
    repModel.setTransferred(taskSummary.isTransferred());
    repModel.setAttachmentSummaries(
        taskSummary.getAttachmentSummaries().stream()
            .map(attachmentAssembler::toModel)
            .collect(Collectors.toList()));
    try {
      repModel.setCustom1(taskSummary.getCustomAttribute("1"));
      repModel.setCustom2(taskSummary.getCustomAttribute("2"));
      repModel.setCustom3(taskSummary.getCustomAttribute("3"));
      repModel.setCustom4(taskSummary.getCustomAttribute("4"));
      repModel.setCustom5(taskSummary.getCustomAttribute("5"));
      repModel.setCustom6(taskSummary.getCustomAttribute("6"));
      repModel.setCustom7(taskSummary.getCustomAttribute("7"));
      repModel.setCustom8(taskSummary.getCustomAttribute("8"));
      repModel.setCustom9(taskSummary.getCustomAttribute("9"));
      repModel.setCustom10(taskSummary.getCustomAttribute("10"));
      repModel.setCustom11(taskSummary.getCustomAttribute("11"));
      repModel.setCustom12(taskSummary.getCustomAttribute("12"));
      repModel.setCustom13(taskSummary.getCustomAttribute("13"));
      repModel.setCustom14(taskSummary.getCustomAttribute("14"));
      repModel.setCustom15(taskSummary.getCustomAttribute("15"));
      repModel.setCustom16(taskSummary.getCustomAttribute("16"));
    } catch (InvalidArgumentException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return repModel;
  }

  public TaskSummary toEntityModel(TaskSummaryRepresentationModel repModel) {
    TaskImpl taskSummary = (TaskImpl) taskService.newTask();
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
    taskSummary
        .setWorkbasketSummary(workbasketAssembler.toEntityModel(repModel.getWorkbasketSummary()));
    taskSummary.setBusinessProcessId(repModel.getBusinessProcessId());
    taskSummary.setParentBusinessProcessId(repModel.getParentBusinessProcessId());
    taskSummary.setOwner(repModel.getOwner());
    taskSummary.setPrimaryObjRef(repModel.getPrimaryObjRef());
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

  @PageLinks(Mapping.URL_TASKS)
  public TaskanaPagedModel<TaskSummaryRepresentationModel> toPageModel(
      List<TaskSummary> taskSummaries, PageMetadata pageMetadata) {
    return taskSummaries.stream()
               .map(this::toModel)
               .collect(
                   Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> new TaskanaPagedModel<>(TASKS, list, pageMetadata)));
  }
}
