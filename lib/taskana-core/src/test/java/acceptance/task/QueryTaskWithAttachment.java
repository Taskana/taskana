package acceptance.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Attachment;
import pro.taskana.AttachmentSummary;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for the usecase of adding/removing an attachment of a task and update the result correctly.
 */
@RunWith(JAASRunner.class)
public class QueryTaskWithAttachment extends AbstractAccTest {

    public QueryTaskWithAttachment() {
        super();
    }

    @WithAccessId(
            userName = "user_1_1",
            groupNames = {"group_1"})
    @Test
    public void testGetAttachmentSummariesFromTask() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> tasks = taskService.createTaskQuery()
                .classificationKeyIn("L110102")
                .list();
        assertEquals(1, tasks.size());

        List<AttachmentSummary> attachmentSummaries = tasks.get(0).getAttachmentSummaries();
        assertNotNull(attachmentSummaries);
        assertEquals(2, attachmentSummaries.size());
    }

    @WithAccessId(
            userName = "user_1_2")
    @Test
    public void testGetNoAttachmentSummaryFromTask() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> tasks = taskService.createTaskQuery()
                .list();
        assertEquals(20, tasks.size());

        List<AttachmentSummary> attachmentSummaries = tasks.get(0).getAttachmentSummaries();
        assertNotNull(attachmentSummaries);
        assertTrue(attachmentSummaries.isEmpty());
    }

    @WithAccessId(
            userName = "user_1_1",
            groupNames = {"group_1"})
    @Test
    public void testIfNewTaskHasEmptyAttachmentList() {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.newTask("WBI:100000000000000000000000000000000006");
        assertNotNull(task.getAttachments());
        assertNotNull(task.asSummary().getAttachmentSummaries());
    }

    @WithAccessId(
            userName = "user_1_1",
            groupNames = {"group_1"})
    @Test
    public void testIfAttachmentSummariesAreCorrect()
            throws InvalidArgumentException, TaskNotFoundException, NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        // find Task with ID TKI:00...00
        List<TaskSummary> tasks = taskService.createTaskQuery()
                .classificationKeyIn("T2000")
                .customAttributeIn("1", "custom1")
                .list();
        assertEquals(1, tasks.size());

        Task originalTask = taskService.getTask("TKI:000000000000000000000000000000000000");
        Attachment originalAttachment = originalTask.getAttachments().get(0);

        // Test if it's the Summary of the Original Attachment
        AttachmentSummary queryAttachmentSummary = tasks.get(0).getAttachmentSummaries().get(0);
        assertEquals(originalAttachment.asSummary(), queryAttachmentSummary);

        // Test if the values are correct
        assertEquals(originalAttachment.getChannel(), queryAttachmentSummary.getChannel());
        assertEquals(originalAttachment.getClassificationSummary(), queryAttachmentSummary.getClassificationSummary());
        assertEquals(originalAttachment.getCreated(), queryAttachmentSummary.getCreated());
        assertEquals(originalAttachment.getId(), queryAttachmentSummary.getId());
        assertEquals(originalAttachment.getModified(), queryAttachmentSummary.getModified());
        assertEquals(originalAttachment.getObjectReference(), queryAttachmentSummary.getObjectReference());
        assertEquals(originalAttachment.getReceived(), queryAttachmentSummary.getReceived());
        assertEquals(originalAttachment.getTaskId(), queryAttachmentSummary.getTaskId());

        // Verify that they're not the same Object
        assertNotEquals(originalAttachment.hashCode(), queryAttachmentSummary.hashCode());
        assertNotEquals(originalAttachment.getClass(), queryAttachmentSummary.getClass());
    }

}
