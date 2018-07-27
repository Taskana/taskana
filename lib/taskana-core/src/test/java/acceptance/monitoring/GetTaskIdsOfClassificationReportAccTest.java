package acceptance.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.database.TestDataGenerator;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.SelectedItem;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "get task ids of classification report" scenarios.
 */
@RunWith(JAASRunner.class)
public class GetTaskIdsOfClassificationReportAccTest {

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

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("L10000");
        s1.setLowerAgeLimit(0);
        s1.setUpperAgeLimit(0);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("L10000");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("L30000");
        s3.setLowerAgeLimit(Integer.MIN_VALUE);
        s3.setUpperAgeLimit(-11);
        selectedItems.add(s3);

        taskMonitorService.createClassificationReportBuilder().listTaskIdsForSelectedItems(selectedItems);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfClassificationReport() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("L10000");
        s1.setLowerAgeLimit(0);
        s1.setUpperAgeLimit(0);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("L10000");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("L30000");
        s3.setLowerAgeLimit(Integer.MIN_VALUE);
        s3.setUpperAgeLimit(-11);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(6, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000001"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000004"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000007"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000010"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000033"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfClassificationReportWithAttachments()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("L10000");
        s1.setSubKey("L11000");
        s1.setLowerAgeLimit(0);
        s1.setUpperAgeLimit(0);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("L10000");
        s2.setSubKey("L11000");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("L30000");
        s3.setLowerAgeLimit(Integer.MIN_VALUE);
        s3.setUpperAgeLimit(-11);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(2, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000001"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000033"));
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfClassificationReportWithDomainFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("L10000");
        s1.setLowerAgeLimit(0);
        s1.setUpperAgeLimit(0);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("L10000");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("L30000");
        s3.setLowerAgeLimit(Integer.MIN_VALUE);
        s3.setUpperAgeLimit(-11);
        selectedItems.add(s3);

        List<String> domains = new ArrayList<>();
        domains.add("DOMAIN_B");
        domains.add("DOMAIN_C");

        List<String> ids = taskMonitorService.createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(domains)
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(3, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000001"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000004"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
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

}
