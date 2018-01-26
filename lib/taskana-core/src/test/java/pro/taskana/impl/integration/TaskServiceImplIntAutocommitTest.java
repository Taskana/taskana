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
import pro.taskana.impl.JunitHelper;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskSummaryImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
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
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException,
        InvalidArgumentException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setKey("workbasket");
        wb.setName("workbasket");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification("novatec", "TEST", "t1");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());

        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = taskServiceImpl.createTask(task);
        // skanaEngineImpl.getSqlSession().commit(); // needed so that the change is visible in the other session

        TaskanaEngine te2 = taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        Task resultTask = taskServiceImpl2.getTask(task.getId());
        Assert.assertNotNull(resultTask);
    }

    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setName("sdf");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        wb.setKey("wb1k1");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);

        Classification classification = classificationService.newClassification("novatec", "TEST", "t1");
        classification = taskanaEngine.getClassificationService()
            .createClassification(classification);
        classification = taskanaEngine.getClassificationService().getClassification(
            classification.getKey(),
            classification.getDomain());

        TaskImpl task = (TaskImpl) taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        taskServiceImpl.createTask(task);
        taskServiceImpl.getTask(task.getId());

        TaskanaEngineImpl te2 = (TaskanaEngineImpl) taskanaEngineConfiguration.buildTaskanaEngine();
        TaskServiceImpl taskServiceImpl2 = (TaskServiceImpl) te2.getTaskService();
        taskServiceImpl2.getTask(wb.getId());
    }

    @Test
    public void testCreateTaskInTaskanaWithDefaultDb()
        throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
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
        Classification classification = te.getClassificationService().newClassification("novatec", "TEST", "t1");
        te.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task = taskServiceImpl.createTask(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException, SystemException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setKey("key");
        wb.setName("workbasket");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification("novatec", "TEST", "t1");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        taskServiceImpl.createTask(task);

        List<TaskSummary> results = taskServiceImpl.createTaskQuery()
            .nameIn("bla", "test")
            .descriptionLike("test")
            .priorityIn(1, 2, 2)
            .stateIn(TaskState.CLAIMED)
            .workbasketKeyIn("asd", "asdasdasd")
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
    }

    @Test
    public void shouldReturnTaskSummaryListWithValues() throws Exception {
        Workbasket dummyWorkbasket = workbasketService.newWorkbasket();
        dummyWorkbasket.setKey("Dummy-Key");
        dummyWorkbasket.setName("Dummy-Basket");
        dummyWorkbasket.setType(WorkbasketType.GROUP);
        dummyWorkbasket.setDomain("novatec");
        dummyWorkbasket = workbasketService.createWorkbasket(dummyWorkbasket);

        Classification dummyClassification = classificationService.newClassification("novatec", "1", "t1");
        dummyClassification.setName("Dummy-Classification");
        classificationService.createClassification(dummyClassification);

        TaskImpl dummyTask = (TaskImpl) taskServiceImpl.newTask();
        dummyTask.setId(null);
        dummyTask.setName("Dummy-Task");
        dummyTask.setClassificationKey(dummyClassification.getKey());
        dummyTask.setWorkbasketKey(dummyWorkbasket.getKey());
        dummyTask.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        dummyTask = (TaskImpl) taskServiceImpl.createTask(dummyTask);

        List<TaskSummaryImpl> expectedTaskSumamries = new ArrayList<>();
        TaskSummaryImpl taskSummary = new TaskSummaryImpl();
        taskSummary.setTaskId(dummyTask.getId());
        taskSummary.setName(dummyTask.getName());
        expectedTaskSumamries.add(taskSummary);

        List<TaskSummary> actualTaskSumamryResult = taskServiceImpl
            .getTaskSummariesByWorkbasketKey(dummyWorkbasket.getKey());

        assertThat(actualTaskSumamryResult.size(), equalTo(expectedTaskSumamries.size()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void shouldThrowWorkbasketNotFoundExceptionByNullParameter()
        throws WorkbasketNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        taskServiceImpl.getTaskSummariesByWorkbasketKey(null);
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void shouldThrowWorkbasketNotFoundExceptionByInvalidWorkbasketParameter()
        throws WorkbasketNotFoundException, InvalidWorkbasketException, NotAuthorizedException {
        WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setKey("key");
        wb.setName("wb");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        workbasketService.createWorkbasket(wb);
        taskServiceImpl.getTaskSummariesByWorkbasketKey("1");
        wb = (WorkbasketImpl) workbasketService.createWorkbasket(wb);
        workbasketService.createWorkbasket(wb);
        taskServiceImpl.getTaskSummariesByWorkbasketKey("1");
    }

    @Test
    public void shouldTransferTaskToOtherWorkbasket()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        ClassificationAlreadyExistException, TaskNotFoundException, InterruptedException, TaskAlreadyExistException,
        InvalidWorkbasketException, InvalidArgumentException {
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
        task.setClassificationKey(classification.getKey());
        task.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
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
        throws WorkbasketNotFoundException, NotAuthorizedException, TaskNotFoundException, InvalidWorkbasketException,
        ClassificationNotFoundException {
        taskServiceImpl.transfer(UUID.randomUUID() + "_X", "1");
    }

    @WithAccessId(userName = "User")
    @Test
    public void shouldNotTransferByFailingSecurity() throws WorkbasketNotFoundException,
        ClassificationNotFoundException, NotAuthorizedException, ClassificationAlreadyExistException, SQLException,
        TaskNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException {
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

        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification("test-domain",
            "KEY", "t1");
        classification.setCategory("Test Classification");
        classification.setName("Transfert-Task Classification");
        classificationService.createClassification(classification);

        WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("BASE WB");
        wb.setDescription("Normal base WB");
        wb.setOwner(user);
        wb.setKey("k5");
        wb.setDomain("test-domain");
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
        wbNoTransfer.setDomain("test-domain");
        wbNoTransfer.setType(WorkbasketType.CLEARANCE);
        wbNoTransfer.setKey("k99");
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

    @Test
    public void testWithPrimaryObjectRef() throws FileNotFoundException, SQLException, TaskNotFoundException,
        WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException,
        ClassificationAlreadyExistException, TaskAlreadyExistException, InvalidWorkbasketException,
        InvalidArgumentException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setKey("workbasket");
        wb.setName("workbasket");
        wb.setType(WorkbasketType.GROUP);
        wb.setDomain("novatec");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification("novatec", "TEST", "t1");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketKey(wb.getKey());

        task.setClassificationKey(classification.getKey());

        ObjectReference objRef = new ObjectReference();
        objRef.setCompany("novatec");
        objRef.setSystem("linux");
        objRef.setSystemInstance("inst1");
        objRef.setType("fast");
        objRef.setValue("4711");
        task.setPrimaryObjRef(objRef);

        task = taskServiceImpl.createTask(task);

        Task task2 = taskServiceImpl.getTask(task.getId());
        // skanaEngineImpl.getSqlSession().commit(); // needed so that the change is visible in the other session

        Assert.assertNotNull(task2);
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
