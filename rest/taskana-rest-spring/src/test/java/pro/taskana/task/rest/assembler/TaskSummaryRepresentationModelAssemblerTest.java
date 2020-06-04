package pro.taskana.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.rest.models.AttachmentRepresentationModel;
import pro.taskana.task.rest.models.AttachmentSummaryRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryRepresentationModel;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

@TaskanaSpringBootTest
class TaskSummaryRepresentationModelAssemblerTest {

  TaskService taskService;
  TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler;
  WorkbasketService workbasketService;
  ClassificationService classificationService;

  @Autowired
  TaskSummaryRepresentationModelAssemblerTest(TaskService taskService,
      TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler,
      WorkbasketService workbasketService, ClassificationService classificationService) {
    this.taskService = taskService;
    this.taskSummaryRepresentationModelAssembler = taskSummaryRepresentationModelAssembler;
    this.workbasketService = workbasketService;
    this.classificationService = classificationService;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel()
      throws InvalidArgumentException {
    ObjectReference primaryObjRef = new ObjectReference();
    primaryObjRef.setId("abc");
    Classification classification =
        this.classificationService.newClassification("ckey", "cdomain", "MANUAL");
    AttachmentImpl attachment = (AttachmentImpl) this.taskService.newAttachment();
    attachment.setClassificationSummary(classification.asSummary());
    attachment.setId("attachmentId");
    TaskImpl task = (TaskImpl) this.taskService.newTask();
    task.setId("taskId");
    task.setExternalId("externalId");
    task.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setClaimed(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setCompleted(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setPlanned(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setDue(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setName("name");
    task.setCreator("creator");
    task.setDescription("desc");
    task.setNote("note");
    task.setPriority(123);
    task.setState(TaskState.READY);
    task.setClassificationSummary(classification);
    Workbasket workbasket = this.workbasketService.newWorkbasket("key", "domain");
    task.setWorkbasketSummary(workbasket.asSummary());
    task.setBusinessProcessId("businessProcessId");
    task.setParentBusinessProcessId("parentBusinessProcessId");
    task.setOwner("owner");
    task.setPrimaryObjRef(primaryObjRef);
    task.setRead(true);
    task.setTransferred(true);
    task.setCustomAttributes(Collections.singletonMap("abc", "def"));
    task.setCallbackInfo(Collections.singletonMap("ghi", "jkl"));
    task.setAttachments(Collections.singletonList(attachment));
    task.setCustom1("custom1");
    task.setCustom2("custom2");
    task.setCustom3("custom3");
    task.setCustom4("custom4");
    task.setCustom5("custom5");
    task.setCustom6("custom6");
    task.setCustom7("custom7");
    task.setCustom8("custom8");
    task.setCustom9("custom9");
    task.setCustom10("custom10");
    task.setCustom11("custom11");
    task.setCustom12("custom12");
    task.setCustom13("custom13");
    task.setCustom14("custom14");
    task.setCustom15("custom15");
    TaskSummaryRepresentationModel repModel =
        taskSummaryRepresentationModelAssembler.toModel(task);

    testEquality(task, repModel);
    testLinks(repModel);
  }


  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity()
      throws InvalidArgumentException {
    ObjectReference primaryObjRef = new ObjectReference();
    primaryObjRef.setId("abc");
    WorkbasketSummaryRepresentationModel workbasketResource =
        new WorkbasketSummaryRepresentationModel();
    workbasketResource.setWorkbasketId("workbasketId");
    ClassificationSummaryRepresentationModel classificationSummary =
        new ClassificationSummaryRepresentationModel();
    classificationSummary.setKey("keyabc");
    classificationSummary.setDomain("DOMAIN_A");
    classificationSummary.setType("MANUAL");
    AttachmentRepresentationModel attachment = new AttachmentRepresentationModel();
    attachment.setClassificationSummary(classificationSummary);
    attachment.setAttachmentId("attachmentId");
    TaskSummaryRepresentationModel repModel = new TaskRepresentationModel();
    repModel.setTaskId("taskId");
    repModel.setExternalId("externalId");
    repModel.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setClaimed(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setCompleted(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setPlanned(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setDue(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setName("name");
    repModel.setCreator("creator");
    repModel.setDescription("desc");
    repModel.setNote("note");
    repModel.setPriority(123);
    repModel.setState(TaskState.READY);
    repModel.setClassificationSummary(classificationSummary);
    repModel.setWorkbasketSummary(workbasketResource);
    repModel.setBusinessProcessId("businessProcessId");
    repModel.setParentBusinessProcessId("parentBusinessProcessId");
    repModel.setOwner("owner");
    repModel.setPrimaryObjRef(primaryObjRef);
    repModel.setRead(true);
    repModel.setTransferred(true);
    repModel.setCustom1("custom1");
    repModel.setCustom2("custom2");
    repModel.setCustom3("custom3");
    repModel.setCustom4("custom4");
    repModel.setCustom5("custom5");
    repModel.setCustom6("custom6");
    repModel.setCustom7("custom7");
    repModel.setCustom8("custom8");
    repModel.setCustom9("custom9");
    repModel.setCustom10("custom10");
    repModel.setCustom11("custom11");
    repModel.setCustom12("custom12");
    repModel.setCustom13("custom13");
    repModel.setCustom14("custom14");
    repModel.setCustom15("custom15");
    repModel.setCustom16("custom16");
    // when
    TaskSummary task = taskSummaryRepresentationModelAssembler.toEntityModel(repModel);
    // then
    testEquality(task, repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    // given
    ObjectReference primaryObjRef = new ObjectReference();
    primaryObjRef.setId("abc");
    final Workbasket workbasket = workbasketService.newWorkbasket("key", "domain");
    Classification classification =
        classificationService.newClassification("ckey", "cdomain", "MANUAL");
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    attachment.setClassificationSummary(classification.asSummary());
    attachment.setId("attachmentId");
    TaskImpl task = (TaskImpl) taskService.newTask();
    task.setId("taskId");
    task.setExternalId("externalId");
    task.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setClaimed(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setCompleted(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setPlanned(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setDue(Instant.parse("2019-09-13T08:44:17.588Z"));
    task.setName("name");
    task.setCreator("creator");
    task.setDescription("desc");
    task.setNote("note");
    task.setPriority(123);
    task.setState(TaskState.READY);
    task.setClassificationSummary(classification);
    task.setWorkbasketSummary(workbasket);
    task.setBusinessProcessId("businessProcessId");
    task.setParentBusinessProcessId("parentBusinessProcessId");
    task.setOwner("owner");
    task.setPrimaryObjRef(primaryObjRef);
    task.setRead(true);
    task.setTransferred(true);
    task.setCustom1("custom1");
    task.setCustom2("custom2");
    task.setCustom3("custom3");
    task.setCustom4("custom4");
    task.setCustom5("custom5");
    task.setCustom6("custom6");
    task.setCustom7("custom7");
    task.setCustom8("custom8");
    task.setCustom9("custom9");
    task.setCustom10("custom10");
    task.setCustom11("custom11");
    task.setCustom12("custom12");
    task.setCustom13("custom13");
    task.setCustom14("custom14");
    task.setCustom15("custom15");
    task.setCustom16("custom16");
    // when
    TaskSummaryRepresentationModel repModel = taskSummaryRepresentationModelAssembler.toModel(task);
    TaskSummary task2 = taskSummaryRepresentationModelAssembler.toEntityModel(repModel);
    // then
    assertThat(task).isNotSameAs(task2).isEqualTo(task2);
  }

  void testEquality(TaskSummary taskSummary,
      TaskSummaryRepresentationModel repModel) throws InvalidArgumentException {
    Assert.assertEquals(taskSummary.getId(), repModel.getTaskId());
    Assert.assertEquals(taskSummary.getExternalId(), repModel.getExternalId());
    Assert.assertEquals(taskSummary.getCreated(), repModel.getCreated());
    Assert.assertEquals(taskSummary.getClaimed(), repModel.getClaimed());
    Assert.assertEquals(taskSummary.getCompleted(), repModel.getCompleted());
    Assert.assertEquals(taskSummary.getModified(), repModel.getModified());
    Assert.assertEquals(taskSummary.getPlanned(), repModel.getPlanned());
    Assert.assertEquals(taskSummary.getDescription(), repModel.getDescription());
    Assert.assertEquals(taskSummary.getName(), repModel.getName());
    Assert.assertEquals(taskSummary.getCreator(), repModel.getCreator());
    Assert.assertEquals(taskSummary.getNote(), repModel.getNote());
    Assert.assertEquals(taskSummary.getPriority(), repModel.getPriority());
    Assert.assertEquals(taskSummary.getState(), repModel.getState());
    Assert.assertEquals(
        taskSummary.getClassificationSummary().getId(),
        repModel.getClassificationSummary().getClassificationId());
    Assert.assertEquals(
        taskSummary.getWorkbasketSummary().getId(),
        repModel.getWorkbasketSummary().getWorkbasketId());
    Assert.assertEquals(taskSummary.getBusinessProcessId(), repModel.getBusinessProcessId());
    Assert.assertEquals(
        taskSummary.getParentBusinessProcessId(), repModel.getParentBusinessProcessId());
    Assert.assertEquals(taskSummary.getOwner(), repModel.getOwner());
    Assert.assertEquals(taskSummary.getPrimaryObjRef(), repModel.getPrimaryObjRef());
    Assert.assertEquals(taskSummary.isRead(), repModel.isRead());
    Assert.assertEquals(taskSummary.isTransferred(), repModel.isTransferred());
    Assert.assertEquals(taskSummary.getCustomAttribute("1"), repModel.getCustom1());
    Assert.assertEquals(taskSummary.getCustomAttribute("2"), repModel.getCustom2());
    Assert.assertEquals(taskSummary.getCustomAttribute("3"), repModel.getCustom3());
    Assert.assertEquals(taskSummary.getCustomAttribute("4"), repModel.getCustom4());
    Assert.assertEquals(taskSummary.getCustomAttribute("5"), repModel.getCustom5());
    Assert.assertEquals(taskSummary.getCustomAttribute("6"), repModel.getCustom6());
    Assert.assertEquals(taskSummary.getCustomAttribute("7"), repModel.getCustom7());
    Assert.assertEquals(taskSummary.getCustomAttribute("8"), repModel.getCustom8());
    Assert.assertEquals(taskSummary.getCustomAttribute("9"), repModel.getCustom9());
    Assert.assertEquals(taskSummary.getCustomAttribute("10"), repModel.getCustom10());
    Assert.assertEquals(taskSummary.getCustomAttribute("11"), repModel.getCustom11());
    Assert.assertEquals(taskSummary.getCustomAttribute("12"), repModel.getCustom12());
    Assert.assertEquals(taskSummary.getCustomAttribute("13"), repModel.getCustom13());
    Assert.assertEquals(taskSummary.getCustomAttribute("14"), repModel.getCustom14());
    Assert.assertEquals(taskSummary.getCustomAttribute("15"), repModel.getCustom15());
    Assert.assertEquals(taskSummary.getCustomAttribute("16"), repModel.getCustom16());

    this.testEqualityAttachments(
        taskSummary.getAttachmentSummaries(), repModel.getAttachmentSummaries());
  }

  private void testEqualityAttachments(
      List<AttachmentSummary> attachmentSummaries,
      List<AttachmentSummaryRepresentationModel> resources) {
    Assert.assertEquals(attachmentSummaries.size(), resources.size());

    for (int i = 0; i < resources.size(); ++i) {
      AttachmentSummaryRepresentationModel resource = resources.get(i);
      AttachmentSummary attachmentSummary = attachmentSummaries.get(i);
      Assert.assertEquals(attachmentSummary.getId(), resource.getAttachmentId());
    }
  }

  private void testLinks(TaskSummaryRepresentationModel repModel) {
  }
}
