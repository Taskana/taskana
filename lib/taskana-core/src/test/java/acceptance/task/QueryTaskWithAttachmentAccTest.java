package acceptance.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/**
 * Acceptance test for the usecase of adding/removing an attachment of a task and update the result
 * correctly.
 */
@ExtendWith(JaasExtension.class)
class QueryTaskWithAttachmentAccTest extends AbstractAccTest {

  QueryTaskWithAttachmentAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetAttachmentSummariesFromTask() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> tasks = taskService.createTaskQuery().classificationKeyIn("L110102").list();
    assertEquals(1, tasks.size());

    List<AttachmentSummary> attachmentSummaries = tasks.get(0).getAttachmentSummaries();
    assertNotNull(attachmentSummaries);
    assertEquals(2, attachmentSummaries.size());
  }

  @WithAccessId(userName = "user_1_2")
  @Test
  void testGetNoAttachmentSummaryFromTask() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> tasks = taskService.createTaskQuery().list();
    assertEquals(30, tasks.size());

    List<AttachmentSummary> attachmentSummaries = tasks.get(0).getAttachmentSummaries();
    assertNotNull(attachmentSummaries);
    assertTrue(attachmentSummaries.isEmpty());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testIfNewTaskHasEmptyAttachmentList() {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.newTask("WBI:100000000000000000000000000000000006");
    assertNotNull(task.getAttachments());
    assertNotNull(task.asSummary().getAttachmentSummaries());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testIfAttachmentSummariesAreCorrectUsingTaskQueryAndGetTaskById()
      throws TaskNotFoundException, NotAuthorizedException {
    TaskService taskService = taskanaEngine.getTaskService();
    // find Task with ID TKI:00...00
    List<TaskSummary> tasks =
        taskService.createTaskQuery().idIn("TKI:000000000000000000000000000000000000").list();
    assertEquals(1, tasks.size());
    List<AttachmentSummary> queryAttachmentSummaries = tasks.get(0).getAttachmentSummaries();

    Task originalTask = taskService.getTask("TKI:000000000000000000000000000000000000");
    List<Attachment> originalAttachments = originalTask.getAttachments();

    assertEquals(originalAttachments.size(), queryAttachmentSummaries.size());

    for (int i = 0; i < queryAttachmentSummaries.size(); i++) {
      // Test if it's the Summary of the Original Attachment
      assertEquals(originalAttachments.get(i).asSummary(), queryAttachmentSummaries.get(i));
      // Test if the values are correct
      assertEquals(
          originalAttachments.get(i).getChannel(), queryAttachmentSummaries.get(i).getChannel());
      assertEquals(
          originalAttachments.get(i).getClassificationSummary(),
          queryAttachmentSummaries.get(i).getClassificationSummary());
      assertEquals(
          originalAttachments.get(i).getCreated(), queryAttachmentSummaries.get(i).getCreated());
      assertEquals(originalAttachments.get(i).getId(), queryAttachmentSummaries.get(i).getId());
      assertEquals(
          originalAttachments.get(i).getModified(), queryAttachmentSummaries.get(i).getModified());
      assertEquals(
          originalAttachments.get(i).getObjectReference(),
          queryAttachmentSummaries.get(i).getObjectReference());
      assertEquals(
          originalAttachments.get(i).getReceived(), queryAttachmentSummaries.get(i).getReceived());
      assertEquals(
          originalAttachments.get(i).getTaskId(), queryAttachmentSummaries.get(i).getTaskId());

      // Verify that they're not the same Object
      assertNotEquals(
          originalAttachments.get(i).hashCode(), queryAttachmentSummaries.get(i).hashCode());
      assertNotEquals(
          originalAttachments.get(i).getClass(), queryAttachmentSummaries.get(i).getClass());
    }
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testIfAttachmentSummariesAreCorrect()
      throws InvalidArgumentException, TaskNotFoundException, NotAuthorizedException {
    TaskService taskService = taskanaEngine.getTaskService();
    // find Task with ID TKI:00...00
    List<TaskSummary> tasks =
        taskService
            .createTaskQuery()
            .classificationKeyIn("T2000")
            .customAttributeIn("1", "custom1")
            .list();
    assertEquals(1, tasks.size());
    List<AttachmentSummary> queryAttachmentSummaries = tasks.get(0).getAttachmentSummaries();

    Task originalTask = taskService.getTask("TKI:000000000000000000000000000000000000");
    List<Attachment> originalAttachments = originalTask.getAttachments();

    assertEquals(originalAttachments.size(), queryAttachmentSummaries.size());

    for (int i = 0; i < queryAttachmentSummaries.size(); i++) {
      // Test if it's the Summary of the Original Attachment
      assertEquals(originalAttachments.get(i).asSummary(), queryAttachmentSummaries.get(i));
      // Test if the values are correct
      assertEquals(
          originalAttachments.get(i).getChannel(), queryAttachmentSummaries.get(i).getChannel());
      assertEquals(
          originalAttachments.get(i).getClassificationSummary(),
          queryAttachmentSummaries.get(i).getClassificationSummary());
      assertEquals(
          originalAttachments.get(i).getCreated(), queryAttachmentSummaries.get(i).getCreated());
      assertEquals(originalAttachments.get(i).getId(), queryAttachmentSummaries.get(i).getId());
      assertEquals(
          originalAttachments.get(i).getModified(), queryAttachmentSummaries.get(i).getModified());
      assertEquals(
          originalAttachments.get(i).getObjectReference(),
          queryAttachmentSummaries.get(i).getObjectReference());
      assertEquals(
          originalAttachments.get(i).getReceived(), queryAttachmentSummaries.get(i).getReceived());
      assertEquals(
          originalAttachments.get(i).getTaskId(), queryAttachmentSummaries.get(i).getTaskId());

      // Verify that they're not the same Object
      assertNotEquals(
          originalAttachments.get(i).hashCode(), queryAttachmentSummaries.get(i).hashCode());
      assertNotEquals(
          originalAttachments.get(i).getClass(), queryAttachmentSummaries.get(i).getClass());
    }
  }
}
