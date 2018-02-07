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

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
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
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.model.Report;
import pro.taskana.model.ReportLineItem;
import pro.taskana.model.ReportLineItemDefinition;
import pro.taskana.model.TaskState;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "classification report" scenarios.
 */
@RunWith(JAASRunner.class)
public class ProvideClassificationReportAccTest {

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
    public void testGetTotalNumbersOfTasksOfClassificationReport()
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<Classification> classifications = getListOfClassifications();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);

        Report report = taskMonitorService.getClassificationReport(workbaskets, states);

        assertNotNull(report);
        assertEquals(10, report.getDetailLines().get(classifications.get(0).getKey()).getTotalNumberOfTasks());
        assertEquals(10, report.getDetailLines().get(classifications.get(1).getKey()).getTotalNumberOfTasks());
        assertEquals(7, report.getDetailLines().get(classifications.get(2).getKey()).getTotalNumberOfTasks());
        assertEquals(10, report.getDetailLines().get(classifications.get(3).getKey()).getTotalNumberOfTasks());
        assertEquals(13, report.getDetailLines().get(classifications.get(4).getKey()).getTotalNumberOfTasks());
        assertEquals(0, report.getDetailLines().get(classifications.get(0).getKey()).getLineItems().size());
        assertEquals(0, report.getDetailLines().get(classifications.get(1).getKey()).getLineItems().size());
        assertEquals(0, report.getDetailLines().get(classifications.get(2).getKey()).getLineItems().size());
        assertEquals(0, report.getDetailLines().get(classifications.get(3).getKey()).getLineItems().size());
        assertEquals(0, report.getDetailLines().get(classifications.get(4).getKey()).getLineItems().size());
        assertEquals(50, report.getSumLine().getTotalNumberOfTasks());
    }

    @WithAccessId(userName = "monitor_user_1")
    @Test
    public void testGetClassificationReportWithReportLineItemDefinitions()
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        List<Classification> classifications = getListOfClassifications();
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getClassificationReport(workbaskets, states, reportLineItemDefinitions);

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

        assertEquals(10, report.getDetailLines().get(classifications.get(0).getKey()).getTotalNumberOfTasks());
        assertEquals(10, report.getDetailLines().get(classifications.get(1).getKey()).getTotalNumberOfTasks());
        assertEquals(7, report.getDetailLines().get(classifications.get(2).getKey()).getTotalNumberOfTasks());
        assertEquals(10, report.getDetailLines().get(classifications.get(3).getKey()).getTotalNumberOfTasks());
        assertEquals(13, report.getDetailLines().get(classifications.get(4).getKey()).getTotalNumberOfTasks());

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
    public void testEachItemOfClassificationReport()
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        List<Classification> classifications = getListOfClassifications();
        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getClassificationReport(workbaskets, states, reportLineItemDefinitions);

        List<ReportLineItem> line1 = report.getDetailLines().get(classifications.get(0).getKey()).getLineItems();
        assertEquals(7, line1.get(0).getNumberOfTasks());
        assertEquals(2, line1.get(1).getNumberOfTasks());
        assertEquals(1, line1.get(2).getNumberOfTasks());
        assertEquals(0, line1.get(3).getNumberOfTasks());
        assertEquals(0, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getDetailLines().get(classifications.get(1).getKey()).getLineItems();
        assertEquals(5, line2.get(0).getNumberOfTasks());
        assertEquals(3, line2.get(1).getNumberOfTasks());
        assertEquals(1, line2.get(2).getNumberOfTasks());
        assertEquals(1, line2.get(3).getNumberOfTasks());
        assertEquals(0, line2.get(4).getNumberOfTasks());

        List<ReportLineItem> line3 = report.getDetailLines().get(classifications.get(2).getKey()).getLineItems();
        assertEquals(2, line3.get(0).getNumberOfTasks());
        assertEquals(1, line3.get(1).getNumberOfTasks());
        assertEquals(0, line3.get(2).getNumberOfTasks());
        assertEquals(1, line3.get(3).getNumberOfTasks());
        assertEquals(3, line3.get(4).getNumberOfTasks());

        List<ReportLineItem> line4 = report.getDetailLines().get(classifications.get(3).getKey()).getLineItems();
        assertEquals(2, line4.get(0).getNumberOfTasks());
        assertEquals(2, line4.get(1).getNumberOfTasks());
        assertEquals(2, line4.get(2).getNumberOfTasks());
        assertEquals(0, line4.get(3).getNumberOfTasks());
        assertEquals(4, line4.get(4).getNumberOfTasks());

        List<ReportLineItem> line5 = report.getDetailLines().get(classifications.get(4).getKey()).getLineItems();
        assertEquals(3, line5.get(0).getNumberOfTasks());
        assertEquals(3, line5.get(1).getNumberOfTasks());
        assertEquals(0, line5.get(2).getNumberOfTasks());
        assertEquals(5, line5.get(3).getNumberOfTasks());
        assertEquals(2, line5.get(4).getNumberOfTasks());
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

    private List<Classification> getListOfClassifications() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        ClassificationImpl c1 = (ClassificationImpl) classificationService.getClassification("L10000", "DOMAIN_A");
        ClassificationImpl c2 = (ClassificationImpl) classificationService.getClassification("L20000", "DOMAIN_A");
        ClassificationImpl c3 = (ClassificationImpl) classificationService.getClassification("L30000", "DOMAIN_A");
        ClassificationImpl c4 = (ClassificationImpl) classificationService.getClassification("L40000", "DOMAIN_A");
        ClassificationImpl c5 = (ClassificationImpl) classificationService.getClassification("L50000", "DOMAIN_A");
        return Arrays.asList(c1, c2, c3, c4, c5);
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
