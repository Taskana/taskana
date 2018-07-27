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
import pro.taskana.impl.report.ReportRow;
import pro.taskana.impl.report.impl.DetailedClassificationReport;
import pro.taskana.impl.report.impl.DetailedMonitorQueryItem;
import pro.taskana.impl.report.impl.DetailedReportRow;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "detailed classification report" scenarios.
 */
@RunWith(JAASRunner.class)
public class ProvideDetailedClassificationReportAccTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvideDetailedClassificationReportAccTest.class);
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
    public void testRoleCheck()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        taskMonitorService.createClassificationReportBuilder().buildDetailedReport();
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTotalNumbersOfTasksOfDetailedClassificationReport()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report));
        }

        assertNotNull(report);
        assertEquals(5, report.rowSize());

        DetailedReportRow row1 = report.getRow("L10000");
        assertEquals(10, row1.getTotalValue());
        assertEquals(3, row1.getDetailRows().get("L11000").getTotalValue());
        assertEquals(7, row1.getDetailRows().get("N/A").getTotalValue());
        assertEquals(0, row1.getCells().length);
        assertEquals(2, row1.getDetailRows().size());

        DetailedReportRow row2 = report.getRow("L20000");
        assertEquals(10, row2.getTotalValue());
        assertEquals(4, row2.getDetailRows().get("L22000").getTotalValue());
        assertEquals(6, row2.getDetailRows().get("N/A").getTotalValue());
        assertEquals(0, row2.getCells().length);
        assertEquals(2, row2.getDetailRows().size());

        DetailedReportRow row3 = report.getRow("L30000");
        assertEquals(7, row3.getTotalValue());
        assertEquals(3, row3.getDetailRows().get("L33000").getTotalValue());
        assertEquals(1, row3.getDetailRows().get("L99000").getTotalValue());
        assertEquals(3, row3.getDetailRows().get("N/A").getTotalValue());
        assertEquals(0, row3.getCells().length);
        assertEquals(3, row3.getDetailRows().size());

        DetailedReportRow row4 = report.getRow("L40000");
        assertEquals(10, row4.getTotalValue());
        assertEquals(10, row4.getDetailRows().get("N/A").getTotalValue());
        assertEquals(0, row4.getCells().length);
        assertEquals(1, row4.getDetailRows().size());

        DetailedReportRow row5 = report.getRow("L50000");
        assertEquals(13, row5.getTotalValue());
        assertEquals(13, row5.getDetailRows().get("N/A").getTotalValue());
        assertEquals(0, row5.getCells().length);
        assertEquals(1, row5.getDetailRows().size());

        assertEquals(50, report.getSumRow().getTotalValue());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetDetailedClassificationReportWithReportLineItemDefinitions()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(5, report.rowSize());

        assertEquals(10, report.getRow("L10000").getTotalValue());
        assertEquals(10, report.getRow("L20000").getTotalValue());
        assertEquals(7, report.getRow("L30000").getTotalValue());
        assertEquals(10, report.getRow("L40000").getTotalValue());
        assertEquals(13, report.getRow("L50000").getTotalValue());

        int[] sumRow = report.getSumRow().getCells();
        assertArrayEquals(new int[] {10, 9, 11, 0, 4, 0, 7, 4, 5}, sumRow);
        assertEquals(50, report.getSumRow().getTotalValue());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfDetailedClassificationReport() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(5, report.rowSize());

        DetailedReportRow line1 = report.getRow("L10000");
        assertArrayEquals(new int[] {7, 2, 1, 0, 0}, line1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1 = line1.getDetailRows().get("L11000");
        assertArrayEquals(new int[] {2, 0, 1, 0, 0}, detailedLine1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {5, 2, 0, 0, 0}, detailedLineNoAttachment1.getCells());

        DetailedReportRow line2 = report.getRow("L20000");
        assertArrayEquals(new int[] {5, 3, 1, 1, 0}, line2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine2 = line2.getDetailRows().get("L22000");
        assertArrayEquals(new int[] {1, 1, 1, 1, 0}, detailedLine2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {4, 2, 0, 0, 0}, detailedLineNoAttachment2.getCells());

        DetailedReportRow line3 = report.getRow("L30000");
        assertArrayEquals(new int[] {2, 1, 0, 1, 3}, line3.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine3a = line3.getDetailRows().get("L33000");
        assertArrayEquals(new int[] {0, 1, 0, 1, 1}, detailedLine3a.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine3b = line3.getDetailRows().get("L99000");
        assertArrayEquals(new int[] {0, 0, 0, 0, 1}, detailedLine3b.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 0, 0, 0, 1}, detailedLineNoAttachment3.getCells());

        DetailedReportRow line4 = report.getRow("L40000");
        assertArrayEquals(new int[] {2, 2, 2, 0, 4}, line4.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 2, 2, 0, 4}, detailedLineNoAttachment4.getCells());

        DetailedReportRow line5 = report.getRow("L50000");
        assertArrayEquals(new int[] {3, 3, 0, 5, 2}, line5.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {3, 3, 0, 5, 2}, detailedLineNoAttachment5.getCells());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfDetailedClassificationReportWithWorkbasketFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(5, report.rowSize());

        DetailedReportRow line1 = report.getRow("L10000");
        assertArrayEquals(new int[] {6, 0, 0, 0, 0}, line1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1 = line1.getDetailRows().get("L11000");
        assertArrayEquals(new int[] {2, 0, 0, 0, 0}, detailedLine1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {4, 0, 0, 0, 0}, detailedLineNoAttachment1.getCells());

        DetailedReportRow line2 = report.getRow("L20000");
        assertArrayEquals(new int[] {2, 0, 0, 0, 0}, line2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine2 = line2.getDetailRows().get("L22000");
        assertArrayEquals(new int[] {1, 0, 0, 0, 0}, detailedLine2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {1, 0, 0, 0, 0}, detailedLineNoAttachment2.getCells());

        DetailedReportRow line3 = report.getRow("L30000");
        assertArrayEquals(new int[] {2, 1, 0, 1, 1}, line3.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine3a = line3.getDetailRows().get("L33000");
        assertArrayEquals(new int[] {0, 1, 0, 1, 1}, detailedLine3a.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 0, 0, 0, 0}, detailedLineNoAttachment3.getCells());

        DetailedReportRow line4 = report.getRow("L40000");
        assertArrayEquals(new int[] {1, 0, 1, 0, 1}, line4.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {1, 0, 1, 0, 1}, detailedLineNoAttachment4.getCells());

        DetailedReportRow line5 = report.getRow("L50000");
        assertArrayEquals(new int[] {2, 2, 0, 0, 0}, line5.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 2, 0, 0, 0}, detailedLineNoAttachment5.getCells());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfDetailedClassificationReportWithStateFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TaskState> states = Collections.singletonList(TaskState.READY);
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .stateIn(states)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(5, report.rowSize());

        DetailedReportRow line1 = report.getRow("L10000");
        assertArrayEquals(new int[] {7, 2, 1, 0, 0}, line1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1 = line1.getDetailRows().get("L11000");
        assertArrayEquals(new int[] {2, 0, 1, 0, 0}, detailedLine1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {5, 2, 0, 0, 0}, detailedLineNoAttachment1.getCells());

        DetailedReportRow line2 = report.getRow("L20000");
        assertArrayEquals(new int[] {5, 3, 1, 1, 0}, line2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine2 = line2.getDetailRows().get("L22000");
        assertArrayEquals(new int[] {1, 1, 1, 1, 0}, detailedLine2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {4, 2, 0, 0, 0}, detailedLineNoAttachment2.getCells());

        DetailedReportRow line3 = report.getRow("L30000");
        assertArrayEquals(new int[] {2, 1, 0, 1, 0}, line3.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine3a = line3.getDetailRows().get("L33000");
        assertArrayEquals(new int[] {0, 1, 0, 1, 0}, detailedLine3a.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 0, 0, 0, 0}, detailedLineNoAttachment3.getCells());

        DetailedReportRow line4 = report.getRow("L40000");
        assertArrayEquals(new int[] {2, 2, 2, 0, 0}, line4.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 2, 2, 0, 0}, detailedLineNoAttachment4.getCells());

        DetailedReportRow line5 = report.getRow("L50000");
        assertArrayEquals(new int[] {3, 3, 0, 5, 0}, line5.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {3, 3, 0, 5, 0}, detailedLineNoAttachment5.getCells());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfDetailedClassificationReportNotInWorkingDays()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(5, report.rowSize());

        DetailedReportRow line1 = report.getRow("L10000");
        assertArrayEquals(new int[] {9, 0, 1, 0, 0}, line1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1 = line1.getDetailRows().get("L11000");
        assertArrayEquals(new int[] {2, 0, 1, 0, 0}, detailedLine1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {7, 0, 0, 0, 0}, detailedLineNoAttachment1.getCells());

        DetailedReportRow line2 = report.getRow("L20000");
        assertArrayEquals(new int[] {8, 0, 1, 0, 1}, line2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine2 = line2.getDetailRows().get("L22000");
        assertArrayEquals(new int[] {2, 0, 1, 0, 1}, detailedLine2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {6, 0, 0, 0, 0}, detailedLineNoAttachment2.getCells());

        DetailedReportRow line3 = report.getRow("L30000");
        assertArrayEquals(new int[] {3, 0, 0, 0, 4}, line3.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine3a = line3.getDetailRows().get("L33000");
        assertArrayEquals(new int[] {1, 0, 0, 0, 2}, detailedLine3a.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine3b = line3.getDetailRows().get("L99000");
        assertArrayEquals(new int[] {0, 0, 0, 0, 1}, detailedLine3b.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 0, 0, 0, 1}, detailedLineNoAttachment3.getCells());

        DetailedReportRow line4 = report.getRow("L40000");
        assertArrayEquals(new int[] {4, 0, 2, 0, 4}, line4.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {4, 0, 2, 0, 4}, detailedLineNoAttachment4.getCells());

        DetailedReportRow line5 = report.getRow("L50000");
        assertArrayEquals(new int[] {6, 0, 0, 0, 7}, line5.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {6, 0, 0, 0, 7}, detailedLineNoAttachment5.getCells());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfDetailedClassificationReportWithCategoryFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .categoryIn(categories)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(2, report.rowSize());

        DetailedReportRow line1 = report.getRow("L30000");
        assertArrayEquals(new int[] {2, 1, 0, 1, 3}, line1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1a = line1.getDetailRows().get("L33000");
        assertArrayEquals(new int[] {0, 1, 0, 1, 1}, detailedLine1a.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1b = line1.getDetailRows().get("L99000");
        assertArrayEquals(new int[] {0, 0, 0, 0, 1}, detailedLine1b.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1WithoutAttachment = line1.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 0, 0, 0, 1}, detailedLine1WithoutAttachment.getCells());

        DetailedReportRow line2 = report.getRow("L40000");
        assertArrayEquals(new int[] {2, 2, 2, 0, 4}, line2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine2WithoutAttachment = line2.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 2, 2, 0, 4}, detailedLine2WithoutAttachment.getCells());

    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfDetailedClassificationReportWithDomainFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> domains = Collections.singletonList("DOMAIN_A");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(5, report.rowSize());

        DetailedReportRow line1 = report.getRow("L10000");
        assertArrayEquals(new int[] {5, 2, 1, 0, 0}, line1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1 = line1.getDetailRows().get("L11000");
        assertArrayEquals(new int[] {1, 0, 1, 0, 0}, detailedLine1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {4, 2, 0, 0, 0}, detailedLineNoAttachment1.getCells());

        DetailedReportRow line2 = report.getRow("L20000");
        assertArrayEquals(new int[] {3, 1, 1, 1, 0}, line2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine2 = line2.getDetailRows().get("L22000");
        assertArrayEquals(new int[] {1, 0, 1, 1, 0}, detailedLine2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 1, 0, 0, 0}, detailedLineNoAttachment2.getCells());

        DetailedReportRow line3 = report.getRow("L30000");
        assertArrayEquals(new int[] {1, 0, 0, 1, 1}, line3.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine3 = line3.getDetailRows().get("L33000");
        assertArrayEquals(new int[] {0, 0, 0, 1, 1}, detailedLine3.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {1, 0, 0, 0, 0}, detailedLineNoAttachment3.getCells());

        DetailedReportRow line4 = report.getRow("L40000");
        assertArrayEquals(new int[] {2, 0, 0, 0, 3}, line4.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {2, 0, 0, 0, 3}, detailedLineNoAttachment4.getCells());

        DetailedReportRow line5 = report.getRow("L50000");
        assertArrayEquals(new int[] {0, 1, 0, 3, 0}, line5.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {0, 1, 0, 3, 0}, detailedLineNoAttachment5.getCells());
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testEachItemOfDetailedClassificationReportWithCustomFieldValueFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

        DetailedClassificationReport report = taskMonitorService.createClassificationReportBuilder()
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(reportToString(report, columnHeaders));
        }

        assertNotNull(report);
        assertEquals(5, report.rowSize());

        DetailedReportRow line1 = report.getRow("L10000");
        assertArrayEquals(new int[] {4, 0, 0, 0, 0}, line1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine1 = line1.getDetailRows().get("L11000");
        assertArrayEquals(new int[] {1, 0, 0, 0, 0}, detailedLine1.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {3, 0, 0, 0, 0}, detailedLineNoAttachment1.getCells());

        DetailedReportRow line2 = report.getRow("L20000");
        assertArrayEquals(new int[] {4, 1, 1, 1, 0}, line2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine2 = line2.getDetailRows().get("L22000");
        assertArrayEquals(new int[] {1, 1, 1, 1, 0}, detailedLine2.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {3, 0, 0, 0, 0}, detailedLineNoAttachment2.getCells());

        DetailedReportRow line3 = report.getRow("L30000");
        assertArrayEquals(new int[] {1, 0, 0, 1, 1}, line3.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLine3a = line3.getDetailRows().get("L33000");
        assertArrayEquals(new int[] {0, 0, 0, 1, 0}, detailedLine3a.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {1, 0, 0, 0, 1}, detailedLineNoAttachment3.getCells());

        DetailedReportRow line4 = report.getRow("L40000");
        assertArrayEquals(new int[] {1, 1, 2, 0, 2}, line4.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {1, 1, 2, 0, 2}, detailedLineNoAttachment4.getCells());

        DetailedReportRow line5 = report.getRow("L50000");
        assertArrayEquals(new int[] {1, 2, 0, 2, 0}, line5.getCells());

        ReportRow<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getDetailRows().get("N/A");
        assertArrayEquals(new int[] {1, 2, 0, 2, 0}, detailedLineNoAttachment5.getCells());
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

    private String reportToString(DetailedClassificationReport report) {
        return reportToString(report, null);
    }

    private String reportToString(DetailedClassificationReport report, List<TimeIntervalColumnHeader> columnHeaders) {
        String formatColumWidth = "| %-7s ";
        String formatFirstColumn = "| %-36s  %-4s ";
        String formatFirstColumnFirstLine = "| %-29s %12s ";
        String formatFirstColumnDetailLines = "| + %-34s  %-4s ";
        String formatFirstColumnSumLine = "| %-36s  %-5s";
        int reportWidth = columnHeaders == null ? 46 : columnHeaders.size() * 10 + 46;

        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (int i = 0; i < reportWidth; i++) {
            builder.append("-");
        }
        builder.append("\n");
        builder.append(String.format(formatFirstColumnFirstLine, "Classifications + Attachments", "Total"));
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
            for (String detaileLine : report.getRow(rl).getDetailRows().keySet()) {
                ReportRow<DetailedMonitorQueryItem> reportLine = report.getRow(rl).getDetailRows().get(detaileLine);
                builder.append(
                    String.format(formatFirstColumnDetailLines, detaileLine, reportLine.getTotalValue()));
                for (int cell : reportLine.getCells()) {
                    builder.append(String.format(formatColumWidth, cell));
                }
                builder.append("|\n");
            }

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
