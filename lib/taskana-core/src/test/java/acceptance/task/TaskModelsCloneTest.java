/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package acceptance.task;

import static acceptance.CreateTaskModelHelper.createAttachment;
import static acceptance.CreateTaskModelHelper.createDummyClassification;
import static acceptance.CreateTaskModelHelper.createUnitTestTask;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.task.internal.models.TaskSummaryImpl;

class TaskModelsCloneTest {

  @Test
  void testCloneInTaskSummary() {
    TaskSummaryImpl dummyTaskSummary = new TaskSummaryImpl();
    dummyTaskSummary.setId("dummyId");
    dummyTaskSummary.setExternalId("externalId");
    Attachment dummyAttachmentForSummaryTestPreClone =
        createAttachment("uniqueIdForDeepTest", "uniqueTaskIdForDeepTest");
    AttachmentSummary dummyAttachmentSummary = dummyAttachmentForSummaryTestPreClone.asSummary();
    ArrayList<AttachmentSummary> attachmentSummaries = new ArrayList<>();
    attachmentSummaries.add(dummyAttachmentSummary);
    dummyTaskSummary.setAttachmentSummaries(attachmentSummaries);

    TaskSummaryImpl dummyTaskSummaryCloned = dummyTaskSummary.copy();
    assertThat(dummyTaskSummaryCloned).isNotEqualTo(dummyTaskSummary);
    dummyTaskSummaryCloned.setId(dummyTaskSummary.getId());
    dummyTaskSummaryCloned.setExternalId(dummyTaskSummary.getExternalId());
    assertThat(dummyTaskSummaryCloned).isEqualTo(dummyTaskSummary).isNotSameAs(dummyTaskSummary);

    Attachment dummyAttachmentForSummaryTestPostClone =
        createAttachment("differentIdForDeepTest", "differentTaskIdForDeepTest");
    AttachmentSummary dummyAttachmentSummary2 = dummyAttachmentForSummaryTestPostClone.asSummary();
    attachmentSummaries.add(dummyAttachmentSummary2);
    assertThat(dummyTaskSummaryCloned).isNotEqualTo(dummyTaskSummary);
  }

  @Test
  void testCloneInTask() {
    TaskImpl dummyTask =
        createUnitTestTask(
            "dummyTaskId", "dummyTaskName", "workbasketKey", createDummyClassification());
    Map<String, String> dummyCustomAttributesPreClone = new HashMap<>();
    dummyCustomAttributesPreClone.put("dummyAttributeKey", "dummyAttributeValue");
    dummyTask.setCustomAttributeMap(dummyCustomAttributesPreClone);
    Map<String, String> dummyCallbackInfoPreClone = new HashMap<>();
    dummyCallbackInfoPreClone.put("dummyCallbackKey", "dummyCallbackValue");
    dummyTask.setCallbackInfo(dummyCallbackInfoPreClone);

    TaskImpl dummyTaskCloned = dummyTask.copy();
    assertThat(dummyTaskCloned).isNotEqualTo(dummyTask);
    dummyTaskCloned.setId(dummyTask.getId());
    dummyTaskCloned.setExternalId(dummyTask.getExternalId());
    assertThat(dummyTaskCloned).isEqualTo(dummyTask).isNotSameAs(dummyTask);

    dummyCustomAttributesPreClone.put("deepTestAttributeKey", "deepTestAttributeValue");
    dummyCallbackInfoPreClone.put("deepTestCallbackKey", "deepTestCallbackValue");
    assertThat(dummyTaskCloned).isNotSameAs(dummyTask);
  }

  @Test
  void should_PerformDeepCopyOfAttachments_When_TaskClone() {
    TaskImpl dummyTask =
        createUnitTestTask(
            "dummyTaskId", "dummyTaskName", "dummyWorkbasketKey", createDummyClassification());
    List<Attachment> attachments =
        List.of(createAttachment("abc", "dummyTaskId"), createAttachment("def", "dummyTaskId"));
    dummyTask.setAttachments(attachments);

    TaskImpl dummyTaskCloned = dummyTask.copy();

    assertThat(dummyTask.getAttachments()).isNotSameAs(dummyTaskCloned.getAttachments());
  }

