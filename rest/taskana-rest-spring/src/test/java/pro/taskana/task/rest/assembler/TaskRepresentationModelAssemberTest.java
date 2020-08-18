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
import pro.taskana.task.api.TaskCustomField;
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
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

/** Test for {@link TaskRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class TaskRepresentationModelAssemberTest {

  TaskService taskService;
  WorkbasketService workbasketService;
  ClassificationService classificationService;
  TaskRepresentationModelAssembler assembler;

  @Autowired
  TaskRepresentationModelAssemberTest(
      TaskService taskService,
      WorkbasketService workbasketService,
      ClassificationService classificationService,
      TaskRepresentationModelAssembler assembler) {
    this.taskService = taskService;
    this.workbasketService = workbasketService;
    this.classificationService = classificationService;
    this.assembler = assembler;
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() throws Exception {
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
    Task task = assembler.toEntityModel(repModel);
    // then
    testEquality(task, repModel);
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel()
      throws Exception {
    // given
    ObjectReference primaryObjRef = new ObjectReference();
    primaryObjRef.setId("abc");
    final Workbasket workbasket = workbasketService.newWorkbasket("key", "domain");
    ClassificationSummary classification =
        classificationService.newClassification("ckey", "cdomain", "MANUAL").asSummary();
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
    task.setCustomAttributeMap(Collections.singletonMap("abc", "def"));
    task.setCallbackInfo(Collections.singletonMap("ghi", "jkl"));
    task.setAttachments(Collections.singletonList(attachment));
    task.setCustomAttribute(TaskCustomField.CUSTOM_1, "custom1");
    task.setCustomAttribute(TaskCustomField.CUSTOM_2, "custom2");
    task.setCustomAttribute(TaskCustomField.CUSTOM_3, "custom3");
    task.setCustomAttribute(TaskCustomField.CUSTOM_4, "custom4");
    task.setCustomAttribute(TaskCustomField.CUSTOM_5, "custom5");
    task.setCustomAttribute(TaskCustomField.CUSTOM_6, "custom6");
    task.setCustomAttribute(TaskCustomField.CUSTOM_7, "custom7");
    task.setCustomAttribute(TaskCustomField.CUSTOM_8, "custom8");
    task.setCustomAttribute(TaskCustomField.CUSTOM_9, "custom9");
    task.setCustomAttribute(TaskCustomField.CUSTOM_10, "custom10");
    task.setCustomAttribute(TaskCustomField.CUSTOM_11, "custom11");
    task.setCustomAttribute(TaskCustomField.CUSTOM_12, "custom12");
    task.setCustomAttribute(TaskCustomField.CUSTOM_13, "custom13");
    task.setCustomAttribute(TaskCustomField.CUSTOM_14, "custom14");
    task.setCustomAttribute(TaskCustomField.CUSTOM_15, "custom15");
    task.setCustomAttribute(TaskCustomField.CUSTOM_16, "custom16");
    // when
    TaskRepresentationModel repModel = assembler.toModel(task);
    // then
    testEquality(task, repModel);
    testLinks(repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() throws InvalidArgumentException {
    // given
    ObjectReference primaryObjRef = new ObjectReference();
    primaryObjRef.setId("abc");
    final WorkbasketSummary workbasket =
        workbasketService.newWorkbasket("key", "domain").asSummary();
    ClassificationSummary classification =
        classificationService.newClassification("ckey", "cdomain", "MANUAL").asSummary();
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
    task.setWorkbasketSummary(workbasket);
    task.setBusinessProcessId("businessProcessId");
    task.setParentBusinessProcessId("parentBusinessProcessId");
    task.setOwner("owner");
    task.setPrimaryObjRef(primaryObjRef);
    task.setRead(true);
    task.setTransferred(true);
    task.setCustomAttributeMap(Collections.singletonMap("abc", "def"));
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
    TaskRepresentationModel repModel = assembler.toModel(task);
    Task task2 = assembler.toEntityModel(repModel);
    // then
    assertThat(task).hasNoNullFieldsOrProperties().isNotSameAs(task2).isEqualTo(task2);
  }

  private void testEquality(Task task, TaskRepresentationModel repModel) throws Exception {
    TaskSummaryRepresentationModelAssemblerTest.testEquality(task, repModel);

    testEqualityCustomAttributes(task.getCustomAttributeMap(), repModel.getCustomAttributes());
    testEqualityCustomAttributes(task.getCallbackInfo(), repModel.getCallbackInfo());
    testEqualityAttachments(task.getAttachments(), repModel.getAttachments());
  }

  private void testEqualityCustomAttributes(
      Map<String, String> customAttributes,
      List<TaskRepresentationModel.CustomAttribute> repModelAttributes) {
    assertThat(repModelAttributes).hasSize(customAttributes.size());
    repModelAttributes.forEach(
        attribute ->
            assertThat(attribute.getValue()).isEqualTo(customAttributes.get(attribute.getKey())));
  }

  private void testEqualityAttachments(
      List<Attachment> attachments, List<AttachmentRepresentationModel> repModels) {
    String[] objects = attachments.stream().map(Attachment::getId).toArray(String[]::new);

    // Anything else should be be tested in AttachmentResourceAssemblerTest
    assertThat(repModels)
        .hasSize(attachments.size())
        .extracting(AttachmentRepresentationModel::getAttachmentId)
        .containsExactlyInAnyOrder(objects);
  }

  private void testLinks(TaskRepresentationModel repModel) {
    assertThat(repModel.getLinks()).hasSize(1);
    assertThat(repModel.getRequiredLink("self").getHref())
        .isEqualTo(Mapping.URL_TASKS_ID.replaceAll("\\{.*}", repModel.getTaskId()));
  }
}
