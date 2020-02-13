package pro.taskana.rest.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.rest.Mapping;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;

/** Test for {@link TaskResourceAssembler}. */
@TaskanaSpringBootTest
class TaskResourceAssemberTest {

  @Autowired TaskService taskService;
  @Autowired WorkbasketService workbasketService;
  @Autowired ClassificationService classificationService;
  @Autowired TaskResourceAssembler taskResourceAssembler;

  @Test
  void testSimpleResourceToModel() throws InvalidArgumentException {
    // given
    ObjectReference primaryObjRef = new ObjectReference();
    primaryObjRef.setId("abc");
    WorkbasketSummaryResource workbasketResource = new WorkbasketSummaryResource();
    workbasketResource.setWorkbasketId("workbasketId");
    ClassificationSummaryResource classificationResource = new ClassificationSummaryResource();
    classificationResource.setKey("keyabc");
    classificationResource.setDomain("DOMAIN_A");
    classificationResource.setType("MANUAL");
    AttachmentResource attachement = new AttachmentResource();
    attachement.setClassificationSummary(classificationResource);
    attachement.setAttachmentId("attachementId");
    TaskResource resource = new TaskResource();
    resource.setTaskId("taskId");
    resource.setExternalId("externalId");
    resource.setCreated("2019-09-13T08:44:17.588Z");
    resource.setClaimed("2019-09-13T08:44:17.588Z");
    resource.setCompleted("2019-09-13T08:44:17.588Z");
    resource.setModified("2019-09-13T08:44:17.588Z");
    resource.setPlanned("2019-09-13T08:44:17.588Z");
    resource.setDue("2019-09-13T08:44:17.588Z");
    resource.setName("name");
    resource.setCreator("creator");
    resource.setDescription("desc");
    resource.setNote("note");
    resource.setPriority(123);
    resource.setState(TaskState.READY);
    resource.setClassificationSummaryResource(classificationResource);
    resource.setWorkbasketSummaryResource(workbasketResource);
    resource.setBusinessProcessId("businessProcessId");
    resource.setParentBusinessProcessId("parentBusinessProcessId");
    resource.setOwner("owner");
    resource.setPrimaryObjRef(primaryObjRef);
    resource.setRead(true);
    resource.setTransferred(true);
    resource.setCustomAttributes(
        Collections.singletonList(new TaskResource.CustomAttribute("abc", "def")));
    resource.setCallbackInfo(
        Collections.singletonList(new TaskResource.CustomAttribute("ghi", "jkl")));
    resource.setAttachments(Collections.singletonList(attachement));
    resource.setCustom1("custom1");
    resource.setCustom2("custom2");
    resource.setCustom3("custom3");
    resource.setCustom4("custom4");
    resource.setCustom5("custom5");
    resource.setCustom6("custom6");
    resource.setCustom7("custom7");
    resource.setCustom8("custom8");
    resource.setCustom9("custom9");
    resource.setCustom10("custom10");
    resource.setCustom11("custom11");
    resource.setCustom12("custom12");
    resource.setCustom13("custom13");
    resource.setCustom14("custom14");
    resource.setCustom15("custom15");
    // when
    Task task = taskResourceAssembler.toModel(resource);
    // then
    testEquality(task, resource);
  }

  @Test
  void testModelToResource() throws InvalidArgumentException {
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
    // when
    TaskResource resource = taskResourceAssembler.toResource(task);
    // then
    testEquality(task, resource);
    testLinks(resource);
  }

