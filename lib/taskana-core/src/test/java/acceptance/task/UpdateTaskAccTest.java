package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
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
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        Instant modifiedOriginal = task.getModified();
        task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "7654321"));
        task.setBusinessProcessId("MY_PROCESS_ID");
        task.setParentBusinessProcessId("MY_PARENT_PROCESS_ID");
        Task updatedTask = taskService.updateTask(task);
        updatedTask = taskService.getTask(updatedTask.getId());

        assertNotNull(updatedTask);
        assertEquals("7654321", updatedTask.getPrimaryObjRef().getValue());
        assertNotNull(updatedTask.getCreated());
        assertNotNull(updatedTask.getModified());
        assertFalse(modifiedOriginal.isAfter(updatedTask.getModified()));
        assertNotEquals(updatedTask.getCreated(), updatedTask.getModified());
        assertEquals(task.getCreated(), updatedTask.getCreated());
        assertEquals(task.isRead(), updatedTask.isRead());
        assertEquals("MY_PROCESS_ID", updatedTask.getBusinessProcessId());
        assertEquals("MY_PARENT_PROCESS_ID", updatedTask.getParentBusinessProcessId());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testThrowsExceptionIfMandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
        throws NotAuthorizedException, ClassificationNotFoundException, TaskNotFoundException, ConcurrencyException,
        AttachmentPersistenceException {

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
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        TaskNotFoundException, ConcurrencyException, AttachmentPersistenceException {

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
        throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        NotAuthorizedException, AttachmentPersistenceException {

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
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testUpdateReadFlagOfTask()
        throws TaskNotFoundException, NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();

        taskService.setTaskRead("TKI:000000000000000000000000000000000030", true);
        Task updatedTask = taskService.getTask("TKI:000000000000000000000000000000000030");
        assertNotNull(updatedTask);
        assertTrue(updatedTask.isRead());
        assertFalse(updatedTask.getCreated().equals(updatedTask.getModified()));

        taskService.setTaskRead("TKI:000000000000000000000000000000000030", false);
        Task updatedTask2 = taskService.getTask("TKI:000000000000000000000000000000000030");
        assertNotNull(updatedTask2);
        assertFalse(updatedTask2.isRead());
        assertFalse(updatedTask2.getModified().isBefore(updatedTask.getModified()));

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCustomPropertiesOfTask()
        throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException, ConcurrencyException,
        NotAuthorizedException, AttachmentPersistenceException {
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
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        TaskNotFoundException, ConcurrencyException, AttachmentPersistenceException {

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

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testUpdateCallbackInfoOfSimpleTask()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException, ConcurrencyException,
        AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertEquals("1234567", createdTask.getPrimaryObjRef().getValue());
        assertNotNull(createdTask.getCreated());
        assertNotNull(createdTask.getModified());
        assertNotNull(createdTask.getBusinessProcessId());
        assertEquals(null, createdTask.getClaimed());
        assertEquals(null, createdTask.getCompleted());
        assertEquals(createdTask.getCreated(), createdTask.getModified());
        assertEquals(createdTask.getCreated(), createdTask.getPlanned());
        assertEquals(TaskState.READY, createdTask.getState());
        assertEquals(null, createdTask.getParentBusinessProcessId());
        assertEquals(2, createdTask.getPriority());
        assertEquals(false, createdTask.isRead());
        assertEquals(false, createdTask.isTransferred());

        Task retrievedTask = taskService.getTask(createdTask.getId());

        HashMap<String, String> callbackInfo = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            callbackInfo.put("info_" + i, "Value of info_" + i);
        }
        retrievedTask.setCallbackInfo(callbackInfo);
        taskService.updateTask(retrievedTask);

        Task retrievedUpdatedTask = taskService.getTask(createdTask.getId());

        assertEquals(callbackInfo, retrievedUpdatedTask.getCallbackInfo());

    }

}
