package acceptance.task;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.DaysToWorkingDaysConverter;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.TaskanaEngineProxyForTest;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.mappings.AttachmentMapper;
import pro.taskana.mappings.TaskTestMapper;
import pro.taskana.security.CurrentUserContext;
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
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertThat(createdTask.getCreator(), equalTo(CurrentUserContext.getUserid()));
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
    public void testCreateSimpleTaskWithCustomAttributes()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Map<String, String> customAttributesForCreate = createSimpleCustomProperties(13);
        newTask.setCustomAttributes(customAttributesForCreate);
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
        // verify that the database content is as expected
        TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest((TaskanaEngineImpl) taskanaEngine);
        try {
            SqlSession session = engineProxy.getSqlSession();
            Configuration config = session.getConfiguration();
            if (!config.hasMapper(TaskTestMapper.class)) {
                config.addMapper(TaskTestMapper.class);
            }
            TaskTestMapper mapper = session.getMapper(TaskTestMapper.class);

            engineProxy.openConnection();
            String customProperties = mapper.getCustomAttributesAsString(createdTask.getId());
            assertTrue(customProperties.contains("\"Property_13\":\"Property Value of Property_13\""));
            assertTrue(customProperties.contains("\"Property_12\":\"Property Value of Property_12\""));
            assertTrue(customProperties.contains("\"Property_11\":\"Property Value of Property_11\""));
            assertTrue(customProperties.contains("\"Property_10\":\"Property Value of Property_10\""));
            assertTrue(customProperties.contains("\"Property_9\":\"Property Value of Property_9\""));
            assertTrue(customProperties.contains("\"Property_8\":\"Property Value of Property_8\""));
            assertTrue(customProperties.contains("\"Property_7\":\"Property Value of Property_7\""));
            assertTrue(customProperties.contains("\"Property_6\":\"Property Value of Property_6\""));
            assertTrue(customProperties.contains("\"Property_5\":\"Property Value of Property_5\""));
            assertTrue(customProperties.contains("\"Property_4\":\"Property Value of Property_4\""));
            assertTrue(customProperties.contains("\"Property_3\":\"Property Value of Property_3\""));
            assertTrue(customProperties.contains("\"Property_2\":\"Property Value of Property_2\""));
            assertTrue(customProperties.contains("\"Property_1\":\"Property Value of Property_1\""));
        } finally {
            engineProxy.returnConnection();
        }
        // verify that the map is correctly retrieved from the database
        Task retrievedTask = taskService.getTask(createdTask.getId());
        Map<String, String> customAttributesFromDb = retrievedTask.getCustomAttributes();
        assertNotNull(customAttributesFromDb);
        assertTrue(customAttributesFromDb.equals(customAttributesForCreate));

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCreateExternalTaskWithAttachment()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("L12010");
        Map<String, String> customAttributesForCreate = createSimpleCustomProperties(27);
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", customAttributesForCreate));
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);
        assertNotNull(createdTask.getId());
        assertThat(createdTask.getCreator(), equalTo(CurrentUserContext.getUserid()));

        // verify that the database content is as expected
        TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest((TaskanaEngineImpl) taskanaEngine);
        try {
            SqlSession session = engineProxy.getSqlSession();
            AttachmentMapper mapper = session.getMapper(AttachmentMapper.class);
            engineProxy.openConnection();
            String customProperties = mapper.getCustomAttributesAsString(createdTask.getAttachments().get(0).getId());
            assertTrue(customProperties.contains("\"Property_26\":\"Property Value of Property_26\""));
            assertTrue(customProperties.contains("\"Property_25\":\"Property Value of Property_25\""));
            assertTrue(customProperties.contains("\"Property_21\":\"Property Value of Property_21\""));
            assertTrue(customProperties.contains("\"Property_19\":\"Property Value of Property_19\""));
            assertTrue(customProperties.contains("\"Property_16\":\"Property Value of Property_16\""));
            assertTrue(customProperties.contains("\"Property_12\":\"Property Value of Property_12\""));
            assertTrue(customProperties.contains("\"Property_11\":\"Property Value of Property_11\""));
            assertTrue(customProperties.contains("\"Property_7\":\"Property Value of Property_7\""));
            assertTrue(customProperties.contains("\"Property_6\":\"Property Value of Property_6\""));
        } finally {
            engineProxy.returnConnection();
        }

        Task readTask = taskService.getTask(createdTask.getId());
        assertNotNull(readTask);
        assertThat(readTask.getCreator(), equalTo(CurrentUserContext.getUserid()));
        assertNotNull(readTask.getAttachments());
        assertEquals(1, readTask.getAttachments().size());
        assertNotNull(readTask.getAttachments().get(0).getCreated());
        assertNotNull(readTask.getAttachments().get(0).getModified());
        assertEquals(readTask.getAttachments().get(0).getCreated(), readTask.getAttachments().get(0).getModified());
        assertNotNull(readTask.getAttachments().get(0).getClassificationSummary());
        assertNotNull(readTask.getAttachments().get(0).getClassificationSummary().getId());
        assertNotNull(readTask.getAttachments().get(0).getClassificationSummary().getKey());
        assertNotNull(readTask.getAttachments().get(0).getClassificationSummary().getType());
        assertNotNull(readTask.getAttachments().get(0).getClassificationSummary().getCategory());
        assertNotNull(readTask.getAttachments().get(0).getClassificationSummary().getDomain());
        assertNotNull(readTask.getAttachments().get(0).getClassificationSummary().getServiceLevel());
        assertNotNull(readTask.getAttachments().get(0).getReceived());
        assertNotNull(readTask.getAttachments().get(0).getChannel());
        assertNotNull(readTask.getAttachments().get(0).getObjectReference());
        // verify that the map is correctly retrieved from the database
        Map<String, String> customAttributesFromDb = readTask.getAttachments().get(0).getCustomAttributes();
        assertNotNull(customAttributesFromDb);
        assertTrue(customAttributesFromDb.equals(customAttributesForCreate));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCreateExternalTaskWithMultipleAttachments()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
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
        assertEquals(readTask.getAttachments().get(0).getCreated(), readTask.getAttachments().get(1).getModified());
        // assertNotNull(readTask.getAttachments().get(0).getClassification());
        assertNotNull(readTask.getAttachments().get(0).getObjectReference());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testPrioDurationOfTaskFromAttachmentsAtCreate()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("L12010"); // prio 8, SL P7D
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",  // prio 99, SL P2000D
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        newTask.addAttachment(createAttachment("L1060", // prio 1, SL P1D
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
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
        assertEquals(readTask.getAttachments().get(0).getCreated(), readTask.getAttachments().get(1).getModified());
        // assertNotNull(readTask.getAttachments().get(0).getClassification());
        assertNotNull(readTask.getAttachments().get(0).getObjectReference());

        assertTrue(readTask.getPriority() == 99);

        DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter
            .initialize(Collections.singletonList(new TimeIntervalColumnHeader(0)), Instant.now());
        long calendarDays = converter.convertWorkingDaysToDays(readTask.getPlanned(), 1);

        assertTrue(readTask.getDue().equals(readTask.getPlanned().plus(Duration.ofDays(calendarDays))));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testThrowsExceptionIfAttachmentIsInvalid()
        throws NotAuthorizedException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

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

    private Task makeNewTask(TaskService taskService) {
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
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
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setName("Test Name");
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertThat(createdTask.getCreator(), equalTo(CurrentUserContext.getUserid()));
        assertEquals("Test Name", createdTask.getName());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testUseClassificationMetadataFromCorrectDomainForNewTask()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setName("Test Name");
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertThat(createdTask.getCreator(), equalTo(CurrentUserContext.getUserid()));
        assertEquals(2, createdTask.getPriority());  // priority is 22 in DOMAIN_B, task is created in DOMAIN_A
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetExceptionIfWorkbasketDoesNotExist()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

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
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("GPK_KSC", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        taskService.createTask(newTask);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testThrowsExceptionIfMandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
        throws NotAuthorizedException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-Value is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-Type is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-SystemInstances is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));
        try {
            taskService.createTask(newTask);
            fail("Should have thrown an InvalidArgumentException, becasue ObjRef-System is null.");
        } catch (InvalidArgumentException ex) {
            // nothing to do
        }

        // Exception
        newTask = taskService.newTask("WBI:100000000000000000000000000000000006");
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
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

        TaskService taskService = taskanaEngine.getTaskService();
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.getWorkbasket("USER_1_1", "DOMAIN_A");

        Task newTask = taskService.newTask("WBI:100000000000000000000000000000000006");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertThat(createdTask.getCreator(), equalTo(CurrentUserContext.getUserid()));
        assertNotNull(createdTask.getDomain());
        assertEquals(workbasket.getDomain(), createdTask.getDomain());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCreatedTaskObjectEqualsReadTaskObject()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        for (int i = 1; i < 16; i++) {
            newTask.setCustomAttribute(Integer.toString(i), "VALUE " + i);
        }
        newTask.setCustomAttributes(createSimpleCustomProperties(5));
        newTask.setDescription("Description of test task");
        newTask.setNote("My note");
        newTask.addAttachment(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
        Task createdTask = taskService.createTask(newTask);
        Task readTask = taskService.getTask(createdTask.getId());

        assertEquals(createdTask, readTask);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCreateSimpleTaskWithCallbackInfo()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

        HashMap<String, String> callbackInfo = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            callbackInfo.put("info_" + i, "Value of info_" + i);
        }
        newTask.setCallbackInfo(callbackInfo);
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
        assertEquals(callbackInfo, retrievedTask.getCallbackInfo());

    }

}
