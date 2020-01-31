package pro.taskana.rest.resource;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.classification.api.Classification;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.rest.Mapping;
import pro.taskana.task.api.Attachment;
import pro.taskana.task.api.ObjectReference;
import pro.taskana.task.api.Task;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.internal.AttachmentImpl;
import pro.taskana.task.internal.TaskImpl;
import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketService;

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
    classificationResource.key = "keyabc";
    classificationResource.domain = "DOMAIN_A";
    classificationResource.type = "MANUAL";
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
    Assert.assertEquals(task.getId(), resource.getTaskId());
    Assert.assertEquals(task.getExternalId(), resource.getExternalId());
    Assert.assertEquals(
        task.getCreated() == null ? null : task.getCreated().toString(), resource.getCreated());
    Assert.assertEquals(
        task.getClaimed() == null ? null : task.getClaimed().toString(), resource.getClaimed());
    Assert.assertEquals(
        task.getCompleted() == null ? null : task.getCompleted().toString(),
        resource.getCompleted());
    Assert.assertEquals(
        task.getModified() == null ? null : task.getModified().toString(), resource.getModified());
    Assert.assertEquals(
        task.getPlanned() == null ? null : task.getPlanned().toString(), resource.getPlanned());
    Assert.assertEquals(task.getDue() == null ? null : task.getDue().toString(), resource.getDue());
    Assert.assertEquals(task.getName(), resource.getName());
    Assert.assertEquals(task.getCreator(), resource.getCreator());
    Assert.assertEquals(task.getDescription(), resource.getDescription());
    Assert.assertEquals(task.getNote(), resource.getNote());
    Assert.assertEquals(task.getPriority(), resource.getPriority());
    Assert.assertEquals(task.getState(), resource.getState());
    Assert.assertEquals(
        task.getClassificationSummary().getId(),
        resource.getClassificationSummaryResource().getClassificationId());
    Assert.assertEquals(
        task.getWorkbasketSummary().getId(),
        resource.getWorkbasketSummaryResource().getWorkbasketId());
    Assert.assertEquals(task.getBusinessProcessId(), resource.getBusinessProcessId());
    Assert.assertEquals(task.getParentBusinessProcessId(), resource.getParentBusinessProcessId());
    Assert.assertEquals(task.getOwner(), resource.getOwner());
    Assert.assertEquals(task.getPrimaryObjRef(), resource.getPrimaryObjRef());
    Assert.assertEquals(task.isRead(), resource.isRead());
    Assert.assertEquals(task.isTransferred(), resource.isTransferred());
    testEquality(task.getCustomAttributes(), resource.getCustomAttributes());
    testEquality(task.getCallbackInfo(), resource.getCallbackInfo());
    testEqualityAttachements(task.getAttachments(), resource.getAttachments());
    Assert.assertEquals(task.getCustomAttribute("1"), resource.getCustom1());
    Assert.assertEquals(task.getCustomAttribute("2"), resource.getCustom2());
    Assert.assertEquals(task.getCustomAttribute("3"), resource.getCustom3());
    Assert.assertEquals(task.getCustomAttribute("4"), resource.getCustom4());
    Assert.assertEquals(task.getCustomAttribute("5"), resource.getCustom5());
    Assert.assertEquals(task.getCustomAttribute("6"), resource.getCustom6());
    Assert.assertEquals(task.getCustomAttribute("7"), resource.getCustom7());
    Assert.assertEquals(task.getCustomAttribute("8"), resource.getCustom8());
    Assert.assertEquals(task.getCustomAttribute("9"), resource.getCustom9());
    Assert.assertEquals(task.getCustomAttribute("10"), resource.getCustom10());
    Assert.assertEquals(task.getCustomAttribute("11"), resource.getCustom11());
    Assert.assertEquals(task.getCustomAttribute("12"), resource.getCustom12());
    Assert.assertEquals(task.getCustomAttribute("13"), resource.getCustom13());
    Assert.assertEquals(task.getCustomAttribute("14"), resource.getCustom14());
    Assert.assertEquals(task.getCustomAttribute("15"), resource.getCustom15());
    Assert.assertEquals(task.getCustomAttribute("16"), resource.getCustom16());
  }

  private void testEquality(
      Map<String, String> customAttributes, List<TaskResource.CustomAttribute> resourceAttributes) {
    Assert.assertEquals(customAttributes.size(), resourceAttributes.size());
    resourceAttributes.forEach(
        attribute ->
            Assert.assertEquals(customAttributes.get(attribute.getKey()), attribute.getValue()));
  }

  private void testEqualityAttachements(
      List<Attachment> attachments, List<AttachmentResource> resources) {
    Assert.assertEquals(attachments.size(), resources.size());
    for (int i = 0; i < resources.size(); i++) {
      AttachmentResource resource = resources.get(i);
      Attachment attachment = attachments.get(i);
      // Anything else shoulde be tested in AttachementResourceAssemblerTest
      Assert.assertEquals(attachment.getId(), resource.getAttachmentId());
    }
  }

  private void testLinks(TaskResource resource) {
    Assert.assertEquals(1, resource.getLinks().size());
    Assert.assertEquals(
        Mapping.URL_TASKS_ID.replaceAll("\\{.*}", resource.getTaskId()),
        resource.getLink("self").getHref());
  }
}
