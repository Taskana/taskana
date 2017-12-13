package pro.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.ClassificationQueryImpl;
import pro.taskana.impl.ClassificationServiceImpl;
import pro.taskana.impl.ObjectReferenceQueryImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketServiceImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.ClassificationImpl;
import pro.taskana.model.ObjectReference;
import pro.taskana.model.TaskImpl;
import pro.taskana.model.TaskState;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Integration Test for TaskServiceImpl transactions with connection management mode EXPLICIT.
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
    private WorkbasketService workBasketService;

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
        workBasketService = taskanaEngine.getWorkbasketService();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = TaskNotFoundException.class)
    public void testStartTransactionFail()
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
//        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();

        generateSampleAccessItems();

        Workbasket workbasket = new Workbasket();
        workbasket.setName("workbasket");
        workbasket.setId("1"); // set id manually for authorization tests
        Classification classification = classificationService.newClassification();
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketId(workbasket.getId());
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
    public void testCreateTask() throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();
        Workbasket workbasket = new Workbasket();
        workbasket.setName("workbasket");
        workbasket.setId("1"); // set id manually for authorization tests
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);
        connection.commit();
        Classification classification = classificationService.newClassification();
        taskanaEngine.getClassificationService().createClassification(classification);
        connection.commit();
        Task task = taskServiceImpl.newTask();
        task.setWorkbasketId(workbasket.getId());
        task.setClassification(classification);
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
            throws FileNotFoundException, SQLException, TaskNotFoundException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        DataSource ds = TaskanaEngineConfiguration.createDefaultDataSource();
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(ds, false, false);
        TaskanaEngine te = taskanaEngineConfiguration.buildTaskanaEngine();
        Connection connection = ds.getConnection();
        te.setConnection(connection);
        TaskServiceImpl taskServiceImpl = (TaskServiceImpl) te.getTaskService();
        WorkbasketServiceImpl workbasketServiceImpl = (WorkbasketServiceImpl) te.getWorkbasketService();
        ClassificationServiceImpl classificationServiceImpl = (ClassificationServiceImpl) te.getClassificationService();

        Workbasket workbasket = new Workbasket();
        workbasket.setName("workbasket");
        Classification classification = classificationService.newClassification();
        workbasket.setName("workbasket99");
        workbasketServiceImpl.createWorkbasket(workbasket);
        classificationServiceImpl.createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketId(workbasket.getId());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
        connection.commit();
        te.setConnection(null);
    }

    @WithAccessId(userName = "Elena")
    @Test
    public void testCreateTaskWithPlannedAndName() throws SQLException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        Classification classification = classificationService.newClassification();
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

        Timestamp tomorrow = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

        TaskImpl test = this.generateDummyTask();
        test.setClassification(classification);
        test.setName("Name");
        test.setPrimaryObjRef(objectReference);
        test.setPlanned(tomorrow);
        Task resultTask1 = taskServiceImpl.createTask(test);

        Assert.assertNotEquals(resultTask1.getPlanned(), resultTask1.getCreated());
        Assert.assertNotNull(resultTask1.getDue());

        TaskImpl test2 = new TaskImpl();
        test2.setWorkbasketId(test.getWorkbasketId());
        test2.setClassification(classification);
        test2.setPrimaryObjRef(objectReference);
        test2.setDescription("desc");
        Task resultTask2 = taskServiceImpl.createTask(test2);

        Assert.assertEquals(resultTask2.getPlanned(), resultTask2.getCreated());
        Assert.assertTrue(resultTask2.getName().equals(classification.getName()));

        Assert.assertEquals(resultTask1.getClassification().getId(), resultTask2.getClassification().getId());
        Assert.assertTrue(resultTask1.getDue().after(resultTask2.getDue()));
        Assert.assertFalse(resultTask1.getName().equals(resultTask2.getName()));
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = WorkbasketNotFoundException.class)
    public void createTaskShouldThrowWorkbasketNotFoundException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException, ClassificationAlreadyExistException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        TaskImpl test = this.generateDummyTask();
        test.setWorkbasketId("2");
        taskServiceImpl.createTask(test);
    }

    @WithAccessId(userName = "Elena")
    @Test(expected = ClassificationNotFoundException.class)
    public void createManualTaskShouldThrowClassificationNotFoundException() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, SQLException, ClassificationAlreadyExistException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        TaskImpl test = this.generateDummyTask();
        test.setClassification(new ClassificationImpl());
        taskServiceImpl.createTask(test);
    }

    @WithAccessId(userName = "Elena", groupNames = {"DummyGroup"})
    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        generateSampleAccessItems();

        Workbasket workbasket = new Workbasket();
        workbasket.setName("workbasket");
        Classification classification = classificationService.newClassification();
        workbasket.setId("1"); // set id manually for authorization tests
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = new TaskImpl();
        task.setName("Unit Test Task");
        task.setWorkbasketId(workbasket.getId());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);

        TaskanaEngineImpl taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        ClassificationQuery classificationQuery = new ClassificationQueryImpl(taskanaEngineImpl)
                .parentClassification("pId1", "pId2").category("cat1", "cat2").type("oneType").name("1Name", "name2")
                .descriptionLike("my desc").priority(1, 2, 1).serviceLevel("me", "and", "you");

        ObjectReferenceQuery objectReferenceQuery = new ObjectReferenceQueryImpl(taskanaEngineImpl)
                .company("first comp", "sonstwo gmbh").system("sys").type("type1", "type2")
                .systemInstance("sysInst1", "sysInst2").value("val1", "val2", "val3");

        List<Task> results = taskServiceImpl.createTaskQuery().name("bla", "test").descriptionLike("test")
                .priority(1, 2, 2).state(TaskState.CLAIMED).workbasketId("1", "2")
                .owner("test", "test2", "bla").customFields("test").classification(classificationQuery)
                .objectReference(objectReferenceQuery).list();

        Assert.assertEquals(0, results.size());
        connection.commit();
    }

    private TaskImpl generateDummyTask() throws ClassificationAlreadyExistException {
        Workbasket workbasket = new Workbasket();
        workbasket.setName("wb");
        workbasket.setId("1"); // set id manually for authorization tests
        taskanaEngine.getWorkbasketService().createWorkbasket(workbasket);

        Classification classification = classificationService.newClassification();
        taskanaEngine.getClassificationService().createClassification(classification);

        TaskImpl task = new TaskImpl();
        task.setWorkbasketId(workbasket.getId());
        task.setClassification(classification);
        return task;
    }

    private void generateSampleAccessItems() {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketId("1");
        accessItem.setAccessId("Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermOpen(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        WorkbasketAccessItem accessItem2 = new WorkbasketAccessItem();
        accessItem2.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem2.setWorkbasketId("2");
        accessItem2.setAccessId("DummyGroup");
        accessItem2.setPermOpen(true);
        workBasketService.createWorkbasketAuthorization(accessItem2);
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
