package pro.taskana.impl.integration;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.*;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.model.Classification;
import pro.taskana.model.Task;
import pro.taskana.model.TaskSummary;
import pro.taskana.model.Workbasket;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Testing {@link SummaryServiceImpl} with real DB-Connection and
 * real results.
 */
public class SummaryServiceImplIntAutoCommitTest {

    private DataSource dataSource;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private WorkbasketServiceImpl workbasketServiceImpl;
    private TaskServiceImpl taskServiceImpl;
    private ClassificationServiceImpl classificationServiceImpl;
    private SummaryServiceImpl summaryServiceImp;
    private Task dummyTask;
    private Classification dummyClassification;
    private Workbasket dummyWorkbasket;

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

        workbasketServiceImpl = (WorkbasketServiceImpl) taskanaEngine.getWorkbasketService();
        taskServiceImpl = (TaskServiceImpl) taskanaEngine.getTaskService();
        classificationServiceImpl = (ClassificationServiceImpl) taskanaEngine.getClassificationService();
        summaryServiceImp = (SummaryServiceImpl) taskanaEngine.getSummaryService();

        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @Test
    public void shouldReturnTaskSummaryListWithValues() throws Exception {

        generateDummyData();

        List<TaskSummary> expectedTaskSumamries = new ArrayList<>();
        TaskSummary taskSummary = new TaskSummary();
        taskSummary.setTaskId(dummyTask.getId());
        taskSummary.setTaskName(dummyTask.getName());
        taskSummary.setWorkbasketId(dummyWorkbasket.getId());
        taskSummary.setWorkbasketName(dummyWorkbasket.getName());
        taskSummary.setClassificationId(dummyClassification.getId());
        taskSummary.setClassificationName(dummyClassification.getName());
        expectedTaskSumamries.add(taskSummary);

        List<TaskSummary> actualTaskSumamryResult = summaryServiceImp.getTaskSummariesByWorkbasketId(dummyWorkbasket.getId());

        assertThat(actualTaskSumamryResult, equalTo(expectedTaskSumamries));
        assertThat(actualTaskSumamryResult.size(), equalTo(expectedTaskSumamries.size()));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void shouldThrowWorkbasketNotFoundExceptionByNullParameter() throws WorkbasketNotFoundException {
        List<TaskSummary> actualTaskSumamryResult = summaryServiceImp.getTaskSummariesByWorkbasketId(null);
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void shouldThrowWorkbasketNotFoundExceptionByInvalidWorkbasketParameter() throws WorkbasketNotFoundException {
        Workbasket wb = new Workbasket();
        wb.setName("wb");
        workbasketServiceImpl.createWorkbasket(wb);
        List<TaskSummary> actualTaskSumamryResult = summaryServiceImp.getTaskSummariesByWorkbasketId("1");
    }


    private void generateDummyData() throws Exception {
        dummyWorkbasket = new Workbasket();
        dummyWorkbasket.setId("1");
        dummyWorkbasket.setName("Dummy-Basket");
        dummyWorkbasket = workbasketServiceImpl.createWorkbasket(dummyWorkbasket);

        dummyClassification = new Classification();
        dummyClassification.setId("1");
        dummyClassification.setName("Dummy-Classification");
        classificationServiceImpl.addClassification(dummyClassification);

        dummyTask = new Task();
        dummyTask.setId("1");
        dummyTask.setName("Dummy-Task");
        dummyTask.setClassification(dummyClassification);
        dummyTask.setWorkbasketId(dummyWorkbasket.getId());
        dummyTask = taskServiceImpl.create(dummyTask);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
