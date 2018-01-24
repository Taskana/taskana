package pro.taskana.impl.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.Task;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.ClassificationServiceImpl;
import pro.taskana.impl.JunitHelper;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketServiceImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskState;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketType;
import pro.taskana.security.CurrentUserContext;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Integration Test for TaskServiceImpl transactions with connection management mode EXPLICIT.
 *
 * @author EH
 */
@RunWith(JAASRunner.class)
public class TaskServiceImplIntExplicitTest {

    private DataSource dataSource;
    private TaskServiceImpl taskServiceImpl;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private ClassificationService classificationService;
    private WorkbasketService workbasketService;

    @BeforeClass
    public static void resetDb() throws SQLException {
        DataSource ds = TaskanaEngineConfigurationTest.getDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, true);
        FileUtils.deleteRecursive("~/data", true);
    }

    @Before
    public void setup() throws FileNotFoundException, SQLException, LoginException {
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        classificationService = taskanaEngine.getClassificationService();
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
        workbasketService = taskanaEngine.getWorkbasketService();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket();
        workbasket.setName("workbasket");
        workbasket.setId("1"); // set id manually for authorization tests
        workbasket.setKey("k1");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        Classification classification = classificationService.newClassification("novatec", "TEST", "type1");
        taskanaEngineImpl.getWorkbasketService().createWorkbasket(workbasket);
        taskanaEngineImpl.getClassificationService().createClassification(classification);
        connection.commit();
        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(workbasket.getKey());
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = taskServiceImpl.createTask(task);
        connection.commit();
        taskServiceImpl.getTask(workbasket.getId());

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTask(workbasket.getId());
        connection.commit();
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testCreateTask()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        Task task = this.generateDummyTask();
        connection.commit();

        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey("wb");
        accessItem.setAccessId("Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermRead(true);
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAuthorization(accessItem);

        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = taskServiceImpl.createTask(task);
        connection.commit();  // needed so that the change is visible in the other session

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTask(task.getId());
        Assert.assertNotNull(resultTask);
        connection.commit();
    }

    @Test
    public void testCreateTaskInTaskanaWithDefaultDb()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
        DataSource ds = TaskanaEngineConfiguration.createDefaultDataSource();
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(ds, false, false);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        Connection connection = ds.getConnection();
        te.setConnection(connection);
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, false);
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();
        WorkbasketServiceImpl workBasketServiceImpl = (WorkbasketServiceImpl) te.getWorkbasketService();
        ClassificationServiceImpl classificationServiceImpl = (ClassificationServiceImpl) te.getClassificationService();

        Workbasket workbasket = workbasketService.newWorkbasket();
        workbasket.setName("workbasket");
        workbasket.setKey("K99");
        workbasket.setName("workbasket99");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workbasket = workBasketServiceImpl.createWorkbasket(workbasket);
        Classification classification = classificationService.newClassification("novatec", "TEST", "t1");
        classification = classificationServiceImpl.createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(workbasket.getKey());
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task.addAttachment(null);
        task = taskServiceImpl.createTask(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
        connection.commit();
        te.setConnection(null);
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testCreateTaskWithPlannedAndName() throws SQLException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        Classification classification = classificationService.newClassification("novatec", "TEST1", "t1");
        classification.setCategory("MANUAL");
        classification.setName("classification name");
        classification.setServiceLevel("P1D");
        taskanaEngine.getClassificationService().createClassification(classification);

        ObjectReference objectReference = new ObjectReference();
        objectReference.setCompany("Novatec");
        objectReference.setSystem("System");
        objectReference.setSystemInstance("2");
        objectReference.setValue("4444");
        objectReference.setType("type");

        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey("wb");
        accessItem.setAccessId("Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermRead(true);
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAuthorization(accessItem);

        Instant tomorrow = Instant.now().plus(Duration.ofDays(1L));

        Workbasket wb = workbasketService.newWorkbasket();
        wb.setDomain("novatec");
        wb.setName("wbk1");
        wb.setType(WorkbasketType.PERSONAL);
        wb.setKey("k1");
        workbasketService.createWorkbasket(wb);

        Task test = taskServiceImpl.newTask();
        test.setWorkbasketKey("k1");
        test.setPrimaryObjRef(objectReference);
        test.setPlanned(tomorrow);
        test.setClassificationKey(classification.getKey());
        test = taskServiceImpl.createTask(test);

        Task task = this.generateDummyTask();
        task.setClassificationKey(classification.getKey());
        task.setName("Name");
        task.setPrimaryObjRef(objectReference);
        task.setPlanned(tomorrow);
        Task resultTask = taskServiceImpl.createTask(task);
        Assert.assertNotEquals(resultTask.getPlanned(), resultTask.getCreated());
        Assert.assertNotNull(resultTask.getDue());

        Task task2 = taskServiceImpl.newTask();
        task2.setWorkbasketKey(task.getWorkbasketKey());
        task2.setClassificationKey(classification.getKey());
        task2.setPrimaryObjRef(objectReference);
        task2.setDescription("desc");
        Task resultTask2 = taskServiceImpl.createTask(task2);

        Assert.assertEquals(resultTask2.getPlanned(), resultTask2.getCreated());
        Assert.assertTrue(resultTask2.getName().equals(classification.getName()));

        Assert.assertEquals(resultTask.getClassificationSummary().getId(),
            resultTask2.getClassificationSummary().getId());
        Assert.assertTrue(resultTask.getDue().isAfter(resultTask2.getDue()));
        Assert.assertFalse(resultTask.getName().equals(resultTask2.getName()));
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = WorkbasketNotFoundException.class)
    public void createTaskShouldThrowWorkbasketNotFoundException()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException,
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException,
        InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        Task test = this.generateDummyTask();
        test.setWorkbasketKey("2");
        taskServiceImpl.createTask(test);
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = ClassificationNotFoundException.class)
    public void createManualTaskShouldThrowClassificationNotFoundException()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException,
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException,
        InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        Workbasket wb = workbasketService.newWorkbasket();
        wb.setName("dummy-WB");
        wb.setKey("WB NR.1");
        wb.setDomain("nova");
        wb.setType(WorkbasketType.PERSONAL);
        wb = workbasketService.createWorkbasket(wb);
        this.createWorkbasketWithSecurity(wb, CurrentUserContext.getUserid(), true, true,
            true, false);
        Classification classification = classificationService.newClassification(wb.getDomain(),
            UUID.randomUUID().toString(), "t1"); // not persisted,
        // not found.
        classification.setName("not persisted - so not found.");

        Task task = this.generateDummyTask();
        task.setWorkbasketKey(wb.getKey());
        task.setClassificationKey(classification.getKey());
        taskServiceImpl.createTask(task);
    }

    @WithAccessId(userName = "Elena", groupNames = {"DummyGroup"})
    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException, SystemException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket();
        workbasket.setName("workbasket");
        Classification classification = classificationService.newClassification("novatec", "TEST", "t1");
        classificationService.createClassification(classification);
        workbasket.setId("1"); // set id manually for authorization tests
        workbasket.setKey("k1");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workbasket = (WorkbasketImpl) workbasketService.createWorkbasket(workbasket);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey("k1");
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = taskServiceImpl.createTask(task);

        List<TaskSummary> results = taskServiceImpl.createTaskQuery()
            .nameIn("bla", "test")
            .descriptionLike("test")
            .priorityIn(1, 2, 2)
            .stateIn(TaskState.CLAIMED)
            .workbasketKeyIn("k1")
            .ownerIn("test", "test2", "bla")
            .customFieldsIn("test")
            .classificationKeyIn("pId1", "pId2")
            .primaryObjectReferenceCompanyIn("first comp", "sonstwo gmbh")
            .primaryObjectReferenceSystemIn("sys")
            .primaryObjectReferenceTypeIn("type1", "type2")
            .primaryObjectReferenceSystemInstanceIn("sysInst1", "sysInst2")
            .primaryObjectReferenceValueIn("val1", "val2", "val3")
            .list();

        Assert.assertEquals(0, results.size());
        connection.commit();
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void shouldTransferTaskToOtherWorkbasket()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, TaskNotFoundException, InterruptedException, TaskAlreadyExistException,
        SQLException, InvalidWorkbasketException, InvalidArgumentException {
        Workbasket sourceWB;
        Workbasket destinationWB;
        WorkbasketImpl wb;
        ClassificationImpl classification;
        TaskImpl task;
        Task resultTask;
        final int sleepTime = 100;
        final String user = CurrentUserContext.getUserid();
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        // Source Workbasket
        wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("Basic-Workbasket");
        wb.setDescription("Just used as base WB for Task here");
        wb.setOwner(user);
        wb.setDomain("domain");
        wb.setKey("sourceWbKey");
        wb.setType(WorkbasketType.PERSONAL);
        sourceWB = workbasketService.createWorkbasket(wb);

        createWorkbasketWithSecurity(wb, wb.getOwner(), false, false, false, false);
        createWorkbasketWithSecurity(sourceWB, sourceWB.getOwner(), true, true, true, true);

        // Destination Workbasket
        wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("Desination-WorkBasket");
        wb.setDescription("Destination WB where Task should be transfered to");
        wb.setOwner(user);
        wb.setDomain("domain");
        wb.setType(WorkbasketType.TOPIC);
        wb.setKey("wb2Key");
        destinationWB = workbasketService.createWorkbasket(wb);
        createWorkbasketWithSecurity(destinationWB, destinationWB.getOwner(), false, true, true, true);

        // Classification required for Task
        classification = (ClassificationImpl) classificationService.newClassification("domain", "KEY", "t1");
        classification.setCategory("Test Classification");
        classification.setName("Transfert-Task Classification");
        classificationService.createClassification(classification);

        // Task which should be transfered
        task = (TaskImpl) taskServiceImpl.newTask();
        task.setName("Task Name");
        task.setDescription("Task used for transfer Test");
        task.setWorkbasketKey(sourceWB.getKey());
        task.setRead(true);
        task.setTransferred(false);
        task.setModified(null);
        task.setClassificationKey("KEY");
        task.setOwner(user);
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = (TaskImpl) taskServiceImpl.createTask(task);
        Thread.sleep(sleepTime);    // Sleep for modification-timestamp
        connection.commit();

        resultTask = taskServiceImpl.transfer(task.getId(), destinationWB.getKey());
        connection.commit();
        assertThat(resultTask.isRead(), equalTo(false));
        assertThat(resultTask.isTransferred(), equalTo(true));
        assertThat(resultTask.getWorkbasketKey(), equalTo(destinationWB.getKey()));
        assertThat(resultTask.getModified(), not(equalTo(null)));
        assertThat(resultTask.getModified(), not(equalTo(task.getModified())));
        assertThat(resultTask.getCreated(), not(equalTo(null)));
        assertThat(resultTask.getCreated(), equalTo(task.getCreated()));
    }

    @Test(expected = TaskNotFoundException.class)
    public void shouldNotTransferAnyTask()
        throws WorkbasketNotFoundException, NotAuthorizedException, TaskNotFoundException, SQLException,
        InvalidWorkbasketException, ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        taskServiceImpl.transfer(UUID.randomUUID() + "_X", "1");
    }

    @WithAccessId(userName = "User")
    @Test
    public void shouldNotTransferByFailingSecurity() throws WorkbasketNotFoundException,
        ClassificationNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException, SQLException,
        TaskNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
        final String user = "User";

        // Set up Security for this Test
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, true);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
        classificationService = taskanaEngine.getClassificationService();
        workbasketService = taskanaEngine.getWorkbasketService();

        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification("test-domain",
            "KEY",
            "t1");
        classification.setCategory("Test Classification");
        classification.setName("Transfert-Task Classification");
        classificationService.createClassification(classification);

        WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("BASE WB");
        wb.setDescription("Normal base WB");
        wb.setOwner(user);
        wb.setKey("wbKey1");
        wb.setDomain("test-domain");
        wb.setType(WorkbasketType.GROUP);
        wb = (WorkbasketImpl) workbasketService.createWorkbasket(wb);
        createWorkbasketWithSecurity(wb, wb.getOwner(), true, true, true, true);

        WorkbasketImpl wbNoAppend = (WorkbasketImpl) workbasketService.newWorkbasket();
        wbNoAppend.setName("Test-Security-WorkBasket-APPEND");
        wbNoAppend.setDescription("Workbasket without permission APPEND on Task");
        wbNoAppend.setOwner(user);
        wbNoAppend.setKey("keyNoAppend");
        wbNoAppend.setDomain("anotherDomain");
        wbNoAppend.setType(WorkbasketType.CLEARANCE);
        wbNoAppend = (WorkbasketImpl) workbasketService.createWorkbasket(wbNoAppend);
        createWorkbasketWithSecurity(wbNoAppend, wbNoAppend.getOwner(), true, true, false, true);

        WorkbasketImpl wbNoTransfer = (WorkbasketImpl) workbasketService.newWorkbasket();
        wbNoTransfer.setName("Test-Security-WorkBasket-TRANSFER");
        wbNoTransfer.setDescription("Workbasket without permission TRANSFER on Task");
        wbNoTransfer.setOwner(user);
        wbNoTransfer.setKey("keyNoTransfer");
        wbNoTransfer.setDomain("test-domain");
        wbNoTransfer.setType(WorkbasketType.GROUP);
        wbNoTransfer = (WorkbasketImpl) workbasketService.createWorkbasket(wbNoTransfer);
        createWorkbasketWithSecurity(wbNoTransfer, wbNoTransfer.getOwner(), true, true, true, false);

        TaskImpl task = (TaskImpl) taskServiceImpl.newTask();
        task.setName("Task Name");
        task.setDescription("Task used for transfer Test");
        task.setWorkbasketKey(wb.getKey());
        task.setOwner(user);
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = (TaskImpl) taskServiceImpl.createTask(task);

        // Check failing with missing APPEND
        try {
            task = (TaskImpl) taskServiceImpl.transfer(task.getId(), wbNoAppend.getKey());
            fail("Transfer Task should be FAILD, because there are no APPEND-Rights on destination WB.");
        } catch (NotAuthorizedException e) {
            if (!e.getMessage().contains("APPEND")) {
                fail("Transfer Task should be FAILD, because there are no APPEND-Rights on destination WB.");
            }
            assertThat(task.isTransferred(), equalTo(false));
            assertThat(task.getWorkbasketKey(), not(equalTo(wbNoAppend.getKey())));
            assertThat(task.getWorkbasketKey(), equalTo(wb.getKey()));
        }

        // Check failing with missing TRANSFER
        task.setId("");
        task.setWorkbasketKey(wbNoTransfer.getKey());
        task = (TaskImpl) taskServiceImpl.createTask(task);
        try {
            task = (TaskImpl) taskServiceImpl.transfer(task.getId(), wb.getKey());
            fail("Transfer Task should be FAILD, because there are no TRANSFER-Rights on current WB.");
        } catch (NotAuthorizedException e) {
            if (!e.getMessage().contains("TRANSFER")) {
                fail("Transfer Task should be FAILD, because there are no APPEND-Rights on current WB.");
            }
            assertThat(task.isTransferred(), equalTo(false));
            assertThat(task.getWorkbasketKey(), not(equalTo(wbNoAppend.getKey())));
        }
    }

    private Task generateDummyTask() throws ClassificationAlreadyExistException, ClassificationNotFoundException,
        WorkbasketNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket();
        workbasket.setKey("wb");
        workbasket.setName("wb");
        workbasket.setId("1"); // set id manually for authorization tests
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        Classification classification = classificationService.newClassification("novatec", "TEST", "t1");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setWorkbasketKey(workbasket.getKey());
        task.setClassificationKey(classification.getKey());
        return task;
    }

    private void generateSampleAccessItems() {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey("k1");
        accessItem.setAccessId("Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermRead(true);
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAuthorization(accessItem);

        WorkbasketAccessItem accessItem2 = new WorkbasketAccessItem();
        accessItem2.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem2.setWorkbasketKey("k2");
        accessItem2.setAccessId("DummyGroup");
        accessItem.setPermRead(true);
        accessItem2.setPermOpen(true);
        workbasketService.createWorkbasketAuthorization(accessItem2);
    }

    private void createWorkbasketWithSecurity(Workbasket wb, String accessId, boolean permOpen,
        boolean permRead, boolean permAppend, boolean permTransfer) {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey(wb.getKey());
        accessItem.setAccessId(accessId);
        accessItem.setPermOpen(permOpen);
        accessItem.setPermRead(permRead);
        accessItem.setPermAppend(permAppend);
        accessItem.setPermTransfer(permTransfer);
        workbasketService.createWorkbasketAuthorization(accessItem);
    }

    @After
    public void cleanUp() {
        taskanaEngineImpl.setConnection(null);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

}
