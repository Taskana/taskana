package pro.taskana.impl.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
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
import pro.taskana.impl.ObjectReferenceQueryImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.ClassificationImpl;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskSummary;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketType;
import pro.taskana.security.CurrentUserContext;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Integration Test for TaskServiceImpl transactions with connection management mode AUTOCOMMIT.
 *
 * @author EH
 */
@RunWith(JAASRunner.class)
public class TaskServiceImplIntAutocommitTest {

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
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, false);

        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
        classificationService = taskanaEngine.getClassificationService();
        workbasketService = taskanaEngine.getWorkbasketService();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @Test
    public void testStart() throws FileNotFoundException, SQLException, TaskNotFoundException,
        WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException,
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setKey("workbasket");
        wb.setName("workbasket");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());

        task.setClassification(classification);

        task = taskServiceImpl.createTask(task);
        // skanaEngineImpl.getSqlSession().commit(); // needed so that the change is visible in the other session

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTaskById(task.getId());
        Assert.assertNotNull(resultTask);
    }

    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setName("sdf");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        wb.setKey("wb1k1");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());
        task.setClassification(classification);
        taskServiceImpl.createTask(task);
        taskServiceImpl.getTaskById(task.getId());

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTaskById(wb.getId());
    }

    @Test
    public void testCreateTaskInTaskanaWithDefaultDb()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException {
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(null, false, false);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        ((TaskanaEngineImpl) te).setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();

        Workbasket wb = workbasketService.newWorkbasket();
        wb.setKey("workbasket");
        wb.setName("workbasket");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        te.getWorkbasketService().createWorkbasket(wb);
        Classification classification = te.getClassificationService().newClassification();
        classification.setKey("TEST");
        te.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setKey("key");
        wb.setName("workbasket");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());
        task.setClassification(classification);
        taskServiceImpl.createTask(task);

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
            .workbasketKeyIn("asd", "asdasdasd")
            .owner("test", "test2", "bla")
            .customFields("test")
            .classification(classificationQuery)
            .objectReference(objectReferenceQuery)
            .list();

        Assert.assertEquals(0, results.size());
    }

    @Test
    public void shouldReturnTaskSummaryListWithValues() throws Exception {
        Workbasket dummyWorkbasket = workbasketService.newWorkbasket();
        dummyWorkbasket.setKey("Dummy-Key");
        dummyWorkbasket.setName("Dummy-Basket");
        dummyWorkbasket.setType(WorkbasketType.GROUP);
        dummyWorkbasket.setDomain("novatec");
        dummyWorkbasket = workbasketService.createWorkbasket(dummyWorkbasket);

        Classification dummyClassification = classificationService.newClassification();
        dummyClassification.setKey("1");
        dummyClassification.setName("Dummy-Classification");
        classificationService.createClassification(dummyClassification);

        TaskImpl dummyTask = (TaskImpl) taskServiceImpl.newTask();
        dummyTask.setId(null);
        dummyTask.setName("Dummy-Task");
        dummyTask.setClassification(dummyClassification);
        dummyTask.setWorkbasketKey(dummyWorkbasket.getKey());
        dummyTask = (TaskImpl) taskServiceImpl.createTask(dummyTask);

        List<TaskSummary> expectedTaskSumamries = new ArrayList<>();
        TaskSummary taskSummary = new TaskSummary();
        taskSummary.setTaskId(dummyTask.getId());
        taskSummary.setTaskName(dummyTask.getName());
        taskSummary.setWorkbasketKey(dummyWorkbasket.getKey());
        taskSummary.setWorkbasketName(dummyWorkbasket.getName());
        taskSummary.setClassificationKey(dummyClassification.getKey());
        taskSummary.setClassificationName(dummyClassification.getName());
        expectedTaskSumamries.add(taskSummary);

        List<TaskSummary> actualTaskSumamryResult = taskServiceImpl
            .getTaskSummariesByWorkbasketKey(dummyWorkbasket.getKey());

        assertThat(actualTaskSumamryResult.size(), equalTo(expectedTaskSumamries.size()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void shouldThrowWorkbasketNotFoundExceptionByNullParameter()
        throws WorkbasketNotFoundException, InvalidWorkbasketException {
        taskServiceImpl.getTaskSummariesByWorkbasketKey(null);
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void shouldThrowWorkbasketNotFoundExceptionByInvalidWorkbasketParameter()
        throws WorkbasketNotFoundException, InvalidWorkbasketException {
        WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setKey("key");
        wb.setName("wb");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        wb = (WorkbasketImpl) workbasketService.createWorkbasket(wb);
        taskServiceImpl.getTaskSummariesByWorkbasketKey("1");
    }

    @Test
    public void shouldTransferTaskToOtherWorkbasket()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, TaskNotFoundException, InterruptedException, TaskAlreadyExistException,
        InvalidWorkbasketException {
        Workbasket sourceWB;
        Workbasket destinationWB;
        WorkbasketImpl wb;
        ClassificationImpl classification;
        TaskImpl task;
        Task resultTask;
        final int sleepTime = 100;

        // Source Workbasket
        wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("Basic-Workbasket");
        wb.setDescription("Just used as base WB for Task here");
        wb.setKey("key1");
        wb.setDomain("domain");
        wb.setType(WorkbasketType.GROUP);
        wb.setOwner("The Tester ID");
        sourceWB = workbasketService.createWorkbasket(wb);

        // Destination Workbasket
        wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("Desination-WorkBasket");
        wb.setKey("k1");
        wb.setDomain("domain");
        wb.setType(WorkbasketType.CLEARANCE);
        wb.setDescription("Destination WB where Task should be transfered to");
        wb.setOwner("The Tester ID");
        destinationWB = workbasketService.createWorkbasket(wb);

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
        task = (TaskImpl) taskServiceImpl.createTask(task);
        Thread.sleep(sleepTime);    // Sleep for modification-timestamp

        resultTask = taskServiceImpl.transfer(task.getId(), destinationWB.getKey());
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
        throws WorkbasketNotFoundException, NotAuthorizedException, TaskNotFoundException, InvalidWorkbasketException {
        taskServiceImpl.transfer(UUID.randomUUID() + "_X", "1");
    }

    @WithAccessId(userName = "User")
    @Test
    public void shouldNotTransferByFailingSecurity() throws WorkbasketNotFoundException,
        ClassificationNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException, SQLException,
        TaskNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException {
        final String user = CurrentUserContext.getUserid();

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
        wb.setKey("k5");
        wb.setDomain("d1");
        wb.setType(WorkbasketType.TOPIC);
        wb = (WorkbasketImpl) workbasketService.createWorkbasket(wb);
        createWorkbasketWithSecurity(wb, wb.getOwner(), true, true, true, true);

        WorkbasketImpl wbNoAppend = (WorkbasketImpl) workbasketService.newWorkbasket();
        wbNoAppend.setName("Test-Security-WorkBasket-APPEND");
        wbNoAppend.setDescription("Workbasket without permission APPEND on Task");
        wbNoAppend.setOwner(user);
        wbNoAppend.setDomain("d2");
        wbNoAppend.setType(WorkbasketType.PERSONAL);
        wbNoAppend.setKey("key77");
        wbNoAppend = (WorkbasketImpl) workbasketService.createWorkbasket(wbNoAppend);
        createWorkbasketWithSecurity(wbNoAppend, wbNoAppend.getOwner(), true, true, false, true);

        WorkbasketImpl wbNoTransfer = (WorkbasketImpl) workbasketService.newWorkbasket();
        wbNoTransfer.setName("Test-Security-WorkBasket-TRANSFER");
        wbNoTransfer.setDescription("Workbasket without permission TRANSFER on Task");
        wbNoTransfer.setOwner(user);
        wbNoTransfer.setDomain("d3");
        wbNoTransfer.setType(WorkbasketType.CLEARANCE);
        wbNoTransfer.setKey("k99");
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

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
