package acceptance.monitoring;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.sql.DataSource;

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
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.impl.report.impl.WorkbasketLevelReport;

/**
 * Acceptance test for all "workbasket level report" scenarios.
 */
public class ProvideWorkbasketLevelReportAccTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvideWorkbasketLevelReportAccTest.class);
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
        taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        cleaner.clearDb(dataSource, false);
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        testDataGenerator.generateMonitoringTestData(dataSource);
    }

    @Test
    public void testGetTotalNumbersOfTasksOfWorkbasketLevelReport() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null, null, null);

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

    @Test
    public void testGetWorkbasketLevelReportWithReportLineItemDefinitions() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null, null, null,
            columnHeaders);

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

    @Test
    public void testEachItemOfWorkbasketLevelReport() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null, null, null,
            columnHeaders);

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

    @Test
    public void testEachItemOfWorkbasketLevelReportNotInWorkingDays() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null, null, null,
            columnHeaders, false);

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

    @Test
    public void testEachItemOfWorkbasketLevelReportWithWorkbasketFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(workbasketIds, null, null, null,
            null, null,
            columnHeaders);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(1, report.rowSize());

        int[] row1 = report.getRow("USER_1_1").getCells();
        assertArrayEquals(new int[] {13, 3, 1, 1, 2}, row1);

    }

    @Test
    public void testEachItemOfWorkbasketLevelReportWithStateFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TaskState> states = Collections.singletonList(TaskState.READY);
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(null, states, null, null, null, null,
            columnHeaders);

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

    @Test
    public void testEachItemOfWorkbasketLevelReportWithCategoryFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(null, null, categories, null, null,
            null,
            columnHeaders);

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

    @Test
    public void testEachItemOfWorkbasketLevelReportWithDomainFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(null, null, null, domains, null,
            null,
            columnHeaders);

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

    @Test
    public void testEachItemOfWorkbasketLevelReportWithCustomFieldValueFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Collections.singletonList("Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        WorkbasketLevelReport report = taskMonitorService.getWorkbasketLevelReport(null, null, null, null,
            customField, customFieldValues, columnHeaders);

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

    private String reportToString(WorkbasketLevelReport report) {
        return reportToString(report, null);
    }

    private String reportToString(WorkbasketLevelReport report,
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
        builder.append(String.format(formatFirstColumnFirstLine, "Workbasket levels", "Total"));
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
