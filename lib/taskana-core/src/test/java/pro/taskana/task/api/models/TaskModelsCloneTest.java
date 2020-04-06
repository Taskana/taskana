package pro.taskana.task.api.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import pro.taskana.task.internal.CreateTaskModelHelper;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;

class TaskModelsCloneTest {

  @Test
  void testCloneInTaskSummary() {
    TaskSummaryImpl dummyTaskSummary = new TaskSummaryImpl();
    dummyTaskSummary.setId("dummyId");
    Attachment dummyAttachmentForSummaryTestPreClone =
        CreateTaskModelHelper.createAttachment("uniqueIdForDeepTest", "uniqueTaskIdForDeepTest");
    AttachmentSummary dummyAttachmentSummary = dummyAttachmentForSummaryTestPreClone.asSummary();
    ArrayList<AttachmentSummary> attachmentSummaries = new ArrayList<>();
    attachmentSummaries.add(dummyAttachmentSummary);
    dummyTaskSummary.setAttachmentSummaries(attachmentSummaries);

    TaskSummaryImpl dummyTaskSummaryCloned = dummyTaskSummary.copy();
    assertThat(dummyTaskSummaryCloned).isNotEqualTo(dummyTaskSummary);
    dummyTaskSummaryCloned.setId(dummyTaskSummary.getId());
    assertThat(dummyTaskSummaryCloned).isEqualTo(dummyTaskSummary);
    assertThat(dummyTaskSummaryCloned).isNotSameAs(dummyTaskSummary);

    Attachment dummyAttachmentForSummaryTestPostClone =
        CreateTaskModelHelper.createAttachment(
            "differentIdForDeepTest", "differentTaskIdForDeepTest");
    AttachmentSummary dummyAttachmentSummary2 = dummyAttachmentForSummaryTestPostClone.asSummary();
    attachmentSummaries.add(dummyAttachmentSummary2);
    assertThat(dummyTaskSummaryCloned).isNotEqualTo(dummyTaskSummary);
  }

  @Test
  void testCloneInTask() {
    TaskImpl dummyTask =
        CreateTaskModelHelper.createUnitTestTask(
            "dummyTaskId",
            "dummyTaskName",
            "workbasketKey",
            CreateTaskModelHelper.createDummyClassification());
    Map<String, String> dummyCustomAttributesPreClone = new HashMap<>();
    dummyCustomAttributesPreClone.put("dummyAttributeKey", "dummyAttributeValue");
    dummyTask.setCustomAttributes(dummyCustomAttributesPreClone);
    Map<String, String> dummyCallbackInfoPreClone = new HashMap<>();
    dummyCallbackInfoPreClone.put("dummyCallbackKey", "dummyCallbackValue");
    dummyTask.setCallbackInfo(dummyCallbackInfoPreClone);

    TaskImpl dummyTaskCloned = dummyTask.copy();
    assertThat(dummyTaskCloned).isNotEqualTo(dummyTask);
    dummyTaskCloned.setId(dummyTask.getId());
    assertThat(dummyTaskCloned).isEqualTo(dummyTask);
    assertThat(dummyTaskCloned).isNotSameAs(dummyTask);

    dummyCustomAttributesPreClone.put("deepTestAttributeKey", "deepTestAttributeValue");
    dummyCallbackInfoPreClone.put("deepTestCallbackKey", "deepTestCallbackValue");
    assertThat(dummyTaskCloned).isNotEqualTo(dummyTask);
  }

  @Test
  void testCloneInTaskComment() {
    TaskCommentImpl dummyComment = new TaskCommentImpl();
    dummyComment.setTextField("dummyTextField");
    dummyComment.setId("dummyId");
    TaskCommentImpl dummyCommentCloned = dummyComment.copy();
    assertThat(dummyCommentCloned).isNotEqualTo(dummyComment);
    dummyCommentCloned.setId(dummyComment.getId());
    assertThat(dummyCommentCloned).isEqualTo(dummyComment);
    assertThat(dummyCommentCloned).isNotSameAs(dummyComment);
  }

  @Test
  void testCloneInObjectReference() {
    ObjectReference dummyReference = new ObjectReference();
    dummyReference.setId("dummyId");
    dummyReference.setSystem("dummySystem");
    dummyReference.setCompany("dummyCompany");
    dummyReference.setSystemInstance("dummySystemInstance");
    dummyReference.setType("dummyType");
    dummyReference.setValue("dummyValue");
    ObjectReference dummyReferenceCloned = dummyReference.copy();
    assertThat(dummyReferenceCloned).isNotEqualTo(dummyReference);
    dummyReferenceCloned.setId(dummyReference.getId());
    assertThat(dummyReferenceCloned).isEqualTo(dummyReference);
    assertThat(dummyReferenceCloned).isNotSameAs(dummyReference);
  }

  @Test
  void testCloneInAttachmentSummary() {
    Attachment dummyAttachmentForSummaryTest =
        CreateTaskModelHelper.createAttachment("dummyAttachmentId", "dummyTaskId");
    AttachmentSummaryImpl dummyAttachmentSummary =
        (AttachmentSummaryImpl) dummyAttachmentForSummaryTest.asSummary();
    AttachmentSummaryImpl dummyAttachmentSummaryCloned = dummyAttachmentSummary.copy();
    assertThat(dummyAttachmentSummaryCloned).isNotEqualTo(dummyAttachmentSummary);
    dummyAttachmentSummaryCloned.setId(dummyAttachmentSummary.getId());
    assertThat(dummyAttachmentSummaryCloned).isEqualTo(dummyAttachmentSummary);
    assertThat(dummyAttachmentSummaryCloned).isNotSameAs(dummyAttachmentSummary);
  }

  @Test
  void testCloneInAttachment() {
    AttachmentImpl dummyAttachment =
        CreateTaskModelHelper.createAttachment("dummyAttachmentId", "dummyTaskId");

    Map<String, String> dummyMapPreClone = new HashMap<>();
    dummyMapPreClone.put("dummyString1", "dummyString2");
    dummyAttachment.setCustomAttributes(dummyMapPreClone);

    AttachmentImpl dummyAttachmentCloned = dummyAttachment.copy();
    assertThat(dummyAttachmentCloned).isNotEqualTo(dummyAttachment);
    dummyAttachmentCloned.setId(dummyAttachment.getId());
    assertThat(dummyAttachmentCloned).isEqualTo(dummyAttachment);
    assertThat(dummyAttachmentCloned).isNotSameAs(dummyAttachment);

    dummyMapPreClone.put("deepTestString1", "deepTestString2");
    assertThat(dummyAttachment).isNotEqualTo(dummyAttachmentCloned);
  }
}
