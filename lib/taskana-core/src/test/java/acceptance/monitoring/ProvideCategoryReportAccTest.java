package acceptance.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.database.TestDataGenerator;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.model.Report;
import pro.taskana.model.ReportLineItemDefinition;
import pro.taskana.model.TaskState;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "category report" scenarios.
 */
@RunWith(JAASRunner.class)
public class ProvideCategoryReportAccTest {

    protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected static TaskanaEngine taskanaEngine;

    @BeforeClass
    public static void setupTest() throws Exception {
        resetDb();
    }

    public static void resetDb() throws SQLException, IOException {
        DataSource dataSource = TaskanaEngineConfigurationTest.getDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, true);
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        ((TaskanaEngineImpl) taskanaEngine).setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        cleaner.clearDb(dataSource, false);
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        testDataGenerator.generateMonitoringTestData(dataSource);
    }

    @WithAccessId(userName = "monitor_user_1")
    @Test
    public void testGetTotalNumbersOfTasksOfCategoryReport()
        throws WorkbasketNotFoundException, NotAuthorizedException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        List<String> categories = Arrays.asList("EXTERN", "AUTOMATIC", "MANUAL");
        Report report = taskMonitorService.getCategoryReport(workbaskets, states);

        assertNotNull(report);
        assertEquals(33, report.getDetailLines().get(categories.get(0)).getTotalNumberOfTasks());
        assertEquals(7, report.getDetailLines().get(categories.get(1)).getTotalNumberOfTasks());
        assertEquals(10, report.getDetailLines().get(categories.get(2)).getTotalNumberOfTasks());
        assertEquals(0, report.getDetailLines().get(categories.get(0)).getLineItems().size());
        assertEquals(0, report.getDetailLines().get(categories.get(1)).getLineItems().size());
        assertEquals(0, report.getDetailLines().get(categories.get(2)).getLineItems().size());
        assertEquals(50, report.getSumLine().getTotalNumberOfTasks());

    }

    @WithAccessId(userName = "monitor_user_1")
    @Test
    public void testGetCategoryReportWithReportLineItemDefinitions()
        throws WorkbasketNotFoundException, NotAuthorizedException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        List<String> categories = Arrays.asList("EXTERN", "AUTOMATIC", "MANUAL");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getCategoryReport(workbaskets, states, reportLineItemDefinitions);

        int sumLineCount = report.getSumLine().getLineItems().get(0).getNumberOfTasks()
            + report.getSumLine().getLineItems().get(1).getNumberOfTasks()
            + report.getSumLine().getLineItems().get(2).getNumberOfTasks()
            + report.getSumLine().getLineItems().get(3).getNumberOfTasks()
            + report.getSumLine().getLineItems().get(4).getNumberOfTasks()
            + report.getSumLine().getLineItems().get(5).getNumberOfTasks()
            + report.getSumLine().getLineItems().get(6).getNumberOfTasks()
            + report.getSumLine().getLineItems().get(7).getNumberOfTasks()
            + report.getSumLine().getLineItems().get(8).getNumberOfTasks();

        assertNotNull(report);

        assertEquals(33, report.getDetailLines().get(categories.get(0)).getTotalNumberOfTasks());
        assertEquals(7, report.getDetailLines().get(categories.get(1)).getTotalNumberOfTasks());
        assertEquals(10, report.getDetailLines().get(categories.get(2)).getTotalNumberOfTasks());

        assertEquals(11, report.getSumLine().getLineItems().get(0).getNumberOfTasks());
        assertEquals(8, report.getSumLine().getLineItems().get(1).getNumberOfTasks());
        assertEquals(11, report.getSumLine().getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, report.getSumLine().getLineItems().get(3).getNumberOfTasks());
        assertEquals(4, report.getSumLine().getLineItems().get(4).getNumberOfTasks());
        assertEquals(0, report.getSumLine().getLineItems().get(5).getNumberOfTasks());
        assertEquals(7, report.getSumLine().getLineItems().get(6).getNumberOfTasks());
        assertEquals(4, report.getSumLine().getLineItems().get(7).getNumberOfTasks());
        assertEquals(5, report.getSumLine().getLineItems().get(8).getNumberOfTasks());
        assertEquals(50, report.getSumLine().getTotalNumberOfTasks());
        assertEquals(50, sumLineCount);

    }

    @WithAccessId(userName = "monitor_user_1")
    @Test
    public void testGetCategoryReportIfWorkbasketContainsNoTask()
        throws WorkbasketNotFoundException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        List<Workbasket> workbaskets = new ArrayList<>();
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService
            .getWorkbasket("WBI:000000000000000000000000000000000004");
        workbaskets.add(workbasket);
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        Report report = taskMonitorService.getCategoryReport(workbaskets, states);

        assertNotNull(report);
        assertEquals(0, report.getDetailLines().size());
        assertEquals(0, report.getSumLine().getTotalNumberOfTasks());
    }

    private List<Workbasket> getListOfWorkbaskets() throws WorkbasketNotFoundException, NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        WorkbasketImpl workbasket1 = (WorkbasketImpl) workbasketService
            .getWorkbasket("WBI:000000000000000000000000000000000001");
        WorkbasketImpl workbasket2 = (WorkbasketImpl) workbasketService
            .getWorkbasket("WBI:000000000000000000000000000000000002");
        WorkbasketImpl workbasket3 = (WorkbasketImpl) workbasketService
            .getWorkbasket("WBI:000000000000000000000000000000000003");
        WorkbasketImpl workbasket4 = (WorkbasketImpl) workbasketService
            .getWorkbasket("WBI:000000000000000000000000000000000004");

        return Arrays.asList(workbasket1, workbasket2, workbasket3, workbasket4);
    }

    private List<ReportLineItemDefinition> getListOfReportLineItemDefinitions() {
        List<ReportLineItemDefinition> reportLineItemDefinitions = new ArrayList<>();
        reportLineItemDefinitions.add(new ReportLineItemDefinition(Integer.MIN_VALUE, -11));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(-10, -6));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(-5, -2));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(-1));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(0));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(1));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(2, 5));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(6, 10));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(11, Integer.MAX_VALUE));
        return reportLineItemDefinitions;
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
