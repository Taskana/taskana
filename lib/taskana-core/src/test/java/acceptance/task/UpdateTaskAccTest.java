package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.ClassificationSummary;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "update task" scenarios.
 */
@RunWith(JAASRunner.class)
public class UpdateTaskAccTest extends AbstractAccTest {

    public UpdateTaskAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testUpdatePrimaryObjectReferenceOfTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "7654321"));
        Task updatedTask = taskService.updateTask(task);
        updatedTask = taskService.getTask(updatedTask.getId());

        Assert.assertNotNull(updatedTask);
        Assert.assertEquals("7654321", updatedTask.getPrimaryObjRef().getValue());
        Assert.assertNotNull(updatedTask.getCreated());
        Assert.assertNotNull(updatedTask.getModified());
        Assert.assertNotEquals(updatedTask.getCreated(), updatedTask.getModified());
        Assert.assertEquals(task.getCreated(), updatedTask.getCreated());
        Assert.assertEquals(task.isRead(), updatedTask.isRead());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testThrowsExceptionIfMandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        task.setPrimaryObjRef(null);
        Task updatedTask = null;
        try {
            updatedTask = taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
        try {
            updatedTask = taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
        try {
            updatedTask = taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));
        try {
            updatedTask = taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));
        try {
            updatedTask = taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        try {
            updatedTask = taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testThrowsExceptionIfTaskHasAlreadyBeenUpdated()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        Task task2 = taskService.getTask("TKI:000000000000000000000000000000000000");

        task.setCustom1("willi");
        Task updatedTask = null;
        updatedTask = taskService.updateTask(task);
        updatedTask = taskService.getTask(updatedTask.getId());

        task2.setCustom2("Walter");
        try {
            updatedTask = taskService.updateTask(task2);
        } catch (ConcurrencyException ex) {
            assertEquals("The task has already been updated by another user", ex.getMessage());
        }

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testUpdateClassificationOfTask()
        throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        ClassificationSummary classificationSummary = task.getClassificationSummary();
        task.setClassificationKey("T2100");
        Task updatedTask = taskService.updateTask(task);
        updatedTask = taskService.getTask(updatedTask.getId());

        assertNotNull(updatedTask);
        assertEquals("T2100", updatedTask.getClassificationSummary().getKey());
        assertThat(updatedTask.getClassificationSummary(), not(equalTo(classificationSummary)));
        assertNotEquals(updatedTask.getCreated(), updatedTask.getModified());
        assertEquals(task.getPlanned(), updatedTask.getPlanned());
        assertEquals(task.getName(), updatedTask.getName());
        assertEquals(task.getDescription(), updatedTask.getDescription());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testCustomPropertiesOfTask()
        throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        task.setCustom1("T2100");
        Task updatedTask = taskService.updateTask(task);
        updatedTask = taskService.getTask(updatedTask.getId());

        assertNotNull(updatedTask);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test(expected = InvalidArgumentException.class)
    public void testUpdateOfWorkbasketKeyWhatIsNotAllowed()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        task.setWorkbasketKey("USER_2_2");
        Task updatedTask = taskService.updateTask(task);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
