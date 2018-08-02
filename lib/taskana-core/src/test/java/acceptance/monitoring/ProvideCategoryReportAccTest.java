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
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.report.impl.CategoryReport;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "category report" scenarios.
 */
@RunWith(JAASRunner.class)
public class ProvideCategoryReportAccTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvideCategoryReportAccTest.class);
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
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false,
            TaskanaEngineConfigurationTest.getSchemaName());
        taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        ((TaskanaEngineImpl) taskanaEngine).setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        cleaner.clearDb(dataSource, false);
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        testDataGenerator.generateMonitoringTestData(dataSource);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testRoleCheck() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        taskMonitorService.createCategoryReportBuilder().buildReport();
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTotalNumbersOfTasksOfCategoryReport() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder().buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        assertEquals(33, report.getRow("EXTERN").getTotalValue());
        assertEquals(7, report.getRow("AUTOMATIC").getTotalValue());
        assertEquals(10, report.getRow("MANUAL").getTotalValue());
        assertEquals(0, report.getRow("EXTERN").getCells().length);
        assertEquals(0, report.getRow("AUTOMATIC").getCells().length);
        assertEquals(0, report.getRow("MANUAL").getCells().length);
        assertEquals(50, report.getSumRow().getTotalValue());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetCategoryReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        int sumLineCount = IntStream.of(report.getSumRow().getCells()).sum();

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        assertEquals(33, report.getRow("EXTERN").getTotalValue());
        assertEquals(7, report.getRow("AUTOMATIC").getTotalValue());
        assertEquals(10, report.getRow("MANUAL").getTotalValue());

        int[] sumRow = report.getSumRow().getCells();
        assertArrayEquals(new int[] {10, 9, 11, 0, 4, 0, 7, 4, 5}, sumRow);
        assertEquals(50, report.getSumRow().getTotalValue());
        assertEquals(50, sumLineCount);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfCategoryReport() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("EXTERN").getCells();
        assertArrayEquals(new int[] {15, 8, 2, 6, 2}, row1);

        int[] row2 = report.getRow("AUTOMATIC").getCells();
        assertArrayEquals(new int[] {2, 1, 0, 1, 3}, row2);

        int[] row3 = report.getRow("MANUAL").getCells();
        assertArrayEquals(new int[] {2, 2, 2, 0, 4}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfCategoryReportNotInWorkingDays() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("EXTERN").getCells();
        assertArrayEquals(new int[] {23, 0, 2, 0, 8}, row1);

        int[] row2 = report.getRow("AUTOMATIC").getCells();
        assertArrayEquals(new int[] {3, 0, 0, 0, 4}, row2);

        int[] row3 = report.getRow("MANUAL").getCells();
        assertArrayEquals(new int[] {4, 0, 2, 0, 4}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfCategoryReportWithWorkbasketFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("EXTERN").getCells();
        assertArrayEquals(new int[] {10, 2, 0, 0, 0}, row1);

        int[] row2 = report.getRow("AUTOMATIC").getCells();
        assertArrayEquals(new int[] {2, 1, 0, 1, 1}, row2);

        int[] row3 = report.getRow("MANUAL").getCells();
        assertArrayEquals(new int[] {1, 0, 1, 0, 1}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfCategoryReportWithStateFilter() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TaskState> states = Collections.singletonList(TaskState.READY);
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("EXTERN").getCells();
        assertArrayEquals(new int[] {15, 8, 2, 6, 0}, row1);

        int[] row2 = report.getRow("AUTOMATIC").getCells();
        assertArrayEquals(new int[] {2, 1, 0, 1, 0}, row2);

        int[] row3 = report.getRow("MANUAL").getCells();
        assertArrayEquals(new int[] {2, 2, 2, 0, 0}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfCategoryReportWithCategoryFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .categoryIn(categories)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(2, report.rowSize());

        int[] row1 = report.getRow("AUTOMATIC").getCells();
        assertArrayEquals(new int[] {2, 1, 0, 1, 3}, row1);

        int[] row2 = report.getRow("MANUAL").getCells();
        assertArrayEquals(new int[] {2, 2, 2, 0, 4}, row2);

    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfCategoryReportWithDomainFilter() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("EXTERN").getCells();
        assertArrayEquals(new int[] {8, 4, 2, 4, 0}, row1);

        int[] row2 = report.getRow("AUTOMATIC").getCells();
        assertArrayEquals(new int[] {1, 0, 0, 1, 1}, row2);

        int[] row3 = report.getRow("MANUAL").getCells();
        assertArrayEquals(new int[] {2, 0, 0, 0, 3}, row3);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfCategoryReportWithCustomFieldValueFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        CategoryReport report = taskMonitorService.createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .buildReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(3, report.rowSize());

        int[] row1 = report.getRow("EXTERN").getCells();
        assertArrayEquals(new int[] {9, 3, 1, 3, 0}, row1);

        int[] row2 = report.getRow("AUTOMATIC").getCells();
        assertArrayEquals(new int[] {1, 0, 0, 1, 1}, row2);

        int[] row3 = report.getRow("MANUAL").getCells();
        assertArrayEquals(new int[] {1, 1, 2, 0, 2}, row3);
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

    private String reportToString(CategoryReport report) {
        return reportToString(report, null);
    }

    private String reportToString(CategoryReport report,
        List<TimeIntervalColumnHeader> columnHeaders) {
        String formatColumWidth = "| %-7s ";
        String formatFirstColumn = "| %-36s  %-4s ";
        String formatFirstColumnFirstLine = "| %-29s %12s ";
        String formatFirstColumnSumLine = "| %-36s  %-5s";
        int reportWidth = columnHeaders == null ? 46 : columnHeaders.size() * 10 + 46;

        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");
        builder.append(String.format(formatFirstColumnFirstLine, "Categories", "Total"));
        if (columnHeaders != null) {
            for (TimeIntervalColumnHeader def : columnHeaders) {
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
            if (columnHeaders != null) {
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
