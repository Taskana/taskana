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
import pro.taskana.impl.DetailedClassificationReport;
import pro.taskana.impl.DetailedReportLine;
import pro.taskana.impl.ReportLine;
import pro.taskana.impl.ReportLineItemDefinition;
import pro.taskana.impl.TaskState;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "classification report" scenarios.
 */
@RunWith(JAASRunner.class)
public class ProvideDetailedClassificationReportAccTest {

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
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);

        DetailedClassificationReport report = taskMonitorService.getDetailedClassificationReport(workbaskets, states);

        assertNotNull(report);

        DetailedReportLine line1 = (DetailedReportLine) report.getReportLines().get("L10000");
        assertEquals(10, line1.getTotalNumberOfTasks());
        assertEquals(3, line1.getDetailLines().get("L11000").getTotalNumberOfTasks());
        assertEquals(7, line1.getDetailLines().get("N/A").getTotalNumberOfTasks());
        assertEquals(0, line1.getLineItems().size());
        assertEquals(2, line1.getDetailLines().size());

        DetailedReportLine line2 = (DetailedReportLine) report.getReportLines().get("L20000");
        assertEquals(10, line2.getTotalNumberOfTasks());
        assertEquals(4, line2.getDetailLines().get("L22000").getTotalNumberOfTasks());
        assertEquals(6, line2.getDetailLines().get("N/A").getTotalNumberOfTasks());
        assertEquals(0, line2.getLineItems().size());
        assertEquals(2, line2.getDetailLines().size());

        DetailedReportLine line3 = (DetailedReportLine) report.getReportLines().get("L30000");
        assertEquals(7, line3.getTotalNumberOfTasks());
        assertEquals(3, line3.getDetailLines().get("L33000").getTotalNumberOfTasks());
        assertEquals(1, line3.getDetailLines().get("L99000").getTotalNumberOfTasks());
        assertEquals(3, line3.getDetailLines().get("N/A").getTotalNumberOfTasks());
        assertEquals(0, line3.getLineItems().size());
        assertEquals(3, line3.getDetailLines().size());

        DetailedReportLine line4 = (DetailedReportLine) report.getReportLines().get("L40000");
        assertEquals(10, line4.getTotalNumberOfTasks());
        assertEquals(10, line4.getDetailLines().get("N/A").getTotalNumberOfTasks());
        assertEquals(0, line4.getLineItems().size());
        assertEquals(1, line4.getDetailLines().size());

        DetailedReportLine line5 = (DetailedReportLine) report.getReportLines().get("L50000");
        assertEquals(13, line5.getTotalNumberOfTasks());
        assertEquals(13, line5.getDetailLines().get("N/A").getTotalNumberOfTasks());
        assertEquals(0, line5.getLineItems().size());
        assertEquals(1, line5.getDetailLines().size());

        assertEquals(50, report.getSumLine().getTotalNumberOfTasks());
    }

    @WithAccessId(userName = "monitor_user_1")
    @Test
    public void testGetClassificationReportWithReportLineItemDefinitions()
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        DetailedClassificationReport report = taskMonitorService.getDetailedClassificationReport(workbaskets, states,
            reportLineItemDefinitions);

        assertNotNull(report);

        assertEquals(10, report.getReportLines().get("L10000").getTotalNumberOfTasks());
        assertEquals(10, report.getReportLines().get("L20000").getTotalNumberOfTasks());
        assertEquals(7, report.getReportLines().get("L30000").getTotalNumberOfTasks());
        assertEquals(10, report.getReportLines().get("L40000").getTotalNumberOfTasks());
        assertEquals(13, report.getReportLines().get("L50000").getTotalNumberOfTasks());

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
    }

    @WithAccessId(userName = "monitor_user_1")
    @Test
    public void testEachItemOfClassificationReport()
        throws WorkbasketNotFoundException, NotAuthorizedException, ClassificationNotFoundException {

        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<Workbasket> workbaskets = getListOfWorkbaskets();
        List<TaskState> states = Arrays.asList(TaskState.READY, TaskState.CLAIMED);

        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        DetailedClassificationReport report = taskMonitorService.getDetailedClassificationReport(workbaskets, states,
            reportLineItemDefinitions);

        DetailedReportLine line1 = (DetailedReportLine) report.getReportLines().get("L10000");
        assertEquals(7, line1.getLineItems().get(0).getNumberOfTasks());
        assertEquals(2, line1.getLineItems().get(1).getNumberOfTasks());
        assertEquals(1, line1.getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, line1.getLineItems().get(3).getNumberOfTasks());
        assertEquals(0, line1.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLine1 = line1.getDetailLines().get("L11000");
        assertEquals(2, detailedLine1.getLineItems().get(0).getNumberOfTasks());
        assertEquals(0, detailedLine1.getLineItems().get(1).getNumberOfTasks());
        assertEquals(1, detailedLine1.getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, detailedLine1.getLineItems().get(3).getNumberOfTasks());
        assertEquals(0, detailedLine1.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLineNoAttachment1 = line1.getDetailLines().get("N/A");
        assertEquals(5, detailedLineNoAttachment1.getLineItems().get(0).getNumberOfTasks());
        assertEquals(2, detailedLineNoAttachment1.getLineItems().get(1).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment1.getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment1.getLineItems().get(3).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment1.getLineItems().get(4).getNumberOfTasks());

        DetailedReportLine line2 = (DetailedReportLine) report.getReportLines().get("L20000");
        assertEquals(5, line2.getLineItems().get(0).getNumberOfTasks());
        assertEquals(3, line2.getLineItems().get(1).getNumberOfTasks());
        assertEquals(1, line2.getLineItems().get(2).getNumberOfTasks());
        assertEquals(1, line2.getLineItems().get(3).getNumberOfTasks());
        assertEquals(0, line2.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLine2 = line2.getDetailLines().get("L22000");
        assertEquals(1, detailedLine2.getLineItems().get(0).getNumberOfTasks());
        assertEquals(1, detailedLine2.getLineItems().get(1).getNumberOfTasks());
        assertEquals(1, detailedLine2.getLineItems().get(2).getNumberOfTasks());
        assertEquals(1, detailedLine2.getLineItems().get(3).getNumberOfTasks());
        assertEquals(0, detailedLine2.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLineNoAttachment2 = line2.getDetailLines().get("N/A");
        assertEquals(4, detailedLineNoAttachment2.getLineItems().get(0).getNumberOfTasks());
        assertEquals(2, detailedLineNoAttachment2.getLineItems().get(1).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment2.getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment2.getLineItems().get(3).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment2.getLineItems().get(4).getNumberOfTasks());

        DetailedReportLine line3 = (DetailedReportLine) report.getReportLines().get("L30000");
        assertEquals(2, line3.getLineItems().get(0).getNumberOfTasks());
        assertEquals(1, line3.getLineItems().get(1).getNumberOfTasks());
        assertEquals(0, line3.getLineItems().get(2).getNumberOfTasks());
        assertEquals(1, line3.getLineItems().get(3).getNumberOfTasks());
        assertEquals(3, line3.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLine3a = line3.getDetailLines().get("L33000");
        assertEquals(0, detailedLine3a.getLineItems().get(0).getNumberOfTasks());
        assertEquals(1, detailedLine3a.getLineItems().get(1).getNumberOfTasks());
        assertEquals(0, detailedLine3a.getLineItems().get(2).getNumberOfTasks());
        assertEquals(1, detailedLine3a.getLineItems().get(3).getNumberOfTasks());
        assertEquals(1, detailedLine3a.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLine3b = line3.getDetailLines().get("L99000");
        assertEquals(0, detailedLine3b.getLineItems().get(0).getNumberOfTasks());
        assertEquals(0, detailedLine3b.getLineItems().get(1).getNumberOfTasks());
        assertEquals(0, detailedLine3b.getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, detailedLine3b.getLineItems().get(3).getNumberOfTasks());
        assertEquals(1, detailedLine3b.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLineNoAttachment3 = line3.getDetailLines().get("N/A");
        assertEquals(2, detailedLineNoAttachment3.getLineItems().get(0).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment3.getLineItems().get(1).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment3.getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment3.getLineItems().get(3).getNumberOfTasks());
        assertEquals(1, detailedLineNoAttachment3.getLineItems().get(4).getNumberOfTasks());

        DetailedReportLine line4 = (DetailedReportLine) report.getReportLines().get("L40000");
        assertEquals(2, line4.getLineItems().get(0).getNumberOfTasks());
        assertEquals(2, line4.getLineItems().get(1).getNumberOfTasks());
        assertEquals(2, line4.getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, line4.getLineItems().get(3).getNumberOfTasks());
        assertEquals(4, line4.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLineNoAttachment4 = line4.getDetailLines().get("N/A");
        assertEquals(2, detailedLineNoAttachment4.getLineItems().get(0).getNumberOfTasks());
        assertEquals(2, detailedLineNoAttachment4.getLineItems().get(1).getNumberOfTasks());
        assertEquals(2, detailedLineNoAttachment4.getLineItems().get(2).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment4.getLineItems().get(3).getNumberOfTasks());
        assertEquals(4, detailedLineNoAttachment4.getLineItems().get(4).getNumberOfTasks());

        DetailedReportLine line5 = (DetailedReportLine) report.getReportLines().get("L50000");
        assertEquals(3, line5.getLineItems().get(0).getNumberOfTasks());
        assertEquals(3, line5.getLineItems().get(1).getNumberOfTasks());
        assertEquals(0, line5.getLineItems().get(2).getNumberOfTasks());
        assertEquals(5, line5.getLineItems().get(3).getNumberOfTasks());
        assertEquals(2, line5.getLineItems().get(4).getNumberOfTasks());

        ReportLine detailedLineNoAttachment5 = line5.getDetailLines().get("N/A");
        assertEquals(3, detailedLineNoAttachment5.getLineItems().get(0).getNumberOfTasks());
        assertEquals(3, detailedLineNoAttachment5.getLineItems().get(1).getNumberOfTasks());
        assertEquals(0, detailedLineNoAttachment5.getLineItems().get(2).getNumberOfTasks());
        assertEquals(5, detailedLineNoAttachment5.getLineItems().get(3).getNumberOfTasks());
        assertEquals(2, detailedLineNoAttachment5.getLineItems().get(4).getNumberOfTasks());
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
