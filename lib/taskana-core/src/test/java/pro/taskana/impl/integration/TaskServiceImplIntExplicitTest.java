package pro.taskana.impl.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.KeyDomain;
import pro.taskana.Task;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.ClassificationServiceImpl;
import pro.taskana.impl.JunitHelper;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketServiceImpl;
import pro.taskana.impl.WorkbasketSummaryImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
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
    public static void resetDb() {
        DataSource ds = TaskanaEngineConfigurationTest.getDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, true);
    }

    @Before
    public void setup() throws SQLException {
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false,
            TaskanaEngineConfigurationTest.getSchemaName());
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        classificationService = taskanaEngine.getClassificationService();
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
        workbasketService = taskanaEngine.getWorkbasketService();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail()
        throws SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("k1", "DOMAIN_A");
        workbasket.setName("workbasket");
        workbasket.setId("1"); // set id manually for authorization tests

        workbasket.setType(WorkbasketType.GROUP);
        Classification classification = classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
        taskanaEngineImpl.getWorkbasketService().createWorkbasket(workbasket);

        WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermRead(true);
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAccessItem(accessItem);

        taskanaEngineImpl.getClassificationService().createClassification(classification);
        connection.commit();
        Task task = taskServiceImpl.newTask(workbasket.getId());
        task.setName("Unit Test Task");
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = taskServiceImpl.createTask(task);
        connection.commit();
        task = taskServiceImpl.getTask(task.getId());

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTask(workbasket.getId());
        connection.commit();
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testCreateTask()
        throws SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Task task = this.generateDummyTask();
        connection.commit();

        WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermRead(true);
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAccessItem(accessItem);

        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = taskServiceImpl.createTask(task);
        connection.commit();  // needed so that the change is visible in the other session

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTask(task.getId());
        Assert.assertNotNull(resultTask);
        connection.commit();
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void testCreateTaskInTaskanaWithDefaultDb()
        throws SQLException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        DataSource ds = TaskanaEngineConfiguration.createDefaultDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, false);
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(ds, false, false,
            null);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        Connection connection = ds.getConnection();
        te.setConnection(connection);
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();
        WorkbasketServiceImpl workBasketServiceImpl = (WorkbasketServiceImpl) te.getWorkbasketService();
        ClassificationServiceImpl classificationServiceImpl = (ClassificationServiceImpl) te.getClassificationService();

        Workbasket workbasket = workbasketService.newWorkbasket("K99", "DOMAIN_A");
        workbasket.setName("workbasket");

        workbasket.setName("workbasket99");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket = workBasketServiceImpl.createWorkbasket(workbasket);
        Classification classification = classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
        classification = classificationServiceImpl.createClassification(classification);

        Task task = taskServiceImpl.newTask(workbasket.getId());
        task.setName("Unit Test Task");
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task.addAttachment(null);
        task = taskServiceImpl.createTask(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
        connection.commit();
        te.setConnection(null);
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test(expected = WorkbasketNotFoundException.class)
    public void createTaskShouldThrowWorkbasketNotFoundException()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException,
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException,
        InvalidArgumentException, WorkbasketAlreadyExistException, DomainNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Task test = this.generateDummyTask();
        ((WorkbasketSummaryImpl) (test.getWorkbasketSummary())).setId("2");
        taskServiceImpl.createTask(test);
    }

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test(expected = ClassificationNotFoundException.class)
    public void createManualTaskShouldThrowClassificationNotFoundException()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException,
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException,
        InvalidArgumentException, WorkbasketAlreadyExistException, DomainNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        Workbasket wb = workbasketService.newWorkbasket("WB NR.1", "DOMAIN_A");
        wb.setName("dummy-WB");
        wb.setType(WorkbasketType.PERSONAL);
        wb = workbasketService.createWorkbasket(wb);
        this.createWorkbasketWithSecurity(wb, CurrentUserContext.getUserid(), true, true,
            true, false);
        Classification classification = classificationService.newClassification(
            UUID.randomUUID().toString(), wb.getDomain(), "t1"); // not persisted,
        // not found.
        classification.setName("not persisted - so not found.");

        Task task = this.generateDummyTask();
        ((TaskImpl) task).setWorkbasketSummary(wb.asSummary());
        task.setClassificationKey(classification.getKey());
        taskServiceImpl.createTask(task);
    }

    @WithAccessId(userName = "Elena", groupNames = {"DummyGroup", "businessadmin"})
    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException, SystemException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("k1", "DOMAIN_A");
        workbasket.setName("workbasket");
        Classification classification = classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
        classificationService.createClassification(classification);
        workbasket.setId("1"); // set id manually for authorization tests
        workbasket.setType(WorkbasketType.GROUP);
        workbasket = (WorkbasketImpl) workbasketService.createWorkbasket(workbasket);

        WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermRead(true);
        accessItem.setPermOpen(true);
        workbasketService.createWorkbasketAccessItem(accessItem);

        Task task = taskServiceImpl.newTask(workbasket.getId());
        task.setName("Unit Test Task");
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = taskServiceImpl.createTask(task);

        List<TaskSummary> results = taskServiceImpl.createTaskQuery()
            .nameIn("bla", "test")
            .descriptionLike("test")
            .priorityIn(1, 2, 2)
            .stateIn(TaskState.CLAIMED)
            .workbasketKeyDomainIn(new KeyDomain("k1", "DOMAIN_A"))
            .ownerIn("test", "test2", "bla")
            .customAttributeLike("13", "test")
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

    @WithAccessId(userName = "Elena", groupNames = {"businessadmin"})
    @Test
    public void shouldTransferTaskToOtherWorkbasket()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, TaskNotFoundException, InterruptedException, TaskAlreadyExistException,
        SQLException, InvalidWorkbasketException, InvalidArgumentException, WorkbasketAlreadyExistException,
        DomainNotFoundException, InvalidStateException {
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
        wb = (WorkbasketImpl) workbasketService.newWorkbasket("sourceWbKey", "DOMAIN_A");
        wb.setName("Basic-Workbasket");
        wb.setDescription("Just used as base WB for Task here");
        wb.setOwner(user);
        wb.setType(WorkbasketType.PERSONAL);
        sourceWB = workbasketService.createWorkbasket(wb);

        createWorkbasketWithSecurity(wb, wb.getOwner(), false, false, false, false);
        createWorkbasketWithSecurity(sourceWB, sourceWB.getOwner(), true, true, true, true);

        // Destination Workbasket
        wb = (WorkbasketImpl) workbasketService.newWorkbasket("wb2Key", "DOMAIN_A");
        wb.setName("Desination-WorkBasket");
        wb.setDescription("Destination WB where Task should be transfered to");
        wb.setOwner(user);
        wb.setType(WorkbasketType.TOPIC);

        destinationWB = workbasketService.createWorkbasket(wb);
        createWorkbasketWithSecurity(destinationWB, destinationWB.getOwner(), false, true, true, true);

        // Classification required for Task
        classification = (ClassificationImpl) classificationService.newClassification("KEY", "DOMAIN_A", "TASK");
        classification.setCategory("EXTERNAL");
        classification.setName("Transfert-Task Classification");
        classificationService.createClassification(classification);

        // Task which should be transfered
        task = (TaskImpl) taskServiceImpl.newTask(sourceWB.getId());
        task.setName("Task Name");
        task.setDescription("Task used for transfer Test");
        task.setRead(true);
        task.setTransferred(false);
        task.setModified(null);
        task.setClassificationKey("KEY");
        task.setOwner(user);
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = (TaskImpl) taskServiceImpl.createTask(task);
        Thread.sleep(sleepTime);    // Sleep for modification-timestamp
        connection.commit();

        resultTask = taskServiceImpl.transfer(task.getId(), destinationWB.getId());
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
        InvalidStateException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        taskServiceImpl.transfer(UUID.randomUUID() + "_X", "1");
    }

    @WithAccessId(userName = "User", groupNames = {"businessadmin"})
    @Test
    public void shouldNotTransferByFailingSecurity() throws WorkbasketNotFoundException,
        ClassificationNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException, SQLException,
        TaskNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException,
        WorkbasketAlreadyExistException, DomainNotFoundException, InvalidStateException {
        final String user = "User";

        // Set up Security for this Test
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, true,
            TaskanaEngineConfigurationTest.getSchemaName());
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
        classificationService = taskanaEngine.getClassificationService();
        workbasketService = taskanaEngine.getWorkbasketService();

        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification(
            "KEY", "DOMAIN_A", "TASK");
        classification.setCategory("EXTERNAL");
        classification.setName("Transfert-Task Classification");
        classificationService.createClassification(classification);

        WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket("wbKey1", "DOMAIN_A");
        wb.setName("BASE WB");
        wb.setDescription("Normal base WB");
        wb.setOwner(user);
        wb.setType(WorkbasketType.GROUP);
        wb = (WorkbasketImpl) workbasketService.createWorkbasket(wb);
        createWorkbasketWithSecurity(wb, wb.getOwner(), true, true, true, true);

        WorkbasketImpl wbNoAppend = (WorkbasketImpl) workbasketService.newWorkbasket("keyNoAppend", "DOMAIN_B");
        wbNoAppend.setName("Test-Security-WorkBasket-APPEND");
        wbNoAppend.setDescription("Workbasket without permission APPEND on Task");
        wbNoAppend.setOwner(user);

        wbNoAppend.setType(WorkbasketType.CLEARANCE);
        wbNoAppend = (WorkbasketImpl) workbasketService.createWorkbasket(wbNoAppend);
        createWorkbasketWithSecurity(wbNoAppend, wbNoAppend.getOwner(), true, true, false, true);

        WorkbasketImpl wbNoTransfer = (WorkbasketImpl) workbasketService.newWorkbasket("keyNoTransfer", "DOMAIN_A");
        wbNoTransfer.setName("Test-Security-WorkBasket-TRANSFER");
        wbNoTransfer.setDescription("Workbasket without permission TRANSFER on Task");
        wbNoTransfer.setOwner(user);
        wbNoTransfer.setType(WorkbasketType.GROUP);
        wbNoTransfer = (WorkbasketImpl) workbasketService.createWorkbasket(wbNoTransfer);
        createWorkbasketWithSecurity(wbNoTransfer, wbNoTransfer.getOwner(), true, true, true, false);

        TaskImpl task = (TaskImpl) taskServiceImpl.newTask(wb.getId());
        task.setName("Task Name");
        task.setDescription("Task used for transfer Test");
        task.setOwner(user);
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = (TaskImpl) taskServiceImpl.createTask(task);

        // Check failing with missing APPEND
        try {
            task = (TaskImpl) taskServiceImpl.transfer(task.getId(), wbNoAppend.getId());
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
        task.getWorkbasketSummaryImpl().setId(wbNoTransfer.getId());
        task = (TaskImpl) taskServiceImpl.createTask(task);
        try {
            task = (TaskImpl) taskServiceImpl.transfer(task.getId(), wb.getId());
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
        throws ClassificationAlreadyExistException, InvalidWorkbasketException, NotAuthorizedException,
        WorkbasketAlreadyExistException, DomainNotFoundException, InvalidArgumentException {
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("wb", "DOMAIN_A");
        workbasket.setName("wb");
        workbasket.setId("1"); // set id manually for authorization tests
        workbasket.setType(WorkbasketType.GROUP);
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        Classification classification = classificationService.newClassification("TEST", "DOMAIN_A", "TASK");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask(workbasket.getId());
        task.setClassificationKey(classification.getKey());
        return task;
    }

    private void createWorkbasketWithSecurity(Workbasket wb, String accessId, boolean permOpen,
        boolean permRead, boolean permAppend, boolean permTransfer)
        throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem(wb.getId(), accessId);
        accessItem.setPermOpen(permOpen);
        accessItem.setPermRead(permRead);
        accessItem.setPermAppend(permAppend);
        accessItem.setPermTransfer(permTransfer);
        workbasketService.createWorkbasketAccessItem(accessItem);
    }

    @After
    public void cleanUp() throws SQLException {
        taskanaEngineImpl.setConnection(null);
    }
}
