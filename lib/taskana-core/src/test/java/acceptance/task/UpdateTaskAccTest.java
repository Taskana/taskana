package acceptance.task;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
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
        groupNames = {"group_1"})
    @Test
    public void testUpdatePrimaryObjectReferenceOfTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTaskById("TKI:000000000000000000000000000000000000");
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "7654321"));
        Task updatedTask = taskService.updateTask(task);

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
        groupNames = {"group_1"})
    @Test
    public void testThrowsExceptionIfMandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTaskById("TKI:000000000000000000000000000000000000");
        task.setPrimaryObjRef(null);
        Task updatedTask = null;
        try {
            updatedTask = taskService.updateTask(task);
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
        try {
            updatedTask = taskService.updateTask(task);
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
        try {
            updatedTask = taskService.updateTask(task);
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));
        try {
            updatedTask = taskService.updateTask(task);
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));
        try {
            updatedTask = taskService.updateTask(task);
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        try {
            updatedTask = taskService.updateTask(task);
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testThrowsExceptionIfTaskHasAlreadyBeenUpdated()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTaskById("TKI:000000000000000000000000000000000000");
        Task task2 = taskService.getTaskById("TKI:000000000000000000000000000000000000");

        task.setCustom1("willi");
        Task updatedTask = null;
        updatedTask = taskService.updateTask(task);

        task2.setCustom2("Walter");
        try {
            updatedTask = taskService.updateTask(task2);
        } catch (ConcurrencyException ex) {
            assertEquals("The task has already been updated by another user", ex.getMessage());
        }

    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testUpdateClassificationOfTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        // TaskService taskService = taskanaEngine.getTaskService();
        // Task with classification T2000
        // Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        // task.setClassificationKey("T2100"));
        // Task updatedTask = taskService.updateTask(task);
        //
        // assertNotNull(updatedTask);
        // assertEquals("T2100", updatedTask.getClassification().getKey());
        // assertNotEquals(updatedTask.getCreated(), updatedTask.getModified());
        // assertEquals(22, updatedTask.getPriority());
        // assertEquals(task.getPlanned(), updatedTask.getPlanned());
        // assertEquals(???, updatedTask.getDue()); // should be one day later
        // assertEquals("T-Vertragstermin VERA", updatedTask.getName());
        // assertEquals("T-Vertragstermin VERA", updatedTask.getDescription());
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCustomPropertiesOfTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        // TaskService taskService = taskanaEngine.getTaskService();
        // Task with classification T2000
        // Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        // task.setCustomProperty1("T2100"));
        // ...
        // Task updatedTask = taskService.updateTask(task);
        //
        // assertNotNull(updatedTask);
        // meaningful assertions
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    // @Test(expected = InvalidOperationException.class)
    public void testUpdateOfWorkbasketKeyNotAllowed()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        // Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        // task.setWorkbasketKey("USER_2_2");
        // Task updatedTask = taskService.updateTask(task);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
