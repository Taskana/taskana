package acceptance.monitoring;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.report.impl.CombinedClassificationFilter;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.impl.report.impl.WorkbasketReport;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "workbasket level report" scenarios.
 */
@RunWith(JAASRunner.class)
public class ProvideWorkbasketReportAccTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvideWorkbasketReportAccTest.class);
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
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, TaskanaEngineConfigurationTest.getSchemaName());
        taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        cleaner.clearDb(dataSource, false);
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        testDataGenerator.generateMonitoringTestData(dataSource);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testRoleCheck()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        taskMonitorService.createWorkbasketReportBuilder().buildReport();
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTotalNumbersOfTasksOfWorkbasketReport()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder().buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        assertEquals(20, report.getRow("USER_1_1").getTotalValue());
        assertEquals(20, report.getRow("USER_1_2").getTotalValue());
        assertEquals(10, report.getRow("USER_1_3").getTotalValue());

        assertEquals(50, report.getSumRow().getTotalValue());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetWorkbasketReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        int sumLineCount = IntStream.of(report.getSumRow().getCells()).sum();

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        assertEquals(20, report.getRow("USER_1_1").getTotalValue());
        assertEquals(20, report.getRow("USER_1_2").getTotalValue());
        assertEquals(10, report.getRow("USER_1_3").getTotalValue());

        int[] sumRow = report.getSumRow().getCells();
        assertArrayEquals(new int[] {10, 9, 11, 0, 4, 0, 7, 4, 5}, sumRow);

        assertEquals(50, report.getSumRow().getTotalValue());
        assertEquals(50, sumLineCount);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfWorkbasketReport() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {13, 3, 1, 1, 2}, row1);

        int[] row2 = report.getRow("USER_1_2").getCells();
        assertArrayEquals(new int[] {4, 6, 3, 6, 1}, row2);

        int[] row3 = report.getRow("USER_1_3").getCells();
        assertArrayEquals(new int[] {2, 2, 0, 0, 6}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfWorkbasketReportNotInWorkingDays()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {16, 0, 1, 0, 3}, row1);

        int[] row2 = report.getRow("USER_1_2").getCells();
        assertArrayEquals(new int[] {10, 0, 3, 0, 7}, row2);

        int[] row3 = report.getRow("USER_1_3").getCells();
        assertArrayEquals(new int[] {4, 0, 0, 0, 6}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfWorkbasketReportWithWorkbasketFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .workbasketIdIn(workbasketIds)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(1, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {13, 3, 1, 1, 2}, row1);

    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfWorkbasketReportWithStateFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TaskState> states = Collections.singletonList(TaskState.READY);
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {13, 3, 1, 1, 0}, row1);

        int[] row2 = report.getRow("USER_1_2").getCells();
        assertArrayEquals(new int[] {4, 6, 3, 6, 0}, row2);

        int[] row3 = report.getRow("USER_1_3").getCells();
        assertArrayEquals(new int[] {2, 2, 0, 0, 0}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfWorkbasketReportWithCategoryFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .categoryIn(categories)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {3, 1, 1, 1, 2}, row1);

        int[] row2 = report.getRow("USER_1_2").getCells();
        assertArrayEquals(new int[] {1, 1, 1, 0, 1}, row2);

        int[] row3 = report.getRow("USER_1_3").getCells();
        assertArrayEquals(new int[] {0, 1, 0, 0, 4}, row3);

    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfWorkbasketReportWithDomainFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {8, 1, 0, 1, 2}, row1);

        int[] row2 = report.getRow("USER_1_2").getCells();
        assertArrayEquals(new int[] {2, 2, 2, 4, 0}, row2);

        int[] row3 = report.getRow("USER_1_3").getCells();
        assertArrayEquals(new int[] {1, 1, 0, 0, 2}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfWorkbasketReportWithCustomFieldValueFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {6, 1, 1, 1, 1}, row1);

        int[] row2 = report.getRow("USER_1_2").getCells();
        assertArrayEquals(new int[] {3, 2, 2, 3, 1}, row2);

        int[] row3 = report.getRow("USER_1_3").getCells();
        assertArrayEquals(new int[] {2, 1, 0, 0, 1}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfWorkbasketReportForSelectedClassifications()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();
        List<CombinedClassificationFilter> combinedClassificationFilter = new ArrayList<>();
        combinedClassificationFilter
            .add(new CombinedClassificationFilter("CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000008"));
        combinedClassificationFilter
            .add(new CombinedClassificationFilter("CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000009"));
        combinedClassificationFilter
            .add(new CombinedClassificationFilter("CLI:000000000000000000000000000000000002",
                "CLI:000000000000000000000000000000000007"));
        combinedClassificationFilter
            .add(new CombinedClassificationFilter("CLI:000000000000000000000000000000000005"));

        WorkbasketReport report = taskMonitorService.createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {3, 3, 0, 1, 1}, row1);

        int[] row2 = report.getRow("USER_1_2").getCells();
        assertArrayEquals(new int[] {0, 2, 1, 6, 0}, row2);

        int[] row3 = report.getRow("USER_1_3").getCells();
        assertArrayEquals(new int[] {1, 0, 0, 0, 3}, row3);
    }

    private List<TimeIntervalColumnHeader> getListOfColumnHeaders() {
        List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
        columnHeaders.add(new TimeIntervalColumnHeader(Integer.MIN_VALUE, -11));
        columnHeaders.add(new TimeIntervalColumnHeader(-10, -6));
        columnHeaders.add(new TimeIntervalColumnHeader(-5, -2));
        columnHeaders.add(new TimeIntervalColumnHeader(-1));
        columnHeaders.add(new TimeIntervalColumnHeader(0));
        columnHeaders.add(new TimeIntervalColumnHeader(1));
        columnHeaders.add(new TimeIntervalColumnHeader(2, 5));
        columnHeaders.add(new TimeIntervalColumnHeader(6, 10));
        columnHeaders.add(new TimeIntervalColumnHeader(11, Integer.MAX_VALUE));
        return columnHeaders;
    }

    private List<TimeIntervalColumnHeader> getShortListOfColumnHeaders() {
        List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
        columnHeaders.add(new TimeIntervalColumnHeader(Integer.MIN_VALUE, -6));
        columnHeaders.add(new TimeIntervalColumnHeader(-5, -1));
        columnHeaders.add(new TimeIntervalColumnHeader(0));
        columnHeaders.add(new TimeIntervalColumnHeader(1, 5));
        columnHeaders.add(new TimeIntervalColumnHeader(6, Integer.MAX_VALUE));
        return columnHeaders;
    }

    private String reportToString(WorkbasketReport report) {
        return reportToString(report, null);
    }

    private String reportToString(WorkbasketReport report,
        List<TimeIntervalColumnHeader> reportLineItemDefinitions) {
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
        builder.append(String.format(formatFirstColumnFirstLine, "Workbaskets", "Total"));
        if (reportLineItemDefinitions != null) {
            for (TimeIntervalColumnHeader def : reportLineItemDefinitions) {
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

        for (String rl : report.rowTitles()) {
            builder
                .append(String.format(formatFirstColumn, rl, report.getRow(rl).getTotalValue()));
            if (reportLineItemDefinitions != null) {
                for (int cell : report.getRow(rl).getCells()) {
                    builder.append(String.format(formatColumWidth, cell));
                }
            }
            builder.append("|\n");
            for (int i = 0; i < reportWidth; i++) {
                builder.append("-");
            }
            builder.append("\n");
        }
        builder.append(String.format(formatFirstColumnSumLine, "Total", report.getSumRow().getTotalValue()));
        for (int cell : report.getSumRow().getCells()) {
            builder.append(String.format(formatColumWidth, cell));
        }
        builder.append("|\n");
        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");
        return builder.toString();
    }

}
