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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.database.TestDataGenerator;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.Report;
import pro.taskana.impl.ReportLineItem;
import pro.taskana.impl.ReportLineItemDefinition;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;

/**
 * Acceptance test for all "workbasket level report" scenarios.
 */
public class ProvideWorkbasketLevelReportAccTest {

    protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected static TaskanaEngine taskanaEngine;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProvideWorkbasketLevelReportAccTest.class);

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
        taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        ((TaskanaEngineImpl) taskanaEngine).setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        cleaner.clearDb(dataSource, false);
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        testDataGenerator.generateMonitoringTestData(dataSource);
    }

    @Test
    public void testGetTotalNumbersOfTasksOfWorkbasketLevelReport() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        Report report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null, null, null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report));
        }

        assertNotNull(report);
        assertEquals(20, report.getReportLines().get("USER_1_1").getTotalNumberOfTasks());
        assertEquals(20, report.getReportLines().get("USER_1_2").getTotalNumberOfTasks());
        assertEquals(10, report.getReportLines().get("USER_1_3").getTotalNumberOfTasks());
        assertEquals(50, report.getSumLine().getTotalNumberOfTasks());
    }

    @Test
    public void testGetWorkbasketLevelReportWithReportLineItemDefinitions() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null, null, null,
            reportLineItemDefinitions);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, reportLineItemDefinitions));
        }

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

        assertEquals(20, report.getReportLines().get("USER_1_1").getTotalNumberOfTasks());
        assertEquals(20, report.getReportLines().get("USER_1_2").getTotalNumberOfTasks());
        assertEquals(10, report.getReportLines().get("USER_1_3").getTotalNumberOfTasks());

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

    @Test
    public void testEachItemOfWorkbasketLevelReport() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null, null, null,
            reportLineItemDefinitions);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, reportLineItemDefinitions));
        }

        List<ReportLineItem> line1 = report.getReportLines().get("USER_1_1").getLineItems();
        assertEquals(13, line1.get(0).getNumberOfTasks());
        assertEquals(3, line1.get(1).getNumberOfTasks());
        assertEquals(1, line1.get(2).getNumberOfTasks());
        assertEquals(1, line1.get(3).getNumberOfTasks());
        assertEquals(2, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getReportLines().get("USER_1_2").getLineItems();
        assertEquals(4, line2.get(0).getNumberOfTasks());
        assertEquals(6, line2.get(1).getNumberOfTasks());
        assertEquals(3, line2.get(2).getNumberOfTasks());
        assertEquals(6, line2.get(3).getNumberOfTasks());
        assertEquals(1, line2.get(4).getNumberOfTasks());

        List<ReportLineItem> line3 = report.getReportLines().get("USER_1_3").getLineItems();
        assertEquals(2, line3.get(0).getNumberOfTasks());
        assertEquals(2, line3.get(1).getNumberOfTasks());
        assertEquals(0, line3.get(2).getNumberOfTasks());
        assertEquals(0, line3.get(3).getNumberOfTasks());
        assertEquals(6, line3.get(4).getNumberOfTasks());
    }

    @Test
    public void testEachItemOfWorkbasketLevelReportNotInWorkingDays() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null, null, null,
            reportLineItemDefinitions, false);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, reportLineItemDefinitions));
        }

        List<ReportLineItem> line1 = report.getReportLines().get("USER_1_1").getLineItems();
        assertEquals(16, line1.get(0).getNumberOfTasks());
        assertEquals(0, line1.get(1).getNumberOfTasks());
        assertEquals(1, line1.get(2).getNumberOfTasks());
        assertEquals(0, line1.get(3).getNumberOfTasks());
        assertEquals(3, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getReportLines().get("USER_1_2").getLineItems();
        assertEquals(10, line2.get(0).getNumberOfTasks());
        assertEquals(0, line2.get(1).getNumberOfTasks());
        assertEquals(3, line2.get(2).getNumberOfTasks());
        assertEquals(0, line2.get(3).getNumberOfTasks());
        assertEquals(7, line2.get(4).getNumberOfTasks());

        List<ReportLineItem> line3 = report.getReportLines().get("USER_1_3").getLineItems();
        assertEquals(4, line3.get(0).getNumberOfTasks());
        assertEquals(0, line3.get(1).getNumberOfTasks());
        assertEquals(0, line3.get(2).getNumberOfTasks());
        assertEquals(0, line3.get(3).getNumberOfTasks());
        assertEquals(6, line3.get(4).getNumberOfTasks());
    }

    @Test
    public void testEachItemOfWorkbasketLevelReportWithWorkbasketFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getWorkbasketLevelReport(workbasketIds, null, null, null, null, null,
            reportLineItemDefinitions);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, reportLineItemDefinitions));
        }

        List<ReportLineItem> line1 = report.getReportLines().get("USER_1_1").getLineItems();
        assertEquals(13, line1.get(0).getNumberOfTasks());
        assertEquals(3, line1.get(1).getNumberOfTasks());
        assertEquals(1, line1.get(2).getNumberOfTasks());
        assertEquals(1, line1.get(3).getNumberOfTasks());
        assertEquals(2, line1.get(4).getNumberOfTasks());

        assertEquals(1, report.getReportLines().size());
    }

    @Test
    public void testEachItemOfWorkbasketLevelReportWithStateFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TaskState> states = Arrays.asList(TaskState.READY);
        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getWorkbasketLevelReport(null, states, null, null, null, null,
            reportLineItemDefinitions);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, reportLineItemDefinitions));
        }

        List<ReportLineItem> line1 = report.getReportLines().get("USER_1_1").getLineItems();
        assertEquals(13, line1.get(0).getNumberOfTasks());
        assertEquals(3, line1.get(1).getNumberOfTasks());
        assertEquals(1, line1.get(2).getNumberOfTasks());
        assertEquals(1, line1.get(3).getNumberOfTasks());
        assertEquals(0, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getReportLines().get("USER_1_2").getLineItems();
        assertEquals(4, line2.get(0).getNumberOfTasks());
        assertEquals(6, line2.get(1).getNumberOfTasks());
        assertEquals(3, line2.get(2).getNumberOfTasks());
        assertEquals(6, line2.get(3).getNumberOfTasks());
        assertEquals(0, line2.get(4).getNumberOfTasks());

        List<ReportLineItem> line3 = report.getReportLines().get("USER_1_3").getLineItems();
        assertEquals(2, line3.get(0).getNumberOfTasks());
        assertEquals(2, line3.get(1).getNumberOfTasks());
        assertEquals(0, line3.get(2).getNumberOfTasks());
        assertEquals(0, line3.get(3).getNumberOfTasks());
        assertEquals(0, line3.get(4).getNumberOfTasks());
    }

    @Test
    public void testEachItemOfWorkbasketLevelReportWithCategoryFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getWorkbasketLevelReport(null, null, categories, null, null, null,
            reportLineItemDefinitions);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, reportLineItemDefinitions));
        }

        List<ReportLineItem> line1 = report.getReportLines().get("USER_1_1").getLineItems();
        assertEquals(3, line1.get(0).getNumberOfTasks());
        assertEquals(1, line1.get(1).getNumberOfTasks());
        assertEquals(1, line1.get(2).getNumberOfTasks());
        assertEquals(1, line1.get(3).getNumberOfTasks());
        assertEquals(2, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getReportLines().get("USER_1_2").getLineItems();
        assertEquals(1, line2.get(0).getNumberOfTasks());
        assertEquals(1, line2.get(1).getNumberOfTasks());
        assertEquals(1, line2.get(2).getNumberOfTasks());
        assertEquals(0, line2.get(3).getNumberOfTasks());
        assertEquals(1, line2.get(4).getNumberOfTasks());

        List<ReportLineItem> line3 = report.getReportLines().get("USER_1_3").getLineItems();
        assertEquals(0, line3.get(0).getNumberOfTasks());
        assertEquals(1, line3.get(1).getNumberOfTasks());
        assertEquals(0, line3.get(2).getNumberOfTasks());
        assertEquals(0, line3.get(3).getNumberOfTasks());
        assertEquals(4, line3.get(4).getNumberOfTasks());

        assertEquals(3, report.getReportLines().size());
    }

    @Test
    public void testEachItemOfWorkbasketLevelReportWithDomainFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> domains = Arrays.asList("DOMAIN_A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getWorkbasketLevelReport(null, null, null, domains, null, null,
            reportLineItemDefinitions);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, reportLineItemDefinitions));
        }

        List<ReportLineItem> line1 = report.getReportLines().get("USER_1_1").getLineItems();
        assertEquals(8, line1.get(0).getNumberOfTasks());
        assertEquals(1, line1.get(1).getNumberOfTasks());
        assertEquals(0, line1.get(2).getNumberOfTasks());
        assertEquals(1, line1.get(3).getNumberOfTasks());
        assertEquals(2, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getReportLines().get("USER_1_2").getLineItems();
        assertEquals(2, line2.get(0).getNumberOfTasks());
        assertEquals(2, line2.get(1).getNumberOfTasks());
        assertEquals(2, line2.get(2).getNumberOfTasks());
        assertEquals(4, line2.get(3).getNumberOfTasks());
        assertEquals(0, line2.get(4).getNumberOfTasks());

        List<ReportLineItem> line3 = report.getReportLines().get("USER_1_3").getLineItems();
        assertEquals(1, line3.get(0).getNumberOfTasks());
        assertEquals(1, line3.get(1).getNumberOfTasks());
        assertEquals(0, line3.get(2).getNumberOfTasks());
        assertEquals(0, line3.get(3).getNumberOfTasks());
        assertEquals(2, line3.get(4).getNumberOfTasks());
    }

    @Test
    public void testEachItemOfWorkbasketLevelReportWithCustomFieldValueFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Arrays.asList("Geschaeftsstelle A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getShortListOfReportLineItemDefinitions();

        Report report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null,
            customField, customFieldValues, reportLineItemDefinitions);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, reportLineItemDefinitions));
        }

        List<ReportLineItem> line1 = report.getReportLines().get("USER_1_1").getLineItems();
        assertEquals(6, line1.get(0).getNumberOfTasks());
        assertEquals(1, line1.get(1).getNumberOfTasks());
        assertEquals(1, line1.get(2).getNumberOfTasks());
        assertEquals(1, line1.get(3).getNumberOfTasks());
        assertEquals(1, line1.get(4).getNumberOfTasks());

        List<ReportLineItem> line2 = report.getReportLines().get("USER_1_2").getLineItems();
        assertEquals(3, line2.get(0).getNumberOfTasks());
        assertEquals(2, line2.get(1).getNumberOfTasks());
        assertEquals(2, line2.get(2).getNumberOfTasks());
        assertEquals(3, line2.get(3).getNumberOfTasks());
        assertEquals(1, line2.get(4).getNumberOfTasks());

        List<ReportLineItem> line3 = report.getReportLines().get("USER_1_3").getLineItems();
        assertEquals(2, line3.get(0).getNumberOfTasks());
        assertEquals(1, line3.get(1).getNumberOfTasks());
        assertEquals(0, line3.get(2).getNumberOfTasks());
        assertEquals(0, line3.get(3).getNumberOfTasks());
        assertEquals(1, line3.get(4).getNumberOfTasks());
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

    private String reportToString(Report report) {
        return reportToString(report, null);
    }

    private String reportToString(Report report, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        String formatColumWidth = "| %-7s ";
        String formatFirstColumn = "| %-36s  %-4s ";
        String formatFirstColumnFirstLine = "| %-29s %12s ";
        String formatFirstColumnSumLine = "| %-36s  %-5s";
        int reportWidth = reportLineItemDefinitions == null ? 46 : reportLineItemDefinitions.size() * 10 + 46;

        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");
        builder.append(String.format(formatFirstColumnFirstLine, "Workbasket levels", "Total"));
        if (reportLineItemDefinitions != null) {
            for (ReportLineItemDefinition def : reportLineItemDefinitions) {
                if (def.getLowerAgeLimit() == Integer.MIN_VALUE) {
                    builder.append(String.format(formatColumWidth, "< " + def.getUpperAgeLimit()));
                } else if (def.getUpperAgeLimit() == Integer.MAX_VALUE) {
                    builder.append(String.format(formatColumWidth, "> " + def.getLowerAgeLimit()));
                } else if (def.getLowerAgeLimit() == def.getUpperAgeLimit()) {
                    if (def.getLowerAgeLimit() == 0) {
                        builder.append(String.format(formatColumWidth, "today"));
                    } else {
                        builder.append(String.format(formatColumWidth, def.getLowerAgeLimit()));
                    }
                } else {
                    builder.append(
                        String.format(formatColumWidth, def.getLowerAgeLimit() + ".." + def.getUpperAgeLimit()));
                }
            }
        }
        builder.append("|\n");

        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");

        for (String rl : report.getReportLines().keySet()) {
            builder
                .append(String.format(formatFirstColumn, rl, report.getReportLines().get(rl).getTotalNumberOfTasks()));
            if (reportLineItemDefinitions != null) {
                for (ReportLineItem reportLineItem : report.getReportLines().get(rl).getLineItems()) {
                    builder.append(String.format(formatColumWidth, reportLineItem.getNumberOfTasks()));
                }
            }
            builder.append("|\n");
            for (int i = 0; i < reportWidth; i++) {
                builder.append("-");
            }
            builder.append("\n");
        }
        builder.append(String.format(formatFirstColumnSumLine, "Total", report.getSumLine().getTotalNumberOfTasks()));
        for (ReportLineItem sumLine : report.getSumLine().getLineItems()) {
            builder.append(String.format(formatColumWidth, sumLine.getNumberOfTasks()));
        }
        builder.append("|\n");
        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");
        return builder.toString();
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/taskana-h2-data", true);
    }
}
