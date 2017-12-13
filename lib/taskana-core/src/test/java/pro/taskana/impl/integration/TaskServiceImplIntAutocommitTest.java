package pro.taskana.impl.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
import pro.taskana.exceptions.NotAuthorizedException;
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
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskSummary;

/**
 * Integration Test for TaskServiceImpl transactions with connection management mode AUTOCOMMIT.
 *
 * @author EH
 */
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
        ClassificationAlreadyExistException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setName("workbasket");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketId(wb.getId());
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
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setName("sdf");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketId(wb.getId());
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
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setName("workbasket");
        wb = taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketId(wb.getId());
        task.setClassification(classification);
        task = taskServiceImpl.createTask(task);

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws SQLException, NotAuthorizedException,
        WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Workbasket wb = workbasketService.newWorkbasket();
        wb.setName("workbasket");
        taskanaEngine.getWorkbasketService().createWorkbasket(wb);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEngine.getClassificationService().createClassification(classification);

        Task task = taskServiceImpl.newTask();
        task.setName("Unit Test Task");
        task.setWorkbasketId(wb.getId());
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
            .workbasketId("asd", "asdasdasd")
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
        dummyWorkbasket.setName("Dummy-Basket");
        dummyWorkbasket = workbasketService.createWorkbasket(dummyWorkbasket);

        Classification dummyClassification = classificationService.newClassification();
        dummyClassification.setKey("1");
        dummyClassification.setName("Dummy-Classification");
        classificationService.createClassification(dummyClassification);

        TaskImpl dummyTask = (TaskImpl) taskServiceImpl.newTask();
        dummyTask.setId("1");
        dummyTask.setName("Dummy-Task");
        dummyTask.setClassification(dummyClassification);
        dummyTask.setWorkbasketId(dummyWorkbasket.getId());
        dummyTask = (TaskImpl) taskServiceImpl.createTask(dummyTask);

        List<TaskSummary> expectedTaskSumamries = new ArrayList<>();
        TaskSummary taskSummary = new TaskSummary();
        taskSummary.setTaskId(dummyTask.getId());
        taskSummary.setTaskName(dummyTask.getName());
        taskSummary.setWorkbasketId(dummyWorkbasket.getId());
        taskSummary.setWorkbasketName(dummyWorkbasket.getName());
        taskSummary.setClassificationKey(dummyClassification.getKey());
        taskSummary.setClassificationName(dummyClassification.getName());
        expectedTaskSumamries.add(taskSummary);

        List<TaskSummary> actualTaskSumamryResult = taskServiceImpl
            .getTaskSummariesByWorkbasketId(dummyWorkbasket.getId());

        assertThat(actualTaskSumamryResult.size(), equalTo(expectedTaskSumamries.size()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void shouldThrowWorkbasketNotFoundExceptionByNullParameter() throws WorkbasketNotFoundException {
        taskServiceImpl.getTaskSummariesByWorkbasketId(null);
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void shouldThrowWorkbasketNotFoundExceptionByInvalidWorkbasketParameter()
        throws WorkbasketNotFoundException {
        WorkbasketImpl wb = (WorkbasketImpl) workbasketService.newWorkbasket();
        wb.setName("wb");
        wb = (WorkbasketImpl) workbasketService.createWorkbasket(wb);
        wb.setId(wb.getId() + " - 1");
        taskServiceImpl.getTaskSummariesByWorkbasketId(wb.getId());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