  void testEquality(Task task, TaskResource resource) throws InvalidArgumentException {
    assertThat(resource.getTaskId()).isEqualTo(task.getId());
    assertThat(resource.getExternalId()).isEqualTo(task.getExternalId());
    assertThat(resource.getCreated())
        .isEqualTo(task.getCreated() == null ? null : task.getCreated().toString());
    assertThat(resource.getClaimed())
        .isEqualTo(task.getClaimed() == null ? null : task.getClaimed().toString());
    assertThat(resource.getCompleted())
        .isEqualTo(task.getCompleted() == null ? null : task.getCompleted().toString());
    assertThat(resource.getModified())
        .isEqualTo(task.getModified() == null ? null : task.getModified().toString());
    assertThat(resource.getPlanned())
        .isEqualTo(task.getPlanned() == null ? null : task.getPlanned().toString());
    assertThat(resource.getDue())
        .isEqualTo(task.getDue() == null ? null : task.getDue().toString());
    assertThat(resource.getName()).isEqualTo(task.getName());
    assertThat(resource.getCreator()).isEqualTo(task.getCreator());
    assertThat(resource.getDescription()).isEqualTo(task.getDescription());
    assertThat(resource.getNote()).isEqualTo(task.getNote());
    assertThat(resource.getPriority()).isEqualTo(task.getPriority());
    assertThat(resource.getState()).isEqualTo(task.getState());
    assertThat(resource.getClassificationSummaryResource().getClassificationId())
        .isEqualTo(task.getClassificationSummary().getId());
    assertThat(resource.getWorkbasketSummaryResource().getWorkbasketId())
        .isEqualTo(task.getWorkbasketSummary().getId());
    assertThat(resource.getBusinessProcessId()).isEqualTo(task.getBusinessProcessId());
    assertThat(resource.getParentBusinessProcessId()).isEqualTo(task.getParentBusinessProcessId());
    assertThat(resource.getOwner()).isEqualTo(task.getOwner());
    assertThat(resource.getPrimaryObjRef()).isEqualTo(task.getPrimaryObjRef());
    assertThat(resource.isRead()).isEqualTo(task.isRead());
    assertThat(resource.isTransferred()).isEqualTo(task.isTransferred());
    testEquality(task.getCustomAttributes(), resource.getCustomAttributes());
    testEquality(task.getCallbackInfo(), resource.getCallbackInfo());
    testEqualityAttachements(task.getAttachments(), resource.getAttachments());
    assertThat(resource.getCustom1()).isEqualTo(task.getCustomAttribute("1"));
    assertThat(resource.getCustom2()).isEqualTo(task.getCustomAttribute("2"));
    assertThat(resource.getCustom3()).isEqualTo(task.getCustomAttribute("3"));
    assertThat(resource.getCustom4()).isEqualTo(task.getCustomAttribute("4"));
    assertThat(resource.getCustom5()).isEqualTo(task.getCustomAttribute("5"));
    assertThat(resource.getCustom6()).isEqualTo(task.getCustomAttribute("6"));
    assertThat(resource.getCustom7()).isEqualTo(task.getCustomAttribute("7"));
    assertThat(resource.getCustom8()).isEqualTo(task.getCustomAttribute("8"));
    assertThat(resource.getCustom9()).isEqualTo(task.getCustomAttribute("9"));
    assertThat(resource.getCustom10()).isEqualTo(task.getCustomAttribute("10"));
    assertThat(resource.getCustom11()).isEqualTo(task.getCustomAttribute("11"));
    assertThat(resource.getCustom12()).isEqualTo(task.getCustomAttribute("12"));
    assertThat(resource.getCustom13()).isEqualTo(task.getCustomAttribute("13"));
    assertThat(resource.getCustom14()).isEqualTo(task.getCustomAttribute("14"));
    assertThat(resource.getCustom15()).isEqualTo(task.getCustomAttribute("15"));
    assertThat(resource.getCustom16()).isEqualTo(task.getCustomAttribute("16"));
  }

  private void testEquality(
      Map<String, String> customAttributes, List<TaskResource.CustomAttribute> resourceAttributes) {
    assertThat(resourceAttributes).hasSize(customAttributes.size());
    resourceAttributes.forEach(
        attribute ->
            assertThat(attribute.getValue()).isEqualTo(customAttributes.get(attribute.getKey())));
  }

  private void testEqualityAttachements(
      List<Attachment> attachments, List<AttachmentResource> resources) {
    assertThat(resources).hasSize(attachments.size());
    for (int i = 0; i < resources.size(); i++) {
      AttachmentResource resource = resources.get(i);
      Attachment attachment = attachments.get(i);
      // Anything else should be be tested in AttachementResourceAssemblerTest
      assertThat(resource.getAttachmentId()).isEqualTo(attachment.getId());
    }
  }

  private void testLinks(TaskResource resource) {
    assertThat(resource.getLinks()).hasSize(1);
    assertThat(resource.getLink("self").getHref())
        .isEqualTo(Mapping.URL_TASKS_ID.replaceAll("\\{.*}", resource.getTaskId()));
  }
}
