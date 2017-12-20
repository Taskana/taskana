package pro.taskana.impl.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.ObjectReferenceQuery;
import pro.taskana.Task;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.ClassificationQueryImpl;
import pro.taskana.impl.ClassificationServiceImpl;
import pro.taskana.impl.ObjectReferenceQueryImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketServiceImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.ClassificationImpl;
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
        TaskAlreadyExistException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket();
        workbasket.setName("workbasket");
        workbasket.setId("1"); // set id manually for authorization tests
        workbasket.setKey("k1");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);
        taskanaEngine.getClassificationService().createClassification(classification);
        connection.commit();
        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(workbasket.getKey());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);
        connection.commit();
        taskServiceImpl.getTaskById(workbasket.getId());

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTaskById(workbasket.getId());
        connection.commit();
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testCreateTask()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException {
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
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAuthorization(accessItem);

        task = taskServiceImpl.createTask(task);
        connection.commit();  // needed so that the change is visible in the other session

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTaskById(task.getId());
        Assert.assertNotNull(resultTask);
        connection.commit();
    }

    @Test
    public void testCreateTaskInTaskanaWithDefaultDb()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException {
        DataSource ds = TaskanaEngineConfiguration.createDefaultDataSource();
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(ds, false, false);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        Connection connection = ds.getConnection();
        te.setConnection(connection);
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();
        WorkbasketServiceImpl workBasketServiceImpl = (WorkbasketServiceImpl) te.getWorkbasketService();
        ClassificationServiceImpl classificationServiceImpl = (ClassificationServiceImpl) te.getClassificationService();

        Workbasket workbasket = workbasketService.newWorkbasket();
        workbasket.setName("workbasket");
        workbasket.setKey("K99");
        workbasket.setName("workbasket99");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workBasketServiceImpl.createWorkbasket(workbasket);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        workbasket.setName("workbasket99");
        classificationServiceImpl.createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(workbasket.getKey());
        task.setClassification(classification);
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
        TaskAlreadyExistException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        Classification classification = classificationService.newClassification();
        classification.setKey("TEST1");
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
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAuthorization(accessItem);

        Timestamp tomorrow = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

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
        test.setClassification(classification);
        test = taskServiceImpl.createTask(test);

        Task task = this.generateDummyTask();
        task.setClassification(classification);
        task.setName("Name");
        task.setPrimaryObjRef(objectReference);
        task.setPlanned(tomorrow);
        Task resultTask = taskServiceImpl.createTask(task);
        Assert.assertNotEquals(resultTask.getPlanned(), resultTask.getCreated());
        Assert.assertNotNull(resultTask.getDue());

        Task task2 = taskServiceImpl.newTask();
        task2.setWorkbasketKey(task.getWorkbasketKey());
        task2.setClassification(classification);
        task2.setPrimaryObjRef(objectReference);
        task2.setDescription("desc");
        Task resultTask2 = taskServiceImpl.createTask(task2);

        Assert.assertEquals(resultTask2.getPlanned(), resultTask2.getCreated());
        Assert.assertTrue(resultTask2.getName().equals(classification.getName()));

        Assert.assertEquals(resultTask.getClassification().getId(), resultTask2.getClassification().getId());
        Assert.assertTrue(resultTask.getDue().after(resultTask2.getDue()));
        Assert.assertFalse(resultTask.getName().equals(resultTask2.getName()));
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = WorkbasketNotFoundException.class)
    public void createTaskShouldThrowWorkbasketNotFoundException()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException,
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException {
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
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        Task test = this.generateDummyTask();
        test.setClassification(new ClassificationImpl());

        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey("wb");
        accessItem.setAccessId("Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAuthorization(accessItem);

        taskServiceImpl.createTask(test);
    }

    @WithAccessId(userName = "Elena", groupNames = {"DummyGroup"})
    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket();
        workbasket.setName("workbasket");
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        classificationService.createClassification(classification);
        workbasket.setId("1"); // set id manually for authorization tests
        workbasket.setKey("k1");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workbasketService.createWorkbasket(workbasket);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(workbasket.getKey());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);

        TaskanaEngineImpl taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        ClassificationQuery classificationQuery = new ClassificationQueryImpl(taskanaEngineImpl)
            .parentClassificationKey("pId1", "pId2")
            .category("cat1", "cat2")
            .type("oneType")
            .name("1Name", "name2")
            .descriptionLike("my desc")
            .priority(1, 2, 1)
            .serviceLevel("me", "and", "you");

        ObjectReferenceQuery objectReferenceQuery = new ObjectReferenceQueryImpl(taskanaEngineImpl)
            .company("first comp", "sonstwo gmbh")
            .system("sys")
            .type("type1", "type2")
            .systemInstance("sysInst1", "sysInst2")
            .value("val1", "val2", "val3");

        List<Task> results = taskServiceImpl.createTaskQuery()
            .name("bla", "test")
            .descriptionLike("test")
            .priority(1, 2, 2)
            .state(TaskState.CLAIMED)
            .workbasketKeyIn("k1", "k2")
            .owner("test", "test2", "bla")
            .customFields("test")
            .classification(classificationQuery)
            .objectReference(objectReferenceQuery)
            .list();

        Assert.assertEquals(0, results.size());
        connection.commit();
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void shouldTransferTaskToOtherWorkbasket()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, TaskNotFoundException, InterruptedException, TaskAlreadyExistException,
        SQLException, InvalidWorkbasketException {
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
        createWorkbasketWithSecurity(sourceWB, sourceWB.getOwner(), false, false, true, true);

        // Destination Workbasket
        wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("Desination-WorkBasket");
        wb.setDescription("Destination WB where Task should be transfered to");
        wb.setOwner(user);
        wb.setDomain("dd11");
        wb.setType(WorkbasketType.TOPIC);
        wb.setKey("wb2Key");
        destinationWB = workbasketService.createWorkbasket(wb);
        createWorkbasketWithSecurity(destinationWB, destinationWB.getOwner(), false, false, true, true);

        // Classification required for Task
        classification = (ClassificationImpl) classificationService.newClassification();
        classification.setCategory("Test Classification");
        classification.setDomain("test-domain");
        classification.setName("Transfert-Task Classification");
        classification.setKey("KEY");
        classificationService.createClassification(classification);

        // Task which should be transfered
        task = (TaskImpl) taskServiceImpl.newTask();
        task.setName("Task Name");
        task.setDescription("Task used for transfer Test");
        task.setWorkbasketKey(sourceWB.getKey());
        task.setRead(true);
        task.setTransferred(false);
        task.setModified(null);
        task.setClassification(classification);
        task.setOwner(user);
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
        InvalidWorkbasketException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        taskServiceImpl.transfer(UUID.randomUUID() + "_X", "1");
    }

    @WithAccessId(userName = "User")
    @Test
    public void shouldNotTransferByFailingSecurity() throws WorkbasketNotFoundException,
        ClassificationNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException, SQLException,
        TaskNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {
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

        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification();
        classification.setCategory("Test Classification");
        classification.setDomain("test-domain");
        classification.setName("Transfert-Task Classification");
        classification.setKey("KEY");
        classificationService.createClassification(classification);

        WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("BASE WB");
        wb.setDescription("Normal base WB");
        wb.setOwner(user);
        wb.setKey("wbKey1");
        wb.setDomain("myDomain");
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
        wbNoTransfer.setDomain("domNoTrans");
        wbNoTransfer.setType(WorkbasketType.GROUP);
        wbNoTransfer = (WorkbasketImpl) workbasketService.createWorkbasket(wbNoTransfer);
        createWorkbasketWithSecurity(wbNoTransfer, wbNoTransfer.getOwner(), true, true, true, false);

        TaskImpl task = (TaskImpl) taskServiceImpl.newTask();
        task.setName("Task Name");
        task.setDescription("Task used for transfer Test");
        task.setWorkbasketKey(wb.getKey());
        task.setOwner(user);
        task.setClassification(classification);
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

    private Task generateDummyTask()
        throws ClassificationAlreadyExistException, InvalidWorkbasketException, WorkbasketNotFoundException {
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket();
        workbasket.setKey("wb");
        workbasket.setName("wb");
        workbasket.setId("1"); // set id manually for authorization tests
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setWorkbasketKey(workbasket.getKey());
        task.setClassification(classification);
        return task;
    }

    private void generateSampleAccessItems() {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey("k1");
        accessItem.setAccessId("Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAuthorization(accessItem);

        WorkbasketAccessItem accessItem2 = new WorkbasketAccessItem();
        accessItem2.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem2.setWorkbasketKey("k2");
        accessItem2.setAccessId("DummyGroup");
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
