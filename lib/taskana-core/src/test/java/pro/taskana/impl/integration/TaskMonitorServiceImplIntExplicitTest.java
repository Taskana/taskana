package pro.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
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
import pro.taskana.ClassificationService;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.JunitHelper;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.Report;
import pro.taskana.model.TaskState;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketType;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Integration Test for TaskMonitorServiceImpl transactions with connection management mode EXPLICIT.
 */
@RunWith(JAASRunner.class)
public class TaskMonitorServiceImplIntExplicitTest {

    private DataSource dataSource;
    private TaskServiceImpl taskServiceImpl;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private ClassificationService classificationService;
    private WorkbasketService workBasketService;
    private TaskMonitorService taskMonitorService;

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
    @Test
    public void testGetWorkbasketLevelReport() throws SQLException, ClassificationAlreadyExistException,
        WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException, TaskNotFoundException,
        TaskAlreadyExistException, InvalidWorkbasketException, InvalidArgumentException, InvalidOwnerException,
        InvalidStateException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        taskMonitorService = taskanaEngine.getTaskMonitorService();

        generateSampleAccessItems();

        WorkbasketImpl workbasket1 = (WorkbasketImpl) workBasketService.newWorkbasket();
        workbasket1.setName("wb1");
        workbasket1.setId("1");
        workbasket1.setKey("1");
        workbasket1.setDomain("novatec");
        workbasket1.setType(WorkbasketType.GROUP);
        workBasketService.createWorkbasket(workbasket1);

        WorkbasketImpl workbasket2 = (WorkbasketImpl) workBasketService.newWorkbasket();
        workbasket2.setName("wb2");
        workbasket2.setId("2");
        workbasket2.setKey("2");
        workbasket2.setDomain("novatec");
        workbasket2.setType(WorkbasketType.GROUP);
        workBasketService.createWorkbasket(workbasket2);

        Classification classification = classificationService.newClassification("novatec", "TEST", "type1");
        classificationService.createClassification(classification);

        TaskImpl task1 = (TaskImpl) taskServiceImpl.newTask();
        task1.setWorkbasketKey(workbasket1.getKey());
        task1.setClassificationKey(classification.getKey());
        task1.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task1 = (TaskImpl) taskServiceImpl.createTask(task1);
        connection.commit();

        TaskImpl task2 = (TaskImpl) taskServiceImpl.newTask();
        task2.setWorkbasketKey(workbasket2.getId());
        task2.setClassificationKey(classification.getKey());
        task2.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task2 = (TaskImpl) taskServiceImpl.createTask(task2);
        connection.commit();

        TaskImpl task3 = (TaskImpl) taskServiceImpl.newTask();
        task3.setWorkbasketKey(workbasket2.getId());
        task3.setClassificationKey(classification.getKey());
        task3.setPrimaryObjRef(JunitHelper.createDefaultObjRef());
        task3 = (TaskImpl) taskServiceImpl.createTask(task3);
        connection.commit();

        List<Workbasket> workbaskets = Arrays.asList(workbasket1, workbasket2);
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        Report report = taskMonitorService.getWorkbasketLevelReport(workbaskets, states);

        int countWorkbasket1 = report.getDetailLines().get(0).getTotalCount();
        int countWorkbasket2 = report.getDetailLines().get(1).getTotalCount();
        int totalCount = report.getSumLine().getTotalCount();

        Assert.assertNotNull(report);
        Assert.assertEquals(countWorkbasket1, 1);
        Assert.assertEquals(countWorkbasket2, 2);
        Assert.assertEquals(countWorkbasket1 + countWorkbasket2, totalCount);
        connection.commit();
    }

    private void generateSampleAccessItems() {
        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem.setWorkbasketKey("1");
        accessItem.setAccessId("Elena");
        accessItem.setPermAppend(true);
        accessItem.setPermRead(true);
        workBasketService.createWorkbasketAuthorization(accessItem);

        WorkbasketAccessItem accessItem2 = new WorkbasketAccessItem();
        accessItem2.setId(IdGenerator.generateWithPrefix("WAI"));
        accessItem2.setWorkbasketKey("2");
        accessItem2.setAccessId("Elena");
        accessItem2.setPermAppend(true);
        accessItem2.setPermRead(true);
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
