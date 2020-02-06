package pro.taskana.rest.resource;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.classification.api.Classification;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.task.api.AttachmentSummary;
import pro.taskana.task.api.ObjectReference;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.internal.AttachmentImpl;
import pro.taskana.task.internal.TaskImpl;
import pro.taskana.task.internal.TaskSummaryImpl;
import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketService;

@TaskanaSpringBootTest
class TaskSummaryAssemblerTest {

  @Autowired TaskService taskService;
  @Autowired TaskSummaryResourceAssembler taskSummaryResourceAssembler;
  @Autowired WorkbasketService workbasketService;
  @Autowired ClassificationService classificationService;

  @Test
  void taskSummaryToResource() {
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
    TaskSummaryImpl taskSummary = (TaskSummaryImpl) task.asSummary();
    TaskSummaryResource resource = this.taskSummaryResourceAssembler.toResource(taskSummary);
    this.testEquality(taskSummary, resource);
  }

  void testEquality(TaskSummaryImpl taskSummary, TaskSummaryResource resource) {
    Assert.assertEquals(taskSummary.getId(), resource.getTaskId());
    Assert.assertEquals(taskSummary.getExternalId(), resource.getExternalId());
    Assert.assertEquals(
        taskSummary.getCreated() == null ? null : taskSummary.getCreated().toString(),
        resource.getCreated());
    Assert.assertEquals(
        taskSummary.getClaimed() == null ? null : taskSummary.getClaimed().toString(),
        resource.getClaimed());
    Assert.assertEquals(
        taskSummary.getCompleted() == null ? null : taskSummary.getCompleted().toString(),
        resource.getCompleted());
    Assert.assertEquals(
        taskSummary.getModified() == null ? null : taskSummary.getModified().toString(),
        resource.getModified());
    Assert.assertEquals(
        taskSummary.getPlanned() == null ? null : taskSummary.getPlanned().toString(),
        resource.getPlanned());
    Assert.assertEquals(taskSummary.getDescription(), resource.getDescription());
    Assert.assertEquals(taskSummary.getName(), resource.getName());
    Assert.assertEquals(taskSummary.getCreator(), resource.getCreator());
    Assert.assertEquals(taskSummary.getNote(), resource.getNote());
    Assert.assertEquals((long) taskSummary.getPriority(), (long) resource.getPriority());
    Assert.assertEquals(taskSummary.getState(), resource.getState());
    Assert.assertEquals(
        taskSummary.getClassificationSummary().getId(),
        resource.getClassificationSummaryResource().getClassificationId());
    Assert.assertEquals(
        taskSummary.getWorkbasketSummary().getId(),
        resource.getWorkbasketSummaryResource().getWorkbasketId());
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

  private void testEqualityAttachments(
      List<AttachmentSummary> attachmentSummaries, List<AttachmentSummaryResource> resources) {
    Assert.assertEquals((long) attachmentSummaries.size(), (long) resources.size());

    for (int i = 0; i < resources.size(); ++i) {
      AttachmentSummaryResource resource = resources.get(i);
      AttachmentSummary attachmentSummary = attachmentSummaries.get(i);
      Assert.assertEquals(attachmentSummary.getId(), resource.getAttachmentId());
    }
  }
}
