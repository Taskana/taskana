package acceptance.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.util.DaysToWorkingDaysConverter;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
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

  UpdateTaskAttachmentsAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testAddNewAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    final int attachmentCount = task.getAttachments().size();
    assertEquals(1, task.getPriority());
    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(1)));
    task.addAttachment(attachment);

    task = taskService.updateTask(task);

    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments().size(), equalTo(attachmentCount + 1));
    assertThat(
        task.getAttachments().get(0).getClassificationSummary().getKey(),
        equalTo("DOCTYPE_DEFAULT"));
    assertThat(
        task.getAttachments().get(0).getObjectReference().getCompany(), equalTo("COMPANY_A"));
    assertThat(task.getAttachments().get(0).getObjectReference().getSystem(), equalTo("SYSTEM_B"));
    assertThat(
        task.getAttachments().get(0).getObjectReference().getSystemInstance(),
        equalTo("INSTANCE_B"));
    assertThat(task.getAttachments().get(0).getObjectReference().getType(), equalTo("ArchiveId"));
    assertThat(
        task.getAttachments().get(0).getObjectReference().getValue(),
        equalTo("12345678901234567890123456789012345678901234567890"));
    assertEquals(99, task.getPriority());
    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(1)));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testAddValidAttachmentTwice()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    task.getAttachments().clear();
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertEquals(0, task.getAttachments().size());

    AttachmentImpl attachment = (AttachmentImpl) this.attachment;
    attachment.setId("TAI:000017");
    task.addAttachment(attachment);
    task.addAttachment(attachment);
    task = taskService.updateTask(task);

    assertEquals(1, task.getAttachments().size());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testAddNewAttachmentTwiceWithoutTaskanaMethodWillThrowAttachmentPersistenceException()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    final int attachmentCount = 0;
    task.getAttachments().clear();
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments().size(), equalTo(attachmentCount));

    AttachmentImpl attachment = (AttachmentImpl) this.attachment;
    attachment.setId("TAI:000017");
    task.getAttachments().add(attachment);
    task.getAttachments().add(attachment);
    task.getAttachments().add(attachment);
    Assertions.assertThrows(
        AttachmentPersistenceException.class, () -> task = taskService.updateTask(task));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testAddExistingAttachmentAgainWillUpdateWhenNotEqual()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    // Add attachment before
    task = taskService.getTask(task.getId());
    final int attachmentCount = task.getAttachments().size();
    task.addAttachment(attachment);
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments().size(), equalTo(attachmentCount + 1));

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
    assertThat(task.getAttachments().size(), equalTo(attachmentCount2));
    assertThat(task.getAttachments().get(0).getChannel(), equalTo(newChannel));
    assertEquals(999, task.getPriority());

    DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(Instant.now());
    long calendarDays = converter.convertWorkingDaysToDays(task.getDue(), 1);
    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(calendarDays)));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testAddExistingAttachmentAgainWillDoNothingWhenEqual()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    // Add Attachment before
    final int attachmentCount = task.getAttachments().size();
    ((AttachmentImpl) attachment).setId("TAI:0001");
    task.addAttachment(attachment);
    task.addAttachment(attachment); // overwrite, same id
    task.addAttachment(attachment); // overwrite, same id
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments().size(), equalTo(attachmentCount + 1));

    // Add same again - ignored
    final int attachmentCount2 = task.getAttachments().size();
    Attachment redundantAttachment = task.getAttachments().get(0);
    task.addAttachment(redundantAttachment);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments().size(), equalTo(attachmentCount2));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testAddAttachmentAsNullValueWillBeIgnored()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    // Try to add a single NULL-Element
    final int attachmentCount = task.getAttachments().size();
    task.addAttachment(null);
    task = taskService.updateTask(task);
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments().size(), equalTo(attachmentCount));

    // Try to set the Attachments to NULL and update it
    ((TaskImpl) task).setAttachments(null);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // locally, not persisted
    task = taskService.getTask(task.getId());
    assertThat(
        task.getAttachments().size(), equalTo(attachmentCount)); // persisted values not changed

    // Test no NullPointer on NULL-Value and removing it on current data.
    // New loading can do this, but returned value should got this "function", too.
    final int attachmentCount2 = task.getAttachments().size();
    task.getAttachments().add(null);
    task.getAttachments().add(null);
    task.getAttachments().add(null);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments().size(), equalTo(attachmentCount2)); // locally, not persisted
    task = taskService.getTask(task.getId());
    assertThat(
        task.getAttachments().size(), equalTo(attachmentCount2)); // persisted values not changed
    assertEquals(1, task.getPriority());
    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(1)));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testRemoveAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    task.addAttachment(attachment);
    task = taskService.updateTask(task);
    assertEquals(99, task.getPriority());
    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(1)));
    int attachmentCount = task.getAttachments().size();
    Attachment attachmentToRemove = task.getAttachments().get(0);
    task.removeAttachment(attachmentToRemove.getId());
    task = taskService.updateTask(task);
    assertThat(
        task.getAttachments().size(),
        equalTo(attachmentCount - 1)); // locally, removed and not persisted
    task = taskService.getTask(task.getId());
    assertThat(
        task.getAttachments().size(), equalTo(attachmentCount - 1)); // persisted, values removed
    assertEquals(1, task.getPriority());
    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(1)));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testRemoveAttachmentWithNullAndNotAddedId()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    task.addAttachment(attachment);
    task = taskService.updateTask(task);
    int attachmentCount = task.getAttachments().size();

    task.removeAttachment(null);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // locally, nothing changed
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // persisted, still same

    task.removeAttachment("INVALID ID HERE");
    task = taskService.updateTask(task);
    assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // locally, nothing changed
    task = taskService.getTask(task.getId());
    assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // persisted, still same
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    ((TaskImpl) task).setAttachments(new ArrayList<>());
    task = taskService.updateTask(task);
    assertEquals(1, task.getPriority());
    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(1)));

    Attachment attachment = this.attachment;
    task.addAttachment(attachment);
    task = taskService.updateTask(task);
    assertEquals(99, task.getPriority());
    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(1)));

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
    assertThat(task.getAttachments().size(), equalTo(attachmentCount));
    assertThat(task.getAttachments().get(0).getChannel(), equalTo(newChannel));
    assertEquals(999, task.getPriority());

    DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(Instant.now());
    long calendarDays = converter.convertWorkingDaysToDays(task.getDue(), 1);

    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(calendarDays)));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void modifyExistingAttachment()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    // setup test
    assertThat(task.getAttachments().size(), equalTo(0));
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
    assertEquals(101, task.getPriority());
    DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(Instant.now());
    long calendarDays = converter.convertWorkingDaysToDays(task.getDue(), 1);

    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(calendarDays)));

    assertThat(task.getAttachments().size(), equalTo(2));
    List<Attachment> attachments = task.getAttachments();
    boolean rohrpostFound = false;
    boolean emailFound = false;
    for (Attachment att : attachments) {
      String channel = att.getChannel();
      int custAttSize = att.getCustomAttributes().size();
      if ("ROHRPOST".equals(channel)) {
        rohrpostFound = true;
      } else if ("E-MAIL".equals(channel)) {
        emailFound = true;
      } else {
        fail("unexpected attachment detected " + att);
      }
      assertTrue(
          ("ROHRPOST".equals(channel) && custAttSize == 4)
              || ("E-MAIL".equals(channel) && custAttSize == 3));
    }
    assertTrue(rohrpostFound && emailFound);

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
    assertEquals(99, task.getPriority());

    calendarDays = converter.convertWorkingDaysToDays(task.getDue(), 16);

    assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(calendarDays)));

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

      assertTrue(
          ("ROHRPOST".equals(channel) && custAttSize == 4)
              || ("FAX".equals(channel) && custAttSize == 3));
    }
    assertTrue(faxFound && rohrpostFound);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void replaceExistingAttachments()
      throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          InvalidArgumentException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {
    setUpMethod();
    // setup test
    assertThat(task.getAttachments().size(), equalTo(0));
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
    assertThat(task.getAttachments().size(), equalTo(2));
    assertThat(
        task.getAttachments().get(0).getClassificationSummary().getKey(),
        equalTo("DOCTYPE_DEFAULT"));

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
    assertThat(task.getAttachments().size(), equalTo(1));
    assertThat(task.getAttachments().get(0).getChannel(), equalTo("DHL"));

    // setup environment for 2nd version of replacement (list.add call)
    task.getAttachments().add(attachment2);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments().size(), equalTo(2));
    assertThat(task.getAttachments().get(1).getChannel(), equalTo("E-MAIL"));
    // replace attachments
    task.getAttachments().clear();
    task.getAttachments().add(attachment3);
    task = taskService.updateTask(task);
    assertThat(task.getAttachments().size(), equalTo(1));
    assertThat(task.getAttachments().get(0).getChannel(), equalTo("DHL"));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testPrioDurationOfTaskFromAttachmentsAtUpdate()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException,
          ConcurrencyException, AttachmentPersistenceException, InvalidStateException {

    setUpMethod();
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

    assertNotNull(createdTask.getId());
    assertThat(createdTask.getCreator(), equalTo(CurrentUserContext.getUserid()));

    Task readTask = taskService.getTask(createdTask.getId());
    assertNotNull(readTask);
    assertThat(readTask.getCreator(), equalTo(CurrentUserContext.getUserid()));
    assertNotNull(readTask.getAttachments());
    assertEquals(2, readTask.getAttachments().size());
    assertNotNull(readTask.getAttachments().get(1).getCreated());
    assertNotNull(readTask.getAttachments().get(1).getModified());
    assertEquals(
        readTask.getAttachments().get(0).getCreated(),
        readTask.getAttachments().get(1).getModified());
    // assertNotNull(readTask.getAttachments().get(0).getClassification());
    assertNotNull(readTask.getAttachments().get(0).getObjectReference());

    assertEquals(99, readTask.getPriority());

    DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(Instant.now());
    long calendarDays = converter.convertWorkingDaysToDays(readTask.getPlanned(), 1);

    assertEquals(readTask.getDue(), readTask.getPlanned().plus(Duration.ofDays(calendarDays)));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
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
    assertNotNull(updatedAttachment);
    assertEquals("TEST_VALUE", updatedAttachment.getCustomAttributes().get("TEST_KEY"));
  }

  // this method needs to run with access ids, otherwise getTask throws NotAuthorizedException
  // since only @Test and not @Before methods are run by JAASRunner, we call this method explicitely
  // at
  // the begin of each testcase....
  private void setUpMethod()
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
    assertThat(task, not(equalTo(null)));
  }
}
