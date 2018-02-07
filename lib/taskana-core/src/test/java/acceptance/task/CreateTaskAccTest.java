package acceptance.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.TaskState;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "create task" scenarios.
 */
@RunWith(JAASRunner.class)
public class CreateTaskAccTest extends AbstractAccTest {

    public CreateTaskAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCreateSimpleManualTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        TaskSummary createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertEquals("T-Vertragstermin VERA", createdTask.getName());
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
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCreateExternalTaskWithAttachment()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("L12010");
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        TaskSummary createdTask = taskService.createTask(newTask);
        assertNotNull(createdTask.getTaskId());

        Task readTask = taskService.getTask(createdTask.getTaskId());
        assertNotNull(readTask);
        assertNotNull(readTask.getAttachments());
        assertEquals(1, readTask.getAttachments().size());
        assertNotNull(readTask.getAttachments().get(0).getCreated());
        assertNotNull(readTask.getAttachments().get(0).getModified());
        assertEquals(readTask.getAttachments().get(0).getCreated(), readTask.getAttachments().get(0).getModified());
        assertNotNull(readTask.getAttachments().get(0).getClassificationSummary());
        assertNotNull(readTask.getAttachments().get(0).getObjectReference());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCreateExternalTaskWithMultipleAttachments()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("L12010");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        TaskSummary createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask.getTaskId());

        Task readTask = taskService.getTask(createdTask.getTaskId());

        assertNotNull(readTask);
        assertNotNull(readTask.getAttachments());
        assertEquals(2, readTask.getAttachments().size());
        assertNotNull(readTask.getAttachments().get(1).getCreated());
        assertNotNull(readTask.getAttachments().get(1).getModified());
        assertEquals(readTask.getAttachments().get(0).getCreated(), readTask.getAttachments().get(1).getModified());
        // assertNotNull(readTask.getAttachments().get(0).getClassification());
        assertNotNull(readTask.getAttachments().get(0).getObjectReference());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testThrowsExceptionIfAttachmentIsInvalid()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = makeNewTask(taskService);
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            null,
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue Attachment-ObjRef is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        newTask = makeNewTask(taskService);
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId", null),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-Value is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        newTask = makeNewTask(taskService);
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", null,
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-Type is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        newTask = makeNewTask(taskService);
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", null, "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-SystemInstance is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        newTask = makeNewTask(taskService);
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", null, "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-System is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
        newTask = makeNewTask(taskService);
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference(null, "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-Company is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
    }

    private Task makeNewTask(TaskService taskService) throws ClassificationNotFoundException {
        Task newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("L12010");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setClassificationKey("L12010");
        return newTask;
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testUseCustomNameIfSetForNewTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setName("Test Name");
        TaskSummary createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertEquals("Test Name", createdTask.getName());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testUseClassificationMetadataFromCorrectDomainForNewTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setName("Test Name");
        TaskSummary createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertEquals(2, createdTask.getPriority());  // priority is 22 in DOMAIN_B, task is created in DOMAIN_A
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetExceptionIfWorkbasketDoesNotExist()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("UNKNOWN");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        taskService.createTask(newTask);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedException.class)
    public void testGetExceptionIfAppendIsNotPermitted()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("GPK_KSC");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        taskService.createTask(newTask);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testThrowsExceptionIfMandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-Value is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-Type is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-SystemInstances is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-System is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-Company is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testSetDomainFromWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.getWorkbasketByKey("USER_1_1");

        Task newTask = taskService.newTask("USER_1_1");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        TaskSummary createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertNotNull(createdTask.getDomain());
        assertEquals(workbasket.getDomain(), createdTask.getDomain());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
