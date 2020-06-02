package pro.taskana.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.rest.models.AttachmentRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

/**
 * Test for {@link TaskRepresentationModelAssembler}.
 */
@TaskanaSpringBootTest
class TaskRepresentationModelAssemberTest {

  TaskService taskService;
  WorkbasketService workbasketService;
  ClassificationService classificationService;
  TaskRepresentationModelAssembler taskRepresentationModelAssembler;

  @Autowired
  TaskRepresentationModelAssemberTest(
      TaskService taskService,
      WorkbasketService workbasketService,
      ClassificationService classificationService,
      TaskRepresentationModelAssembler taskRepresentationModelAssembler) {
    this.taskService = taskService;
    this.workbasketService = workbasketService;
    this.classificationService = classificationService;
    this.taskRepresentationModelAssembler = taskRepresentationModelAssembler;
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity()
      throws InvalidArgumentException {
    // given
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
    TaskRepresentationModel repModel = new TaskRepresentationModel();
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
    repModel.setCustomAttributes(
        Collections.singletonList(TaskRepresentationModel.CustomAttribute.of("abc", "def")));
    repModel.setCallbackInfo(
        Collections.singletonList(TaskRepresentationModel.CustomAttribute.of("ghi", "jkl")));
    repModel.setAttachments(Collections.singletonList(attachment));
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
    TaskImpl task = (TaskImpl) taskRepresentationModelAssembler.toEntityModel(repModel);
    // then
    testEqualityAfterConversion(task, repModel);
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel()
      throws InvalidArgumentException {
    // given
    ObjectReference primaryObjRef = new ObjectReference();
    primaryObjRef.setId("abc");
    final Workbasket workbasket = workbasketService.newWorkbasket("key", "domain");
    ClassificationSummary classification =
        classificationService.newClassification("ckey", "cdomain", "MANUAL");
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    attachment.setClassificationSummary(classification);
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
    task.setCustom16("custom16");
    // when
    TaskRepresentationModel repModel = taskRepresentationModelAssembler.toModel(task);
    // then
    testEqualityAfterConversion(task, repModel);
    testLinks(repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity()
      throws InvalidArgumentException {
    // given
    ObjectReference primaryObjRef = new ObjectReference();
    primaryObjRef.setId("abc");
    final Workbasket workbasket = workbasketService.newWorkbasket("key", "domain");
    ClassificationSummary classification =
        classificationService.newClassification("ckey", "cdomain", "MANUAL");
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    attachment.setClassificationSummary(classification);
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
    task.setCustom16("custom16");
    // when
    TaskRepresentationModel repModel = taskRepresentationModelAssembler.toModel(task);
    TaskImpl task2 = (TaskImpl) taskRepresentationModelAssembler.toEntityModel(repModel);
    // then
    testEqualityOfEntities(task, task2);
  }

  private void testEqualityAfterConversion(Task task, TaskRepresentationModel repModel)
      throws InvalidArgumentException {
    assertThat(repModel.getTaskId()).isEqualTo(task.getId());
    assertThat(repModel.getExternalId()).isEqualTo(task.getExternalId());
    assertThat(repModel.getCreated()).isEqualTo(task.getCreated());
    assertThat(repModel.getClaimed()).isEqualTo(task.getClaimed());
    assertThat(repModel.getCompleted()).isEqualTo(task.getCompleted());
    assertThat(repModel.getModified()).isEqualTo(task.getModified());
    assertThat(repModel.getPlanned()).isEqualTo(task.getPlanned());
    assertThat(repModel.getDue()).isEqualTo(task.getDue());
    assertThat(repModel.getName()).isEqualTo(task.getName());
    assertThat(repModel.getCreator()).isEqualTo(task.getCreator());
    assertThat(repModel.getDescription()).isEqualTo(task.getDescription());
    assertThat(repModel.getNote()).isEqualTo(task.getNote());
    assertThat(repModel.getPriority()).isEqualTo(task.getPriority());
    assertThat(repModel.getState()).isEqualTo(task.getState());
    assertThat(repModel.getClassificationSummary().getClassificationId())
        .isEqualTo(task.getClassificationSummary().getId());
    assertThat(repModel.getWorkbasketSummary().getWorkbasketId())
        .isEqualTo(task.getWorkbasketSummary().getId());
    assertThat(repModel.getBusinessProcessId()).isEqualTo(task.getBusinessProcessId());
    assertThat(repModel.getParentBusinessProcessId()).isEqualTo(task.getParentBusinessProcessId());
    assertThat(repModel.getOwner()).isEqualTo(task.getOwner());
    assertThat(repModel.getPrimaryObjRef()).isEqualTo(task.getPrimaryObjRef());
    assertThat(repModel.isRead()).isEqualTo(task.isRead());
    assertThat(repModel.isTransferred()).isEqualTo(task.isTransferred());
    testEqualityCustomAttributes(task.getCustomAttributes(), repModel.getCustomAttributes());
    testEqualityCustomAttributes(task.getCallbackInfo(), repModel.getCallbackInfo());
    testEqualityAttachments(task.getAttachments(), repModel.getAttachments());
    assertThat(repModel.getCustom1()).isEqualTo(task.getCustomAttribute("1"));
    assertThat(repModel.getCustom2()).isEqualTo(task.getCustomAttribute("2"));
    assertThat(repModel.getCustom3()).isEqualTo(task.getCustomAttribute("3"));
    assertThat(repModel.getCustom4()).isEqualTo(task.getCustomAttribute("4"));
    assertThat(repModel.getCustom5()).isEqualTo(task.getCustomAttribute("5"));
    assertThat(repModel.getCustom6()).isEqualTo(task.getCustomAttribute("6"));
    assertThat(repModel.getCustom7()).isEqualTo(task.getCustomAttribute("7"));
    assertThat(repModel.getCustom8()).isEqualTo(task.getCustomAttribute("8"));
    assertThat(repModel.getCustom9()).isEqualTo(task.getCustomAttribute("9"));
    assertThat(repModel.getCustom10()).isEqualTo(task.getCustomAttribute("10"));
    assertThat(repModel.getCustom11()).isEqualTo(task.getCustomAttribute("11"));
    assertThat(repModel.getCustom12()).isEqualTo(task.getCustomAttribute("12"));
    assertThat(repModel.getCustom13()).isEqualTo(task.getCustomAttribute("13"));
    assertThat(repModel.getCustom14()).isEqualTo(task.getCustomAttribute("14"));
    assertThat(repModel.getCustom15()).isEqualTo(task.getCustomAttribute("15"));
    assertThat(repModel.getCustom16()).isEqualTo(task.getCustomAttribute("16"));
  }

  private void testEqualityOfEntities(Task task1, Task task2)
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

  private void testEqualityCustomAttributes(
      Map<String, String> customAttributes,
      List<TaskRepresentationModel.CustomAttribute> resourceAttributes) {
    assertThat(resourceAttributes).hasSize(customAttributes.size());
    resourceAttributes.forEach(
        attribute ->
            assertThat(attribute.getValue()).isEqualTo(customAttributes.get(attribute.getKey())));
  }

  private void testEqualityAttachments(
      List<Attachment> attachments, List<AttachmentRepresentationModel> resources) {
    String[] objects = attachments.stream().map(Attachment::getId).toArray(String[]::new);

    // Anything else should be be tested in AttachmentResourceAssemblerTest
    assertThat(resources)
        .hasSize(attachments.size())
        .extracting(AttachmentRepresentationModel::getAttachmentId)
        .containsOnly(objects);
  }

  private void testEqualityAttachmentsOfEqualEntities(
      List<Attachment> attachments, List<Attachment> attachments2) {
    String[] objects = attachments.stream().map(Attachment::getId).toArray(String[]::new);

    assertThat(attachments2)
        .hasSize(attachments.size())
        .extracting(Attachment::getId)
        .containsOnly(objects);
  }

  private void testLinks(TaskRepresentationModel resource) {
    assertThat(resource.getLinks()).hasSize(1);
    assertThat(resource.getRequiredLink("self").getHref())
        .isEqualTo(Mapping.URL_TASKS_ID.replaceAll("\\{.*}", resource.getTaskId()));
  }
}
