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
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;
import pro.taskana.task.rest.models.AttachmentRepresentationModel;
import pro.taskana.task.rest.models.AttachmentSummaryRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryRepresentationModel;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

@TaskanaSpringBootTest
class TaskSummaryAssemblerTest {

  TaskService taskService;
  TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler;
  WorkbasketService workbasketService;
  ClassificationService classificationService;

  @Autowired
  TaskSummaryAssemblerTest(TaskService taskService,
      TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler,
      WorkbasketService workbasketService, ClassificationService classificationService) {
    this.taskService = taskService;
    this.taskSummaryRepresentationModelAssembler = taskSummaryRepresentationModelAssembler;
    this.workbasketService = workbasketService;
    this.classificationService = classificationService;
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
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
    testEqualityAfterConversion(task, repModel);
  }


  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
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
    TaskImpl task = (TaskImpl) taskSummaryRepresentationModelAssembler.toEntityModel(repModel);
    // then
    testEqualityAfterConversion(task, repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity()
      throws InvalidArgumentException {
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
    task.setWorkbasketSummary(workbasket.asSummary());
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
    TaskImpl task2 = (TaskImpl) taskSummaryRepresentationModelAssembler.toEntityModel(repModel);
    // then
    testEqualityOfEntities(task, task2);
  }

  void testEqualityAfterConversion(TaskSummaryImpl taskSummary,
      TaskSummaryRepresentationModel resource) {
    Assert.assertEquals(taskSummary.getId(), resource.getTaskId());
    Assert.assertEquals(taskSummary.getExternalId(), resource.getExternalId());
    Assert.assertEquals(taskSummary.getCreated(), resource.getCreated());
    Assert.assertEquals(taskSummary.getClaimed(), resource.getClaimed());
    Assert.assertEquals(taskSummary.getCompleted(), resource.getCompleted());
    Assert.assertEquals(taskSummary.getModified(), resource.getModified());
    Assert.assertEquals(taskSummary.getPlanned(), resource.getPlanned());
    Assert.assertEquals(taskSummary.getDescription(), resource.getDescription());
    Assert.assertEquals(taskSummary.getName(), resource.getName());
    Assert.assertEquals(taskSummary.getCreator(), resource.getCreator());
    Assert.assertEquals(taskSummary.getNote(), resource.getNote());
    Assert.assertEquals(taskSummary.getPriority(), resource.getPriority());
    Assert.assertEquals(taskSummary.getState(), resource.getState());
    Assert.assertEquals(
        taskSummary.getClassificationSummary().getId(),
        resource.getClassificationSummary().getClassificationId());
    Assert.assertEquals(
        taskSummary.getWorkbasketSummary().getId(),
        resource.getWorkbasketSummary().getWorkbasketId());
    Assert.assertEquals(taskSummary.getBusinessProcessId(), resource.getBusinessProcessId());
    Assert.assertEquals(
        taskSummary.getParentBusinessProcessId(), resource.getParentBusinessProcessId());
    Assert.assertEquals(taskSummary.getOwner(), resource.getOwner());
    Assert.assertEquals(taskSummary.getPrimaryObjRef(), resource.getPrimaryObjRef());
    Assert.assertEquals(taskSummary.isRead(), resource.isRead());
    Assert.assertEquals(taskSummary.isTransferred(), resource.isTransferred());
    Assert.assertEquals(taskSummary.getCustom1(), resource.getCustom1());
    Assert.assertEquals(taskSummary.getCustom2(), resource.getCustom2());
    Assert.assertEquals(taskSummary.getCustom3(), resource.getCustom3());
    Assert.assertEquals(taskSummary.getCustom4(), resource.getCustom4());
    Assert.assertEquals(taskSummary.getCustom5(), resource.getCustom5());
    Assert.assertEquals(taskSummary.getCustom6(), resource.getCustom6());
    Assert.assertEquals(taskSummary.getCustom7(), resource.getCustom7());
    Assert.assertEquals(taskSummary.getCustom8(), resource.getCustom8());
    Assert.assertEquals(taskSummary.getCustom9(), resource.getCustom9());
    Assert.assertEquals(taskSummary.getCustom10(), resource.getCustom10());
    Assert.assertEquals(taskSummary.getCustom11(), resource.getCustom11());
    Assert.assertEquals(taskSummary.getCustom12(), resource.getCustom12());
    Assert.assertEquals(taskSummary.getCustom13(), resource.getCustom13());
    Assert.assertEquals(taskSummary.getCustom14(), resource.getCustom14());
    Assert.assertEquals(taskSummary.getCustom15(), resource.getCustom15());
    Assert.assertEquals(taskSummary.getCustom16(), resource.getCustom16());

    this.testEqualityAttachments(
        taskSummary.getAttachmentSummaries(), resource.getAttachmentSummaries());
  }


  void testEqualityOfEntities(Task task1, Task task2)
      throws InvalidArgumentException {
    assertThat(task1.getId()).isEqualTo(task2.getId());
    assertThat(task1.getExternalId()).isEqualTo(task2.getExternalId());
    assertThat(task1.getCreated()).isEqualTo(task2.getCreated());
    assertThat(task1.getClaimed()).isEqualTo(task2.getClaimed());
    assertThat(task1.getCompleted()).isEqualTo(task2.getCompleted());
    assertThat(task1.getModified()).isEqualTo(task2.getModified());
    assertThat(task1.getPlanned()).isEqualTo(task2.getPlanned());
    assertThat(task1.getDue()).isEqualTo(task2.getDue());
    assertThat(task1.getName()).isEqualTo(task2.getName());
    assertThat(task1.getCreator()).isEqualTo(task2.getCreator());
    assertThat(task1.getDescription()).isEqualTo(task2.getDescription());
    assertThat(task1.getNote()).isEqualTo(task2.getNote());
    assertThat(task1.getPriority()).isEqualTo(task2.getPriority());
    assertThat(task1.getState()).isEqualTo(task2.getState());
    assertThat(task1.getClassificationSummary().getId())
        .isEqualTo(task2.getClassificationSummary().getId());
    assertThat(task1.getWorkbasketSummary().getId())
        .isEqualTo(task2.getWorkbasketSummary().getId());
    assertThat(task1.getBusinessProcessId()).isEqualTo(task2.getBusinessProcessId());
    assertThat(task1.getParentBusinessProcessId()).isEqualTo(task2.getParentBusinessProcessId());
    assertThat(task1.getOwner()).isEqualTo(task2.getOwner());
    assertThat(task1.getPrimaryObjRef()).isEqualTo(task2.getPrimaryObjRef());
    assertThat(task1.isRead()).isEqualTo(task2.isRead());
    assertThat(task1.isTransferred()).isEqualTo(task2.isTransferred());
    assertThat(task2.getCustomAttributes()).isEqualTo(task1.getCustomAttributes());
    assertThat(task2.getCallbackInfo()).isEqualTo(task1.getCallbackInfo());
    testEqualityAttachmentsOfEqualEntities(task1.getAttachments(), task2.getAttachments());
    assertThat(task1.getCustomAttribute("1")).isEqualTo(task2.getCustomAttribute("1"));
    assertThat(task1.getCustomAttribute("2")).isEqualTo(task2.getCustomAttribute("2"));
    assertThat(task1.getCustomAttribute("3")).isEqualTo(task2.getCustomAttribute("3"));
    assertThat(task1.getCustomAttribute("4")).isEqualTo(task2.getCustomAttribute("4"));
    assertThat(task1.getCustomAttribute("5")).isEqualTo(task2.getCustomAttribute("5"));
    assertThat(task1.getCustomAttribute("6")).isEqualTo(task2.getCustomAttribute("6"));
    assertThat(task1.getCustomAttribute("7")).isEqualTo(task2.getCustomAttribute("7"));
    assertThat(task1.getCustomAttribute("8")).isEqualTo(task2.getCustomAttribute("8"));
    assertThat(task1.getCustomAttribute("9")).isEqualTo(task2.getCustomAttribute("9"));
    assertThat(task1.getCustomAttribute("10")).isEqualTo(task2.getCustomAttribute("10"));
    assertThat(task1.getCustomAttribute("11")).isEqualTo(task2.getCustomAttribute("11"));
    assertThat(task1.getCustomAttribute("12")).isEqualTo(task2.getCustomAttribute("12"));
    assertThat(task1.getCustomAttribute("13")).isEqualTo(task2.getCustomAttribute("13"));
    assertThat(task1.getCustomAttribute("14")).isEqualTo(task2.getCustomAttribute("14"));
    assertThat(task1.getCustomAttribute("15")).isEqualTo(task2.getCustomAttribute("15"));
    assertThat(task1.getCustomAttribute("16")).isEqualTo(task2.getCustomAttribute("16"));
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

  private void testEqualityAttachmentsOfEqualEntities(
      List<Attachment> attachments, List<Attachment> attachments2) {
    String[] objects = attachments.stream().map(Attachment::getId).toArray(String[]::new);

    assertThat(attachments2)
        .hasSize(attachments.size())
        .extracting(Attachment::getId)
        .containsOnly(objects);
  }

  private void testLinks(TaskSummaryRepresentationModel repModel) {
  }
}
