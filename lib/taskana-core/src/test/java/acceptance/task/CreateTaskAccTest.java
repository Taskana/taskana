package acceptance.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
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
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("USER_1_1");
        Task createdTask = taskService.createTask(newTask);

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

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testCreateExternalTaskWithAttachment()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("L12010", "DOMAIN_A"));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("USER_1_1");
        // newTask.addAttachment(createAttachment("DOKTYP_DEFAULT",
        // createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
        // "12345678901234567890123456789012345678901234567890"),
        // "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        // assertNotNull(createdTask.getAttachments());
        // assertEquals(1, createdTask.getAttachments().size());
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testCreateExternalTaskWithMultipleAttachments()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("L12010", "DOMAIN_A"));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("USER_1_1");
        // newTask.addAttachment(createAttachment("DOKTYP_DEFAULT",
        // createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
        // "12345678901234567890123456789012345678901234567890"),
        // "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        // newTask.addAttachment(createAttachment("DOKTYP_DEFAULT",
        // createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
        // "12345678901234567890123456789012345678901234567890"),
        // "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        // assertNotNull(createdTask.getAttachments());
        // assertEquals(2, createdTask.getAttachments().size());
        // further assertions
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testThrowsExceptionIfAttachmentIsInvalid()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("L12010", "DOMAIN_A"));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("USER_1_1");
        // newTask.addAttachment(createAttachment("DOKTYP_DEFAULT",
        // null,
        // "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        Task createdTask = taskService.createTask(newTask);

        // Further exceptions
        // null in object reference
        //

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testUseCustomNameIfSetForNewTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("USER_1_1");
        newTask.setName("Test Name");
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertEquals("Test Name", createdTask.getName());
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testUseClassificationMetadataFromCorrectDomainForNewTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_B"));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("USER_1_1");
        newTask.setName("Test Name");
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertEquals(2, createdTask.getPriority());  // priority is 22 in DOMAIN_B, task is created in DOMAIN_A
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testImprovedUseClassificationMetadataFromCorrectDomainForNewTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask();
        // newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("USER_1_1");
        newTask.setName("Test Name");
        Task createdTask = taskService.createTask(newTask);

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
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("UNKNOWN");
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
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setWorkbasketKey("GPK_KSC");
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
        Task newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setWorkbasketKey("USER_1_1");
        Task createdTask;
        try {
            createdTask = taskService.createTask(newTask);
        } catch (InvalidArgumentException ex) {
            assertEquals("primary ObjectReference of task must not be null", ex.getMessage());
        }

        // Exception

        newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setWorkbasketKey("USER_1_1");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
        try {
            createdTask = taskService.createTask(newTask);
        } catch (InvalidArgumentException ex) {
            assertEquals("Value of primary ObjectReference of task must not be empty", ex.getMessage());
        }

        // Exception

        newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setWorkbasketKey("USER_1_1");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));

        try {
            createdTask = taskService.createTask(newTask);
        } catch (InvalidArgumentException ex) {
            assertEquals("Type of primary ObjectReference of task must not be empty", ex.getMessage());
        }

        // Exception

        newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setWorkbasketKey("USER_1_1");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));

        try {
            createdTask = taskService.createTask(newTask);
        } catch (InvalidArgumentException ex) {
            assertEquals("SystemInstance of primary ObjectReference of task must not be empty", ex.getMessage());
        }

        // Exception

        newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setWorkbasketKey("USER_1_1");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));

        try {
            createdTask = taskService.createTask(newTask);
        } catch (InvalidArgumentException ex) {
            assertEquals("System of primary ObjectReference of task must not be empty", ex.getMessage());
        }

        // Exception

        newTask = taskService.newTask();
        newTask.setClassification(taskanaEngine.getClassificationService().getClassification("T2100", "DOMAIN_A"));
        newTask.setWorkbasketKey("USER_1_1");
        newTask.setPrimaryObjRef(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

        try {
            createdTask = taskService.createTask(newTask);
        } catch (InvalidArgumentException ex) {
            assertEquals("Company of primary ObjectReference of task must not be empty", ex.getMessage());
        }

        // Exception

    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
