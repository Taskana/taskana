package acceptance.monitoring;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.database.TestDataGenerator;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.report.ReportRow;
import pro.taskana.impl.report.impl.TaskQueryItem;
import pro.taskana.impl.report.impl.TaskStatusColumnHeader;
import pro.taskana.impl.report.impl.TaskStatusReport;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "task status report" scenarios.
 */
@RunWith(JAASRunner.class)
public class ProvideTaskStatusReportAccTest {

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
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false,
            TaskanaEngineConfigurationTest.getSchemaName());
        taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngine.setConnectionManagementMode(TaskanaEngine.ConnectionManagementMode.AUTOCOMMIT);
        cleaner.clearDb(dataSource, false);
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        testDataGenerator.generateMonitoringTestData(dataSource);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testRoleCheck() throws NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
        taskMonitorService.createTaskStatusReportBuilder().buildReport();
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testCompleteTaskStatusReport() throws NotAuthorizedException {
        // given
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
        // when
        TaskStatusReport report = taskMonitorService.createTaskStatusReportBuilder().buildReport();
        // then
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report));
        }
        assertNotNull(report);
        assertEquals(3, report.rowSize());

        ReportRow<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
        assertArrayEquals(new int[] {22, 4, 0}, row1.getCells());
        assertEquals(26, row1.getTotalValue());

        ReportRow<TaskQueryItem> row2 = report.getRow("DOMAIN_B");
        assertArrayEquals(new int[] {9, 3, 0}, row2.getCells());
        assertEquals(12, row2.getTotalValue());

        ReportRow<TaskQueryItem> row3 = report.getRow("DOMAIN_C");
        assertArrayEquals(new int[] {10, 2, 0}, row3.getCells());
        assertEquals(12, row3.getTotalValue());

        ReportRow<TaskQueryItem> sumRow = report.getSumRow();
        assertArrayEquals(new int[] {41, 9, 0}, sumRow.getCells());
        assertEquals(50, sumRow.getTotalValue());
    }

    @WithAccessId(
        userName = "admin")
    @Test
    public void testCompleteTaskStatusReportAsAdmin() throws NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
        taskMonitorService.createTaskStatusReportBuilder().buildReport();
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testCompleteTaskStatusReportWithDomainFilter() throws NotAuthorizedException {
        // given
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
        // when
        TaskStatusReport report = taskMonitorService
            .createTaskStatusReportBuilder()
            .domainIn(asList("DOMAIN_C", "DOMAIN_A"))
            .buildReport();
        // then
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report));
        }
        assertNotNull(report);
        assertEquals(2, report.rowSize());

        ReportRow<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
        assertArrayEquals(new int[] {22, 4, 0}, row1.getCells());
        assertEquals(26, row1.getTotalValue());

        ReportRow<TaskQueryItem> row2 = report.getRow("DOMAIN_C");
        assertArrayEquals(new int[] {10, 2, 0}, row2.getCells());
        assertEquals(12, row2.getTotalValue());

        ReportRow<TaskQueryItem> sumRow = report.getSumRow();
        assertArrayEquals(new int[] {32, 6, 0}, sumRow.getCells());
        assertEquals(38, sumRow.getTotalValue());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testCompleteTaskStatusReportWithStateFilter() throws NotAuthorizedException {
        // given
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
        // when
        TaskStatusReport report = taskMonitorService
            .createTaskStatusReportBuilder()
            .stateIn(Collections.singletonList(TaskState.READY))
            .buildReport();
        // then
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report));
        }
        assertNotNull(report);
        assertEquals(3, report.rowSize());

        ReportRow<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
        assertArrayEquals(new int[] {22}, row1.getCells());
        assertEquals(22, row1.getTotalValue());

        ReportRow<TaskQueryItem> row2 = report.getRow("DOMAIN_B");
        assertArrayEquals(new int[] {9}, row2.getCells());
        assertEquals(9, row2.getTotalValue());

        ReportRow<TaskQueryItem> row3 = report.getRow("DOMAIN_C");
        assertArrayEquals(new int[] {10}, row3.getCells());
        assertEquals(10, row3.getTotalValue());

        ReportRow<TaskQueryItem> sumRow = report.getSumRow();
        assertArrayEquals(new int[] {41}, sumRow.getCells());
        assertEquals(41, sumRow.getTotalValue());
    }

    private String reportToString(TaskStatusReport report) {
        List<TaskStatusColumnHeader> columnHeaders = report.getColumnHeaders();
        String formatColumnWidth = "| %-7s ";
        String formatFirstColumn = "| %-36s  %-4s ";
        String formatFirstColumnFirstLine = "| %-29s %12s ";
        String formatFirstColumnSumLine = "| %-36s  %-5s";
        int reportWidth = columnHeaders.size() * 10 + 46;

        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");
        builder.append(String.format(formatFirstColumnFirstLine, "Domain", "Total"));
        for (TaskStatusColumnHeader def : columnHeaders) {
            builder.append(String.format(formatColumnWidth, def.getDisplayName()));
        }
        builder.append("|\n");

        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");

        for (String rl : report.rowTitles()) {
            builder.append(String.format(formatFirstColumn, rl, report.getRow(rl).getTotalValue()));
            for (int cell : report.getRow(rl).getCells()) {
                builder.append(String.format(formatColumnWidth, cell));
            }
            builder.append("|\n");
            for (int i = 0; i < reportWidth; i++) {
                builder.append("-");
            }
            builder.append("\n");
        }
        builder.append(String.format(formatFirstColumnSumLine, "Total", report.getSumRow().getTotalValue()));
        for (int cell : report.getSumRow().getCells()) {
            builder.append(String.format(formatColumnWidth, cell));
        }
        builder.append("|\n");
        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");
        return builder.toString();
    }

}
