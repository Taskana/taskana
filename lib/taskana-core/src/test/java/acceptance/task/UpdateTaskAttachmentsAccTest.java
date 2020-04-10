package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.common.internal.util.WorkingDaysToDaysConverter;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

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
  void setUp()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
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
            createSimpleCustomProperties(3));
    task.getAttachments().clear();
    taskService.updateTask(task);
    assertThat(task).isNotNull();
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testAddNewAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
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
        .containsOnly(task.getModified());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testAddValidAttachmentTwice()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
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
        .containsOnly(task.getModified());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testAddNewAttachmentTwiceWithoutTaskanaMethodWillThrowAttachmentPersistenceException()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
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

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testAddExistingAttachmentAgainWillUpdateWhenNotEqual()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
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

    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(Instant.now());
    long calendarDays = converter.convertWorkingDaysToDays(task.getDue(), 1);
    assertThat(task.getPlanned().plus(Duration.ofDays(calendarDays))).isEqualTo(task.getDue());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testAddExistingAttachmentAgainWillDoNothingWhenEqual()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
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

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testAddAttachmentAsNullValueWillBeIgnored()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
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

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testRemoveAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
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

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testRemoveAttachmentWithNullAndNotAddedId()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
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

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testUpdateAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
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
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(Instant.now());
    long calendarDays = converter.convertWorkingDaysToDays(task.getDue(), 1);

    assertThat(task.getPlanned().plus(Duration.ofDays(calendarDays))).isEqualTo(task.getDue());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void modifyExistingAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
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
            createSimpleCustomProperties(4));
    task.addAttachment(attachment2);
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getPriority()).isEqualTo(101);
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(Instant.now());
    long calendarDays = converter.convertWorkingDaysToDays(task.getDue(), 1);

    assertThat(task.getPlanned().plus(Duration.ofDays(calendarDays))).isEqualTo(task.getDue());

    assertThat(task.getAttachments()).hasSize(2);
    List<Attachment> attachments = task.getAttachments();
    boolean rohrpostFound = false;
    boolean emailFound = false;
    for (Attachment att : attachments) {
      String channel = att.getChannel();
      int custAttSize = att.getCustomAttributes().size();
      if ("ROHRPOST".equals(channel)) {
        rohrpostFound = true;
        assertThat(task.getModified()).isEqualTo(att.getModified());
      } else if ("E-MAIL".equals(channel)) {
        emailFound = true;
      } else {
        fail("unexpected attachment detected " + att);
      }
      assertThat(
              ("ROHRPOST".equals(channel) && custAttSize == 4)
                  || ("E-MAIL".equals(channel) && custAttSize == 3))
          .isTrue();
    }
    assertThat(rohrpostFound && emailFound).isTrue();

    ClassificationSummary newClassificationSummary =
        taskanaEngine
            .getClassificationService()
            .getClassification("CLI:100000000000000000000000000000000006") // Prio 5, SL P16D
            .asSummary();
    // modify existing attachment
    for (Attachment att : task.getAttachments()) {
      att.setClassificationSummary(newClassificationSummary);
      if (att.getCustomAttributes().size() == 3) {
        att.setChannel("FAX");
      }
    }
    // modify existing attachment and task classification
    task.setClassificationKey("DOCTYPE_DEFAULT"); // Prio 99, SL P2000D
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getPriority()).isEqualTo(99);

    calendarDays = converter.convertWorkingDaysToDays(task.getDue(), 16);

    assertThat(task.getPlanned().plus(Duration.ofDays(calendarDays))).isEqualTo(task.getDue());

    rohrpostFound = false;
    boolean faxFound = false;

    for (Attachment att : task.getAttachments()) {
      String channel = att.getChannel();
      int custAttSize = att.getCustomAttributes().size();
      if ("FAX".equals(channel)) {
        faxFound = true;
      } else if ("ROHRPOST".equals(channel)) {
        rohrpostFound = true;
      } else {
        fail("unexpected attachment detected " + att);
      }

      assertThat(
              ("ROHRPOST".equals(channel) && custAttSize == 4)
                  || ("FAX".equals(channel) && custAttSize == 3))
          .isTrue();
    }
    assertThat(faxFound && rohrpostFound).isTrue();
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void replaceExistingAttachments()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
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
            createSimpleCustomProperties(4));
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
            createSimpleCustomProperties(4));

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

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testPrioDurationOfTaskFromAttachmentsAtUpdate()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException,
          ConcurrencyException, AttachmentPersistenceException, InvalidStateException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
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
            createSimpleCustomProperties(3)));
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
            createSimpleCustomProperties(3)));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
    createdTask
        .getAttachments()
        .forEach(at -> assertThat(createdTask.getModified()).isEqualTo(at.getModified()));
    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
    assertThat(readTask.getAttachments()).isNotNull();
    assertThat(readTask.getAttachments()).hasSize(2);
    assertThat(readTask.getAttachments().get(1).getCreated()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getCreated())
        .isEqualTo(readTask.getAttachments().get(1).getModified());
    assertThat(readTask.getAttachments().get(0).getObjectReference()).isNotNull();

    assertThat(readTask.getPriority()).isEqualTo(99);

    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(Instant.now());
    long calendarDays = converter.convertWorkingDaysToDays(readTask.getPlanned(), 1);

    assertThat(readTask.getPlanned().plus(Duration.ofDays(calendarDays)))
        .isEqualTo(readTask.getDue());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testAddCustomAttributeToAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {

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
    attachment.getCustomAttributes().put("TEST_KEY", "TEST_VALUE");
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
    assertThat(updatedAttachment.getCustomAttributes().get("TEST_KEY")).isEqualTo("TEST_VALUE");
  }
}
