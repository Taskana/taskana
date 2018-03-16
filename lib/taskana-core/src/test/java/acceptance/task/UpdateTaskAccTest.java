package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import pro.taskana.impl.ObjectReference;
import pro.taskana.impl.TaskImpl;
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
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        Instant modifiedOriginal = task.getModified();
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "7654321"));
        Task updatedTask = taskService.updateTask(task);
        updatedTask = taskService.getTask(updatedTask.getId());

        Assert.assertNotNull(updatedTask);
        Assert.assertEquals("7654321", updatedTask.getPrimaryObjRef().getValue());
        Assert.assertNotNull(updatedTask.getCreated());
        Assert.assertNotNull(updatedTask.getModified());
        Assert.assertFalse(modifiedOriginal.isAfter(updatedTask.getModified()));
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
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        task.setPrimaryObjRef(null);
        try {
            taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
        try {
            taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
        try {
            taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));
        try {
            taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));
        try {
            taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        task.setPrimaryObjRef(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        try {
            taskService.updateTask(task);
            fail("update() should have thrown InvalidArgumentException.");
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
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        Task task2 = taskService.getTask("TKI:000000000000000000000000000000000000");

        task.setCustomAttribute("1", "willi");
        Task updatedTask = null;
        updatedTask = taskService.updateTask(task);
        updatedTask = taskService.getTask(updatedTask.getId());

        task2.setCustomAttribute("2", "Walter");
        try {
            updatedTask = taskService.updateTask(task2);
        } catch (ConcurrencyException ex) {
            assertEquals("The task has already been updated by another user", ex.getMessage());
        }

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
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
        groupNames = {"group_1"})
    @Test
    public void testCustomPropertiesOfTask()
        throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        task.setCustomAttribute("1", "T2100");
        Task updatedTask = taskService.updateTask(task);
        updatedTask = taskService.getTask(updatedTask.getId());

        assertNotNull(updatedTask);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidArgumentException.class)
    public void testUpdateOfWorkbasketKeyWhatIsNotAllowed()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        ((TaskImpl) task).setWorkbasketKey("USER_2_2");
        taskService.updateTask(task);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testUpdateTasksByPorForUser1() throws InvalidArgumentException {
        ObjectReference por = new ObjectReference();
        por.setCompany("00");
        por.setSystem("PASystem");
        por.setSystemInstance("00");
        por.setType("VNR");
        por.setValue("22334455");
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("7", "This is modifiedValue 7");
        customProperties.put("14", null);
        customProperties.put("3", "This is modifiedValue 3");
        customProperties.put("16", "This is modifiedValue 16");
        TaskService taskService = taskanaEngine.getTaskService();

        List<String> taskIds = taskService.updateTasks(por, customProperties);
        assertEquals(0, taskIds.size());

    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testUpdateTasksByPor() throws InvalidArgumentException, TaskNotFoundException, NotAuthorizedException {
        ObjectReference por = new ObjectReference();
        por.setCompany("00");
        por.setSystem("PASystem");
        por.setSystemInstance("00");
        por.setType("VNR");
        por.setValue("22334455");
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("7", "This is modifiedValue 7");
        customProperties.put("14", null);
        customProperties.put("3", "This is modifiedValue 3");
        customProperties.put("16", "This is modifiedValue 16");
        TaskService taskService = taskanaEngine.getTaskService();

        List<String> taskIds = taskService.updateTasks(por, customProperties);
        assertEquals(6, taskIds.size());
        for (String taskId : taskIds) {
            Task task = taskService.getTask(taskId);
            assertEquals("This is modifiedValue 3", task.getCustomAttribute("3"));
            assertEquals("This is modifiedValue 7", task.getCustomAttribute("7"));
            assertEquals("This is modifiedValue 16", task.getCustomAttribute("16"));
            assertNull(task.getCustomAttribute("14"));
        }

    }

}
