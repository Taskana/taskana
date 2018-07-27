package acceptance.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
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
 * Acceptance test for all "get task ids of category report" scenarios.
 */
@RunWith(JAASRunner.class)
public class GetTaskIdsOfCustomFieldValueReportAccTest {

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

        taskMonitorService.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .listTaskIdsForSelectedItems(selectedItems);
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfCustomFieldValueReport() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("Geschaeftsstelle A");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("Geschaeftsstelle B");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("Geschaeftsstelle C");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(8, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000002"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000009"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000029"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000033"));
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfCustomFieldValueReportWithWorkbasketFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> workbasketIds = Collections.singletonList("WBI:000000000000000000000000000000000001");
        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("Geschaeftsstelle A");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("Geschaeftsstelle B");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("Geschaeftsstelle C");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(3, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000009"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfCustomFieldValueReportWithStateFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("Geschaeftsstelle A");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("Geschaeftsstelle B");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("Geschaeftsstelle C");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .stateIn(Collections.singletonList(TaskState.READY))
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(8, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000002"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000009"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000029"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000033"));
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfCustomFieldValueReportWithCategoryFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("Geschaeftsstelle A");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("Geschaeftsstelle B");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("Geschaeftsstelle C");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .categoryIn(categories)
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(3, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000009"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000029"));
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfCustomFieldValueReportWithDomainFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("Geschaeftsstelle A");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("Geschaeftsstelle B");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("Geschaeftsstelle C");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(Collections.singletonList("DOMAIN_A"))
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(3, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000009"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000033"));
    }

    @WithAccessId(
        userName = "monitor")
    @Test
    public void testGetTaskIdsOfCustomFieldValueReportWithCustomFieldValueFilter()
        throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        Map<CustomField, String> customAttributeFilter = new HashMap<>();
        customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("Geschaeftsstelle A");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("Geschaeftsstelle B");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("Geschaeftsstelle C");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .customAttributeFilterIn(customAttributeFilter)
            .listTaskIdsForSelectedItems(selectedItems);

        assertEquals(4, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000029"));
    }

    @WithAccessId(
        userName = "monitor")
    @Test(expected = InvalidArgumentException.class)
    public void testThrowsExceptionIfSubKeysAreUsed() throws InvalidArgumentException, NotAuthorizedException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("Geschaeftsstelle A");
        s1.setSubKey("INVALID");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        taskMonitorService.createCategoryReportBuilder().withColumnHeaders(columnHeaders).listTaskIdsForSelectedItems(
            selectedItems);
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
