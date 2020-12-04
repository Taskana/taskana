package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import helper.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import org.assertj.core.api.Condition;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;

/**
 * Acceptance test for the usecase of adding/removing an attachment of a task and update the result
 * correctly.
 */
@ExtendWith(JaasExtension.class)
class UpdateTaskAttachmentsAccTest extends AbstractAccTest {

  private Task task;
  private Attachment attachment;
  private TaskService taskService;

  @BeforeEach
  @WithAccessId(user = "admin")
  void setUp() throws Exception {
    taskService = taskanaEngine.getTaskService();
    task =
        taskService.getTask(
            "TKI:000000000000000000000000000000000000"); // class T2000, prio 1, SL P1D
    task.setClassificationKey("T2000");
    attachment =
        createAttachment(
            "DOCTYPE_DEFAULT", // prio 99, SL P2000D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomPropertyMap(3));
    task.getAttachments().clear();
    taskService.updateTask(task);
    assertThat(task).isNotNull();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateTaskCorrectlyInDatabase_When_AddingAnAttachment() throws Exception {
    final int attachmentCount = task.getAttachments().size();
    assertThat(task.getPriority()).isEqualTo(1);
    assertThat(task.getPlanned().plus(Duration.ofDays(1))).isEqualTo(task.getDue());
    task.addAttachment(attachment);

    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments())
        .hasSize(attachmentCount + 1)
        .contains(attachment)
        .extracting(Attachment::getModified)
        .containsExactlyInAnyOrder(task.getModified());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotAddSameAttachmentAgain_When_AddingToTaskSummary() throws Exception {
    task.getAttachments().clear();
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).isEmpty();

    AttachmentImpl attachment = (AttachmentImpl) this.attachment;
    attachment.setId("TAI:000017");
    task.addAttachment(attachment);
    task.addAttachment(attachment);
    task = taskService.updateTask(task);

    assertThat(task.getAttachments())
        .hasSize(1)
        .extracting(AttachmentSummary::getModified)
        .containsExactlyInAnyOrder(task.getModified());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowAttachmentPersistenceException_When_UpdatingTaskWithTwoIdenticalAttachments()
      throws Exception {
    final int attachmentCount = 0;
    task.getAttachments().clear();
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount);

    AttachmentImpl attachment = (AttachmentImpl) this.attachment;
    attachment.setId("TAI:000017");
    task.getAttachments().add(attachment);
    task.getAttachments().add(attachment);
    ThrowingCallable call = () -> taskService.updateTask(task);
    assertThatThrownBy(call).isInstanceOf(AttachmentPersistenceException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateExistingAttachment_When_AddingSameButNotEqualAttachmentAgain()
      throws Exception {
    // Add attachment before
    task = taskService.getTask(task.getId());
    final int attachmentCount = task.getAttachments().size();
    task.addAttachment(attachment);
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount + 1);

    // Change sth. and add same (id) again - override/update
    String newChannel = "UPDATED EXTERNAL SINCE LAST ADD";
    final int attachmentCount2 = task.getAttachments().size();
    Attachment updatedAttachment = task.getAttachments().get(0);
    updatedAttachment.setChannel(newChannel);
    Classification newClassification =
        taskanaEngine
            .getClassificationService()
            .getClassification("CLI:000000000000000000000000000000000001"); // Prio 999, SL PT5H
    updatedAttachment.setClassificationSummary(newClassification.asSummary());
    task.addAttachment(updatedAttachment);
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount2);
    assertThat(task.getAttachments().get(0).getChannel()).isEqualTo(newChannel);
    assertThat(task.getPriority()).isEqualTo(999);

    Instant expDue = converter.addWorkingDaysToInstant(task.getPlanned(), Duration.ofDays(1));
    assertThat(task.getDue()).isEqualTo(expDue);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotUpdateExistingAttachment_When_AddingIdenticalAttachmentAgain() throws Exception {
    // Add Attachment before
    final int attachmentCount = task.getAttachments().size();
    ((AttachmentImpl) attachment).setId("TAI:0001");
    task.addAttachment(attachment);
    task.addAttachment(attachment); // overwrite, same id
    task.addAttachment(attachment); // overwrite, same id
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount + 1);

    // Add same again - ignored
    final int attachmentCount2 = task.getAttachments().size();
    Attachment redundantAttachment = task.getAttachments().get(0);
    task.addAttachment(redundantAttachment);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments()).hasSize(attachmentCount2);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testAddAttachmentAsNullValueWillBeIgnored() throws Exception {
    // Try to add a single NULL-Element
    final int attachmentCount = task.getAttachments().size();
    task.addAttachment(null);
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount);

    // Try to set the Attachments to NULL and update it
    ((TaskImpl) task).setAttachments(null);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments()).hasSize(attachmentCount); // locally, not persisted
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount); // persisted values not changed

    // Test no NullPointer on NULL-Value and removing it on current data.
    // New loading can do this, but returned value should got this "function", too.
    final int attachmentCount2 = task.getAttachments().size();
    task.getAttachments().add(null);
    task.getAttachments().add(null);
    task.getAttachments().add(null);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments()).hasSize(attachmentCount2); // locally, not persisted
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount2); // persisted values not changed
    assertThat(task.getPriority()).isEqualTo(1);
    assertThat(task.getPlanned().plus(Duration.ofDays(1))).isEqualTo(task.getDue());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testRemoveAttachment() throws Exception {
    task.addAttachment(attachment);
    task = taskService.updateTask(task);
    assertThat(task.getPriority()).isEqualTo(99);
    assertThat(task.getPlanned().plus(Duration.ofDays(1))).isEqualTo(task.getDue());
    int attachmentCount = task.getAttachments().size();
    Attachment attachmentToRemove = task.getAttachments().get(0);
    task.removeAttachment(attachmentToRemove.getId());
    task = taskService.updateTask(task);
    assertThat(task.getAttachments())
        .hasSize(attachmentCount - 1); // locally, removed and not persisted
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount - 1); // persisted, values removed
    assertThat(task.getPriority()).isEqualTo(1);
    assertThat(task.getPlanned().plus(Duration.ofDays(1))).isEqualTo(task.getDue());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testRemoveAttachmentWithNullAndNotAddedId() throws Exception {
    task.addAttachment(attachment);
    task = taskService.updateTask(task);
    int attachmentCount = task.getAttachments().size();

    task.removeAttachment(null);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments()).hasSize(attachmentCount); // locally, nothing changed
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount); // persisted, still same

    task.removeAttachment("INVALID ID HERE");
    task = taskService.updateTask(task);
    assertThat(task.getAttachments()).hasSize(attachmentCount); // locally, nothing changed
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount); // persisted, still same
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testUpdateAttachment() throws Exception {
    ((TaskImpl) task).setAttachments(new ArrayList<>());
    task = taskService.updateTask(task);
    assertThat(task.getPriority()).isEqualTo(1);
    assertThat(task.getPlanned().plus(Duration.ofDays(1))).isEqualTo(task.getDue());

    Attachment attachment = this.attachment;
    task.addAttachment(attachment);
    task = taskService.updateTask(task);
    assertThat(task.getPriority()).isEqualTo(99);
    assertThat(task.getPlanned().plus(Duration.ofDays(1))).isEqualTo(task.getDue());

    final int attachmentCount = task.getAttachments().size();

    String newChannel = attachment.getChannel() + "-X";
    task.getAttachments().get(0).setChannel(newChannel);
    Classification newClassification =
        taskanaEngine
            .getClassificationService()
            .getClassification("CLI:000000000000000000000000000000000001"); // Prio 999, SL PT5H
    task.getAttachments().get(0).setClassificationSummary(newClassification.asSummary());
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(attachmentCount);
    assertThat(task.getAttachments().get(0).getChannel()).isEqualTo(newChannel);
    assertThat(task.getPriority()).isEqualTo(999);
    Instant expDue = converter.addWorkingDaysToInstant(task.getPlanned(), Duration.ofDays(1));

    assertThat(task.getDue()).isEqualTo(expDue);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void modifyExistingAttachment() throws Exception {
    // setup test
    assertThat(task.getAttachments()).isEmpty();
    task.addAttachment(attachment);

    Attachment attachment2 =
        createAttachment(
            "L10303", // prio 101, SL PT7H
            createObjectReference(
                "COMPANY_B",
                "SYSTEM_C",
                "INSTANCE_C",
                "ArchiveId",
                "ABC45678901234567890123456789012345678901234567890"),
            "ROHRPOST",
            "2018-01-15",
            createSimpleCustomPropertyMap(4));
    task.addAttachment(attachment2);
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());

    assertThat(task.getPriority()).isEqualTo(101);
    Instant expDue = converter.addWorkingDaysToInstant(task.getPlanned(), Duration.ofDays(1));
    assertThat(task.getDue()).isEqualTo(expDue);
    assertThat(task.getAttachments())
        .hasSize(2)
        .areExactly(
            1,
            new Condition<>(
                e -> "E-MAIL".equals(e.getChannel()) && e.getCustomAttributeMap().size() == 3,
                "E-MAIL with 3 custom attributes"))
        .areExactly(
            1,
            new Condition<>(
                e -> "ROHRPOST".equals(e.getChannel()) && e.getCustomAttributeMap().size() == 4,
                "ROHRPOST with 4 custom attributes"));

    ClassificationSummary newClassificationSummary =
        taskanaEngine
            .getClassificationService()
            .getClassification("CLI:100000000000000000000000000000000006") // Prio 5, SL P16D
            .asSummary();
    // modify existing attachment
    for (Attachment att : task.getAttachments()) {
      att.setClassificationSummary(newClassificationSummary);
      if (att.getCustomAttributeMap().size() == 3) {
        att.setChannel("FAX");
      }
    }
    // modify existing attachment and task classification
    task.setClassificationKey("DOCTYPE_DEFAULT"); // Prio 99, SL P2000D
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getPriority()).isEqualTo(99);

    expDue = converter.addWorkingDaysToInstant(task.getPlanned(), Duration.ofDays(16));
    assertThat(task.getDue()).isEqualTo(expDue);
    assertThat(task.getAttachments())
        .hasSize(2)
        .areExactly(
            1,
            new Condition<>(
                e -> "FAX".equals(e.getChannel()) && e.getCustomAttributeMap().size() == 3,
                "FAX with 3 custom attributes"))
        .areExactly(
            1,
            new Condition<>(
                e -> "ROHRPOST".equals(e.getChannel()) && e.getCustomAttributeMap().size() == 4,
                "ROHRPOST with 4 custom attributes"));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void replaceExistingAttachments() throws Exception {
    // setup test
    assertThat(task.getAttachments()).isEmpty();
    task.addAttachment(attachment);
    Attachment attachment2 =
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_B",
                "SYSTEM_C",
                "INSTANCE_C",
                "ArchiveId",
                "ABC45678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomPropertyMap(4));
    task.addAttachment(attachment2);
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments()).hasSize(2);
    assertThat(task.getAttachments().get(0).getClassificationSummary().getKey())
        .isEqualTo("DOCTYPE_DEFAULT");

    Attachment attachment3 =
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_C",
                "SYSTEM_7",
                "INSTANCE_7",
                "ArchiveId",
                "ABC4567890123456789012345678901234567890DEF"),
            "DHL",
            "2018-01-15",
            createSimpleCustomPropertyMap(4));

    // replace existing attachments by new via addAttachment call
    task.getAttachments().clear();
    task.addAttachment(attachment3);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments()).hasSize(1);
    assertThat(task.getAttachments().get(0).getChannel()).isEqualTo("DHL");
    task.getAttachments().forEach(at -> assertThat(task.getModified()).isEqualTo(at.getModified()));
    // setup environment for 2nd version of replacement (list.add call)
    task.getAttachments().add(attachment2);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments()).hasSize(2);
    assertThat(task.getAttachments().get(1).getChannel()).isEqualTo("E-MAIL");
    // replace attachments
    task.getAttachments().clear();
    task.getAttachments().add(attachment3);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments()).hasSize(1);
    assertThat(task.getAttachments().get(0).getChannel()).isEqualTo("DHL");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testPrioDurationOfTaskFromAttachmentsAtUpdate() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010"); // prio 8, SL P7D
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    newTask.addAttachment(
        createAttachment(
            "DOCTYPE_DEFAULT", // prio 99, SL P2000D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomPropertyMap(3)));
    newTask.addAttachment(
        createAttachment(
            "L1060", // prio 1, SL P1D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomPropertyMap(3)));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    createdTask
        .getAttachments()
        .forEach(at -> assertThat(createdTask.getModified()).isEqualTo(at.getModified()));
    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(readTask.getAttachments()).isNotNull();
    assertThat(readTask.getAttachments()).hasSize(2);
    assertThat(readTask.getAttachments().get(1).getCreated()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getCreated())
        .isEqualTo(readTask.getAttachments().get(1).getModified());
    assertThat(readTask.getAttachments().get(0).getObjectReference()).isNotNull();

    assertThat(readTask.getPriority()).isEqualTo(99);

    Instant expDue = converter.addWorkingDaysToInstant(readTask.getPlanned(), Duration.ofDays(1));

    assertThat(readTask.getDue()).isEqualTo(expDue);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testAddCustomAttributeToAttachment() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();
    task =
        taskService.getTask(
            "TKI:000000000000000000000000000000000000"); // class T2000, prio 1, SL P1D
    attachment =
        createAttachment(
            "DOCTYPE_DEFAULT", // prio 99, SL P2000D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            null);
    attachment.getCustomAttributeMap().put("TEST_KEY", "TEST_VALUE");
    task.addAttachment(attachment);
    taskService.updateTask(task);
    Task updatedTask = taskService.getTask("TKI:000000000000000000000000000000000000");
    Attachment updatedAttachment =
        updatedTask.getAttachments().stream()
            .filter(a -> attachment.getId().equals(a.getId()))
            .findFirst()
            .orElse(null);
    assertThat(updatedAttachment).isNotNull();
    assertThat(updatedTask.getModified()).isEqualTo(updatedAttachment.getModified());
    assertThat(updatedAttachment.getCustomAttributeMap().get("TEST_KEY")).isEqualTo("TEST_VALUE");
  }
}
