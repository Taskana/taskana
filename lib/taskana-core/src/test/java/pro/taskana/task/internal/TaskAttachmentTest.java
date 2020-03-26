package pro.taskana.task.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.TaskImpl;

/**
 * Unit Test for methods needed fot attachment at TaskImpl.<br>
 * This test should test every interaction with Attachments, which means adding, removing, nulling
 * them.
 */
class TaskAttachmentTest {

  private TaskImpl cut = new TaskImpl();

  @Test
  void testAddAttachmentWithValidValue() {
    Attachment attachment1 = CreateTaskModelHelper.createAttachment("ID1", "taskId1");
    Attachment attachment2 = CreateTaskModelHelper.createAttachment("ID2", "taskId1");
    Attachment attachment3 = CreateTaskModelHelper.createAttachment("ID3", "taskId1");

    cut.addAttachment(attachment1);
    cut.addAttachment(attachment2);
    cut.addAttachment(attachment3);

    assertThat(cut.getAttachments().size(), equalTo(3));
  }

  @Test
  void testAddNullValue() {
    Attachment attachment1 = CreateTaskModelHelper.createAttachment("ID1", "taskId1");

    cut.addAttachment(attachment1);
    cut.addAttachment(null);

    assertThat(cut.getAttachments().size(), equalTo(1));
  }

  @Test
  void testAddSameTwice() {
    // Same values, not same REF. Important.
    Attachment attachment1 = CreateTaskModelHelper.createAttachment("ID1", "taskId1");
    Attachment attachment2 = CreateTaskModelHelper.createAttachment("ID1", "taskId1");

    cut.addAttachment(attachment1);
    cut.addAttachment(attachment2);

    assertThat(cut.getAttachments().size(), equalTo(1));

    // Check with not same vlaues (same ID)
    String newChannel = "I will overwrite the other!";
    attachment1.setChannel(newChannel);
    cut.addAttachment(attachment1);
    assertThat(cut.getAttachments().size(), equalTo(1));
    assertThat(cut.getAttachments().get(0).getChannel(), equalTo(newChannel));
  }

  @Test
  void testRemoveAttachment() {
    // Testing normal way
    Attachment attachment1 = CreateTaskModelHelper.createAttachment("ID1", "taskId1");
    Attachment attachment2 = CreateTaskModelHelper.createAttachment("ID2", "taskId1");
    cut.addAttachment(attachment1);
    cut.addAttachment(attachment2);

    Attachment actual = cut.removeAttachment(attachment2.getId());

    assertThat(cut.getAttachments().size(), equalTo(1));
    assertThat(actual, equalTo(attachment2));
  }

  @Test
  void testRemoveLoopStopsAtResult() {
    Attachment attachment1 = CreateTaskModelHelper.createAttachment("ID2", "taskId1");
    // adding same uncommon way to test that the loop will stop.
    cut.getAttachments().add(attachment1);
    cut.getAttachments().add(attachment1);
    cut.getAttachments().add(attachment1);
    assertThat(cut.getAttachments().size(), equalTo(3));

    Attachment actual = cut.removeAttachment(attachment1.getId());

    assertThat(cut.getAttachments().size(), equalTo(2));
    assertThat(actual, equalTo(attachment1));
  }

  @Test
  void testGetAttachmentSummaries() {
    ObjectReference objRef = new ObjectReference();
    objRef.setId("ObjRefId");
    objRef.setCompany("company");

    Map<String, String> customAttr = new HashMap<>();
    customAttr.put("key", "value");

    Attachment attachment1 = CreateTaskModelHelper.createAttachment("ID1", "taskId1");
    attachment1.setChannel("channel");
    attachment1.setClassificationSummary(new ClassificationImpl().asSummary());
    attachment1.setReceived(Instant.now());
    attachment1.setObjectReference(objRef);
    // attachment1.setCustomAttributes(customAttr);

    cut.addAttachment(attachment1);

    List<AttachmentSummary> summaries = cut.asSummary().getAttachmentSummaries();
    AttachmentSummary attachmentSummary = summaries.get(0);

    assertThat(attachmentSummary, equalTo(attachment1.asSummary()));

    assertThat(attachmentSummary.getId(), equalTo(attachment1.getId()));
    assertThat(attachmentSummary.getTaskId(), equalTo(attachment1.getTaskId()));
    assertThat(attachmentSummary.getChannel(), equalTo(attachment1.getChannel()));
    assertThat(
        attachmentSummary.getClassificationSummary(),
        equalTo(attachment1.getClassificationSummary()));
    assertThat(attachmentSummary.getObjectReference(), equalTo(attachment1.getObjectReference()));
    assertThat(attachmentSummary.getCreated(), equalTo(attachment1.getCreated()));
    assertThat(attachmentSummary.getReceived(), equalTo(attachment1.getReceived()));
    assertThat(attachmentSummary.getModified(), equalTo(attachment1.getModified()));

    // Must be different
    assertNotEquals(attachmentSummary.hashCode(), attachment1.hashCode());

    cut.removeAttachment("ID1");
    assertThat(summaries.size(), equalTo(1));
    summaries = cut.asSummary().getAttachmentSummaries();
    assertThat(summaries.size(), equalTo(0));
  }
}
