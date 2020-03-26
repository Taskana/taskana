package pro.taskana.task.api.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import pro.taskana.task.internal.CreateTaskModelHelper;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;

class TaskModelsCloneTest {

  @Test
  void testCloneInTaskSummary() {
    TaskSummaryImpl dummyTaskSummary = new TaskSummaryImpl();

    Attachment dummyAttachmentForSummaryTestPreClone =
        CreateTaskModelHelper.createAttachment("uniqueIdForDeepTest", "uniqueTaskIdForDeepTest");
    AttachmentSummary dummyAttachmentSummary = dummyAttachmentForSummaryTestPreClone.asSummary();
    ArrayList<AttachmentSummary> attachmentSummaries = new ArrayList<>();
    attachmentSummaries.add(dummyAttachmentSummary);
    dummyTaskSummary.setAttachmentSummaries(attachmentSummaries);

    TaskSummary dummyTaskSummaryCloned = dummyTaskSummary.copy();
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
    Task dummyTask =
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

    Task dummyTaskCloned = dummyTask.copy();
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
    TaskComment dummyCommentCloned = dummyComment.copy();
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
    assertThat(dummyReferenceCloned).isEqualTo(dummyReference);
    assertThat(dummyReferenceCloned).isNotSameAs(dummyReference);
  }

  @Test
  void testCloneInAttachmentSummary() {
    Attachment dummyAttachmentForSummaryTest =
        CreateTaskModelHelper.createAttachment("dummyAttachmentId", "dummyTaskId");
    AttachmentSummary dummyAttachmentSummary = dummyAttachmentForSummaryTest.asSummary();
    AttachmentSummary dummyAttachmentSummaryCloned = dummyAttachmentSummary.copy();
    assertThat(dummyAttachmentSummaryCloned).isEqualTo(dummyAttachmentSummary);
    assertThat(dummyAttachmentSummaryCloned).isNotSameAs(dummyAttachmentSummary);
  }

  @Test
  void testCloneInAttachment() {
    Attachment dummyAttachment =
        CreateTaskModelHelper.createAttachment("dummyAttachmentId", "dummyTaskId");

    Map<String, String> dummyMapPreClone = new HashMap<>();
    dummyMapPreClone.put("dummyString1", "dummyString2");
    dummyAttachment.setCustomAttributes(dummyMapPreClone);

    Attachment dummyAttachmentCloned = dummyAttachment.copy();
    assertThat(dummyAttachmentCloned).isEqualTo(dummyAttachment);
    assertThat(dummyAttachmentCloned).isNotSameAs(dummyAttachment);

    dummyMapPreClone.put("deepTestString1", "deepTestString2");
    assertThat(dummyAttachment).isNotEqualTo(dummyAttachmentCloned);
  }
}
