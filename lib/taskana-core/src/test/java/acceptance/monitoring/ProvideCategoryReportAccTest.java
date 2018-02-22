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
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.Report;
import pro.taskana.impl.ReportLineItem;
import pro.taskana.impl.ReportLineItemDefinition;
import pro.taskana.impl.TaskState;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
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
        List<String> domains = Arrays.asList("DOMAIN_A", "DOMAIN_B", "DOMAIN_C");
        Report report = taskMonitorService.getCategoryReport(workbaskets, states, categories, domains);

        assertNotNull(report);
        assertEquals(33, report.getReportLines().get(categories.get(0)).getTotalNumberOfTasks());
        assertEquals(7, report.getReportLines().get(categories.get(1)).getTotalNumberOfTasks());
        assertEquals(10, report.getReportLines().get(categories.get(2)).getTotalNumberOfTasks());
        assertEquals(0, report.getReportLines().get(categories.get(0)).getLineItems().size());
        assertEquals(0, report.getReportLines().get(categories.get(1)).getLineItems().size());
        assertEquals(0, report.getReportLines().get(categories.get(2)).getLineItems().size());
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
        List<String> domains = Arrays.asList("DOMAIN_A", "DOMAIN_B", "DOMAIN_C");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getCategoryReport(workbaskets, states, categories, domains,
            reportLineItemDefinitions);

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

        assertEquals(33, report.getReportLines().get(categories.get(0)).getTotalNumberOfTasks());
        assertEquals(7, report.getReportLines().get(categories.get(1)).getTotalNumberOfTasks());
        assertEquals(10, report.getReportLines().get(categories.get(2)).getTotalNumberOfTasks());

        assertEquals(10, report.getSumLine().getLineItems().get(0).getNumberOfTasks());
        assertEquals(9, report.getSumLine().getLineItems().get(1).getNumberOfTasks());
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
    public void testEachItemOfCategoryReportWithCategoryFilter()
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
        List<String> domains = Arrays.asList("DOMAIN_A", "DOMAIN_B", "DOMAIN_C");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getCategoryReport(workbaskets, states, categories, domains,
            reportLineItemDefinitions);

        List<ReportLineItem> line1 = report.getReportLines().get("AUTOMATIC").getLineItems();
        assertEquals(2, line1.get(0).getNumberOfTasks());
        assertEquals(1, line1.get(1).getNumberOfTasks());
        assertEquals(0, line1.get(2).getNumberOfTasks());
        assertEquals(1, line1.get(3).getNumberOfTasks());
        assertEquals(3, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getReportLines().get("MANUAL").getLineItems();
        assertEquals(2, line2.get(0).getNumberOfTasks());
        assertEquals(2, line2.get(1).getNumberOfTasks());
        assertEquals(2, line2.get(2).getNumberOfTasks());
        assertEquals(0, line2.get(3).getNumberOfTasks());
        assertEquals(4, line2.get(4).getNumberOfTasks());

        assertEquals(2, report.getReportLines().size());
    }

    @WithAccessId(userName = "monitor_user_1")
    @Test
    public void testEachItemOfCategoryReportWithDomainFilter()
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        List<String> categories = Arrays.asList("EXTERN", "AUTOMATIC", "MANUAL");
        List<String> domains = Arrays.asList("DOMAIN_A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getCategoryReport(workbaskets, states, categories, domains,
            reportLineItemDefinitions);

        List<ReportLineItem> line1 = report.getReportLines().get("EXTERN").getLineItems();
        assertEquals(8, line1.get(0).getNumberOfTasks());
        assertEquals(4, line1.get(1).getNumberOfTasks());
        assertEquals(2, line1.get(2).getNumberOfTasks());
        assertEquals(4, line1.get(3).getNumberOfTasks());
        assertEquals(0, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getReportLines().get("AUTOMATIC").getLineItems();
        assertEquals(1, line2.get(0).getNumberOfTasks());
        assertEquals(0, line2.get(1).getNumberOfTasks());
        assertEquals(0, line2.get(2).getNumberOfTasks());
        assertEquals(1, line2.get(3).getNumberOfTasks());
        assertEquals(1, line2.get(4).getNumberOfTasks());

        List<ReportLineItem> line3 = report.getReportLines().get("MANUAL").getLineItems();
        assertEquals(2, line3.get(0).getNumberOfTasks());
        assertEquals(0, line3.get(1).getNumberOfTasks());
        assertEquals(0, line3.get(2).getNumberOfTasks());
        assertEquals(0, line3.get(3).getNumberOfTasks());
        assertEquals(3, line3.get(4).getNumberOfTasks());
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

    private List<ReportLineItemDefinition> getShortListOfReportLineItemDefinitions() {
        List<ReportLineItemDefinition> reportLineItemDefinitions = new ArrayList<>();
        reportLineItemDefinitions.add(new ReportLineItemDefinition(Integer.MIN_VALUE, -6));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(-5, -1));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(0));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(1, 5));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(6, Integer.MAX_VALUE));
        return reportLineItemDefinitions;
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