  @Test
  void should_RemoveAttachmentAndTaskIds_When_TaskClone() {
    TaskImpl dummyTask =
        createUnitTestTask(
            "dummyTaskId", "dummyTaskName", "workbasketKey", createDummyClassification());
    List<Attachment> attachments =
        List.of(createAttachment("abc", "dummyTaskId"), createAttachment("def", "dummyTaskId"));
    dummyTask.setAttachments(attachments);

    TaskImpl dummyTaskCloned = dummyTask.copy();

    assertThat(dummyTaskCloned.getAttachments())
        .extracting(AttachmentSummary::getId)
        .containsOnlyNulls();
    assertThat(dummyTaskCloned.getAttachments())
        .extracting(AttachmentSummary::getTaskId)
        .containsOnlyNulls();
  }

  @Test
  void should_CopyWithoutId_When_TaskCommentClone() {
    TaskCommentImpl dummyComment = new TaskCommentImpl();
    dummyComment.setTextField("dummyTextField");
    dummyComment.setId("dummyId");
    TaskCommentImpl dummyCommentCloned = dummyComment.copy();
    assertThat(dummyCommentCloned).isNotEqualTo(dummyComment);
    dummyCommentCloned.setId(dummyComment.getId());
    assertThat(dummyCommentCloned).isEqualTo(dummyComment).isNotSameAs(dummyComment);
  }

  @Test
  void should_CopyWithoutId_When_ObjectReferenceClone() {
    ObjectReferenceImpl dummyReference = new ObjectReferenceImpl();
    dummyReference.setId("dummyId");
    dummyReference.setSystem("dummySystem");
    dummyReference.setCompany("dummyCompany");
    dummyReference.setSystemInstance("dummySystemInstance");
    dummyReference.setType("dummyType");
    dummyReference.setValue("dummyValue");

    ObjectReferenceImpl dummyReferenceCloned = dummyReference.copy();

    assertThat(dummyReferenceCloned).isNotEqualTo(dummyReference);
    dummyReferenceCloned.setId(dummyReference.getId());
    assertThat(dummyReferenceCloned).isEqualTo(dummyReference).isNotSameAs(dummyReference);
  }

  @Test
  void should_CopyWithoutIdAndTaskId_When_AttachmentSummaryClone() {
    Attachment dummyAttachmentForSummaryTest = createAttachment("dummyAttachmentId", "dummyTaskId");
    AttachmentSummaryImpl dummyAttachmentSummary =
        (AttachmentSummaryImpl) dummyAttachmentForSummaryTest.asSummary();

    AttachmentSummaryImpl dummyAttachmentSummaryCloned = dummyAttachmentSummary.copy();
    assertThat(dummyAttachmentSummaryCloned).isNotEqualTo(dummyAttachmentSummary);

    dummyAttachmentSummaryCloned.setId(dummyAttachmentSummary.getId());
    dummyAttachmentSummaryCloned.setTaskId(dummyAttachmentSummary.getTaskId());
    assertThat(dummyAttachmentSummaryCloned)
        .isEqualTo(dummyAttachmentSummary)
        .isNotSameAs(dummyAttachmentSummary);
  }

  @Test
  void should_CopyWithoutIdAndTaskId_When_AttachmentClone() {
    AttachmentImpl dummyAttachment = createAttachment("dummyAttachmentId", "dummyTaskId");

    Map<String, String> dummyMapPreClone = new HashMap<>();
    dummyMapPreClone.put("dummyString1", "dummyString2");
    dummyAttachment.setCustomAttributeMap(dummyMapPreClone);

    AttachmentImpl dummyAttachmentCloned = dummyAttachment.copy();
    assertThat(dummyAttachmentCloned).isNotEqualTo(dummyAttachment);
    dummyAttachmentCloned.setId(dummyAttachment.getId());
    dummyAttachmentCloned.setTaskId(dummyAttachment.getTaskId());
    assertThat(dummyAttachmentCloned).isEqualTo(dummyAttachment).isNotSameAs(dummyAttachment);

    dummyMapPreClone.put("deepTestString1", "deepTestString2");
    assertThat(dummyAttachment).isNotEqualTo(dummyAttachmentCloned);
  }
}
