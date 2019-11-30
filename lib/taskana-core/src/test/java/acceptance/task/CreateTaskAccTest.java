package acceptance.task;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.Attachment;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ObjectReference;
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
import pro.taskana.impl.TaskanaEngineProxyForTest;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.mappings.AttachmentMapper;
import pro.taskana.mappings.TaskTestMapper;
import pro.taskana.security.CurrentUserContext;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "create task" scenarios.
 */
@ExtendWith(JAASExtension.class)
class CreateTaskAccTest extends AbstractAccTest {

    private TaskService taskService;
    private ClassificationService classificationService;

    @BeforeEach
    void setup() {
        taskService = taskanaEngine.getTaskService();
        classificationService = taskanaEngine.getClassificationService();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateSimpleManualTask()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        ObjectReference objectReference = createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR",
            "1234567");
        newTask.setPrimaryObjRef(objectReference);
        newTask.setOwner("user_1_1");
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertThat(createdTask.getCreator(), equalTo(CurrentUserContext.getUserid()));
        assertThat(createdTask.getOwner(), equalTo("user_1_1"));
        assertEquals("USER_1_1", createdTask.getWorkbasketKey());
        assertEquals("T-Vertragstermin VERA", createdTask.getName());
        assertEquals(objectReference, createdTask.getPrimaryObjRef());
        assertNotNull(createdTask.getCreated());
        assertNotNull(createdTask.getModified());
        assertNotNull(createdTask.getBusinessProcessId());
        assertNull(createdTask.getClaimed());
        assertNull(createdTask.getCompleted());
        assertEquals(createdTask.getCreated(), createdTask.getModified());
        assertEquals(createdTask.getCreated(), createdTask.getPlanned());
        assertEquals(TaskState.READY, createdTask.getState());
        assertNull(createdTask.getParentBusinessProcessId());
        assertEquals(2, createdTask.getPriority());
        assertFalse(createdTask.isRead());
        assertFalse(createdTask.isTransferred());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateTaskWithPlanned()
        throws NotAuthorizedException, InvalidArgumentException,
        ClassificationNotFoundException, WorkbasketNotFoundException, TaskAlreadyExistException {

        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        Instant instantPlanned = Instant.now().plus(2, ChronoUnit.HOURS);
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setOwner("user_1_1");
        newTask.setPlanned(instantPlanned);
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertNotNull(createdTask.getCreated());
        assertNotNull(createdTask.getPlanned());
        assertEquals(instantPlanned, createdTask.getPlanned());
        assertTrue(createdTask.getCreated().isBefore(createdTask.getPlanned()));

        //verify that planned takes place 2 hours after creation (+- 5 seconds)
        Instant plannedAdjusted = createdTask.getPlanned().minus(2, ChronoUnit.HOURS);
        long difference = Duration.between(createdTask.getCreated(), plannedAdjusted).abs().getSeconds();
        //add some tolerance to ignore that "created" depends on execution speed
        long tolerance = 5;
        assertTrue(Math.abs(difference) < tolerance);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateTaskWithInvalidPlannedAndDue() {

        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        Instant instantPlanned = Instant.now().plus(2, ChronoUnit.HOURS);
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setOwner("user_1_1");

        newTask.setPlanned(instantPlanned);
        newTask.setDue(instantPlanned); //due date not according to service level
        Assertions.assertThrows(InvalidArgumentException.class, () -> taskService.createTask(newTask));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateTaskWithValidPlannedAndDue() throws ClassificationNotFoundException {

        Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
        long serviceLevelDays = Duration.parse(classification.getServiceLevel()).toDays();

        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        Instant instantPlanned = Instant.now();
        newTask.setClassificationKey(classification.getKey());
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setOwner("user_1_1");

        Optional<DaysToWorkingDaysConverter> converter = DaysToWorkingDaysConverter.getLastCreatedInstance();
        //TODO: this is a temporal bug fix because we did not define what happens when a task is planned on the weekend.
        long i = converter.get().convertWorkingDaysToDays(instantPlanned, 0);
        newTask.setPlanned(instantPlanned.plus(Duration.ofDays(i)));
        //due date according to service level
        Instant shouldBeDueDate = converter
            .map(c -> c.convertWorkingDaysToDays(newTask.getPlanned(), serviceLevelDays))
            .map(
                calendarDays -> newTask.getPlanned().plus(Duration.ofDays(calendarDays))).orElseThrow(
                RuntimeException::new);
        newTask.setDue(shouldBeDueDate);
        Assertions.assertDoesNotThrow(() -> taskService.createTask(newTask));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testIdempotencyOfTaskCreation()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setExternalId("MyExternalId");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertThat(createdTask.getCreator(), equalTo(CurrentUserContext.getUserid()));
        assertEquals("T-Vertragstermin VERA", createdTask.getName());
        assertEquals("1234567", createdTask.getPrimaryObjRef().getValue());
        assertNotNull(createdTask.getExternalId());
        assertNotNull(createdTask.getCreated());
        assertNotNull(createdTask.getModified());
        assertNotNull(createdTask.getBusinessProcessId());
        assertNull(createdTask.getClaimed());
        assertNull(createdTask.getCompleted());
        assertEquals(createdTask.getCreated(), createdTask.getModified());
        assertEquals(createdTask.getCreated(), createdTask.getPlanned());
        assertEquals(TaskState.READY, createdTask.getState());
        assertNull(createdTask.getParentBusinessProcessId());
        assertEquals(2, createdTask.getPriority());
        assertFalse(createdTask.isRead());
        assertFalse(createdTask.isTransferred());

        Task newTask2 = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask2.setExternalId("MyExternalId");
        newTask2.setClassificationKey("T2100");
        newTask2.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

        Assertions.assertThrows(TaskAlreadyExistException.class, () ->
            taskService.createTask(newTask2));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateSimpleTaskWithCustomAttributes()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException, NoSuchFieldException,
        IllegalAccessException {

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
        assertNull(createdTask.getClaimed());
        assertNull(createdTask.getCompleted());
        assertEquals(createdTask.getCreated(), createdTask.getModified());
        assertEquals(createdTask.getCreated(), createdTask.getPlanned());
        assertEquals(TaskState.READY, createdTask.getState());
        assertNull(createdTask.getParentBusinessProcessId());
        assertEquals(2, createdTask.getPriority());
        assertFalse(createdTask.isRead());
        assertFalse(createdTask.isTransferred());
        // verify that the database content is as expected
        TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest(taskanaEngine);
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
        assertEquals(customAttributesFromDb, customAttributesForCreate);

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateExternalTaskWithAttachment()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException, NoSuchFieldException,
        IllegalAccessException {

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
        TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest(taskanaEngine);
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
        assertEquals(customAttributesFromDb, customAttributesForCreate);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateExternalTaskWithMultipleAttachments()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

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
    void testCalculationOfDueDateAtCreate()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

        //SL P16D
        Classification classification = classificationService.getClassification("L110105", "DOMAIN_A");
        long serviceLevelDays = Duration.parse(classification.getServiceLevel()).toDays();
        assertTrue(serviceLevelDays > 5);

        Task newTask = taskService.newTask("USER_1_1", classification.getDomain());
        newTask.setClassificationKey(classification.getKey());
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

        Instant planned = Instant.now().plus(10, ChronoUnit.DAYS);
        newTask.setPlanned(planned);
        Task createdTask = taskService.createTask(newTask);
        assertNotNull(createdTask.getId());

        Task readTask = taskService.getTask(createdTask.getId());
        assertNotNull(readTask);
        assertEquals(planned, readTask.getPlanned());

        Optional<Instant> shouldBeDueDate = DaysToWorkingDaysConverter.getLastCreatedInstance()
            .map(converter -> converter.convertWorkingDaysToDays(readTask.getPlanned(), serviceLevelDays))
            .map(
                calendarDays -> readTask.getPlanned().plus(Duration.ofDays(calendarDays)));
        assertTrue(shouldBeDueDate.isPresent());
        assertEquals(readTask.getDue(), shouldBeDueDate.get());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCalculationOfPlannedDateAtCreate()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

        //SL P16D
        Classification classification = classificationService.getClassification("L110105", "DOMAIN_A");
        long serviceLevelDays = Duration.parse(classification.getServiceLevel()).toDays();
        assertTrue(serviceLevelDays > 5);

        Task newTask = taskService.newTask("USER_1_1", classification.getDomain());
        newTask.setClassificationKey(classification.getKey());
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

        Instant due = Instant.now().plus(40, ChronoUnit.DAYS);
        newTask.setDue(due);
        Task createdTask = taskService.createTask(newTask);
        assertNotNull(createdTask.getId());

        Task readTask = taskService.getTask(createdTask.getId());
        assertNotNull(readTask);
        assertEquals(due, readTask.getDue());

        Optional<Long> calendarDaysToSubstract = DaysToWorkingDaysConverter.getLastCreatedInstance()
            .map(converter -> converter.convertWorkingDaysToDays(due, -serviceLevelDays));

        assertTrue(calendarDaysToSubstract.isPresent());
        assertTrue(calendarDaysToSubstract.get() < 0);
        assertTrue(calendarDaysToSubstract.get() <= -serviceLevelDays);

        Instant shouldBePlannedDate = due.plus(Duration.ofDays(calendarDaysToSubstract.get()));
        assertEquals(readTask.getPlanned(), shouldBePlannedDate);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testPrioDurationOfTaskFromAttachmentsAtCreate()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

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

        assertEquals(99, readTask.getPriority());

        DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter
            .initialize(Collections.singletonList(new TimeIntervalColumnHeader(0)), Instant.now());
        long calendarDays = converter.convertWorkingDaysToDays(readTask.getPlanned(), 1);

        assertEquals(readTask.getDue(), readTask.getPlanned().plus(Duration.ofDays(calendarDays)));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testThrowsExceptionIfAttachmentIsInvalid()
        throws ClassificationNotFoundException {

        Consumer<Attachment> testCreateTask = (Attachment invalidAttachment) -> {

            Task taskWithInvalidAttachment = makeNewTask(taskService);
            taskWithInvalidAttachment.addAttachment(invalidAttachment);
            Assertions.assertThrows(InvalidArgumentException.class, () ->
                    taskService.createTask(taskWithInvalidAttachment),
                "Should have thrown an InvalidArgumentException, because Attachment-ObjRef is null.");
        };

        testCreateTask.accept(createAttachment("DOCTYPE_DEFAULT",
            null,
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));

        testCreateTask.accept(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId", null),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));

        testCreateTask.accept(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", null,
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));

        testCreateTask.accept(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", null, "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));

        testCreateTask.accept(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", null, "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));

        testCreateTask.accept(createAttachment("DOCTYPE_DEFAULT",
            createObjectReference(null, "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testUseCustomNameIfSetForNewTask()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

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
    void testUseClassificationMetadataFromCorrectDomainForNewTask()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

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
    @Test
    void testGetExceptionIfWorkbasketDoesNotExist() {

        Task newTask = taskService.newTask("UNKNOWN");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

        Assertions.assertThrows(WorkbasketNotFoundException.class, () ->
            taskService.createTask(newTask));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testGetExceptionIfAppendIsNotPermitted() {

        Task newTask = taskService.newTask("GPK_KSC", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

        Assertions.assertThrows(NotAuthorizedException.class, () ->
            taskService.createTask(newTask));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testThrowsExceptionIfMandatoryPrimaryObjectReferenceIsNotSetOrIncomplete() {

        Consumer<ObjectReference> testCreateTask = (ObjectReference objectReference) -> {

            Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
            newTask.setClassificationKey("T2100");
            if (objectReference != null) {
                newTask.setPrimaryObjRef(objectReference);
            }
            Assertions.assertThrows(InvalidArgumentException.class, () ->
                    taskService.createTask(newTask),
                "Should have thrown an InvalidArgumentException, because ObjRef-ObjRef is null.");
        };

        testCreateTask.accept(null);
        testCreateTask.accept(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
        testCreateTask.accept(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
        testCreateTask.accept(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));
        testCreateTask.accept(createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));
        testCreateTask.accept(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testSetDomainFromWorkbasket()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException {

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
    void testCreatedTaskObjectEqualsReadTaskObject()
        throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

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
    void testCreateSimpleTaskWithCallbackInfo()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException {

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
        assertNull(createdTask.getClaimed());
        assertNull(createdTask.getCompleted());
        assertEquals(createdTask.getCreated(), createdTask.getModified());
        assertEquals(createdTask.getCreated(), createdTask.getPlanned());
        assertEquals(TaskState.READY, createdTask.getState());
        assertNull(createdTask.getParentBusinessProcessId());
        assertEquals(2, createdTask.getPriority());
        assertFalse(createdTask.isRead());
        assertFalse(createdTask.isTransferred());

        Task retrievedTask = taskService.getTask(createdTask.getId());
        assertEquals(callbackInfo, retrievedTask.getCallbackInfo());

    }

    @Test
    void testCreateTaskWithSecurityButNoUserId() {

        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_B", "SYSTEM_B", "INSTANCE_B", "VNR", "1234567"));

        Assertions.assertThrows(NotAuthorizedException.class, () ->
            taskService.createTask(newTask));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateTaskAlreadyExisting()
        throws NotAuthorizedException, TaskNotFoundException {

        Task existingTask = taskService.getTask("TKI:000000000000000000000000000000000000");

        Assertions.assertThrows(TaskAlreadyExistException.class, () ->
            taskService.createTask(existingTask));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testCreateTaskNotAuthorizedOnWorkbasket() {

        Task task = taskService.newTask("TEAMLEAD_2", "DOMAIN_A");

        Assertions.assertThrows(NotAuthorizedException.class, () ->
            taskService.createTask(task));
    }

    private Task makeNewTask(TaskService taskService) {
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("L12010");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setClassificationKey("L12010");
        return newTask;
    }

}
