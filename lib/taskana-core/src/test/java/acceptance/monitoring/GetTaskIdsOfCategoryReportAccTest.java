package acceptance.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.database.TestDataGenerator;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.ReportLineItemDefinition;
import pro.taskana.impl.SelectedItem;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;

/**
 * Acceptance test for all "get task ids of category report" scenarios.
 */
public class GetTaskIdsOfCategoryReportAccTest {

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
        ((TaskanaEngineImpl) taskanaEngine).setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        cleaner.clearDb(dataSource, false);
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        testDataGenerator.generateMonitoringTestData(dataSource);
    }

    @Test
    public void testGetTaskIdsOfCategoryReport() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("EXTERN");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("AUTOMATIC");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("MANUAL");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.getTaskIdsOfCategoryReportLineItems(null, null, null, null, null, null,
            reportLineItemDefinitions, selectedItems);

        assertEquals(11, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000021"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000022"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000023"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000026"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000028"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000032"));
    }

    @Test
    public void testGetTaskIdsOfCategoryReportWithWorkbasketFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> workbasketIds = Arrays.asList("WBI:000000000000000000000000000000000001");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("EXTERN");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("AUTOMATIC");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("MANUAL");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.getTaskIdsOfCategoryReportLineItems(workbasketIds, null, null, null, null,
            null, reportLineItemDefinitions, selectedItems);

        assertEquals(4, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000026"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
    }

    @Test
    public void testGetTaskIdsOfCategoryReportWithStateFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<TaskState> states = Arrays.asList(TaskState.READY);
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("EXTERN");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("AUTOMATIC");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("MANUAL");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.getTaskIdsOfCategoryReportLineItems(null, states, null, null, null, null,
            reportLineItemDefinitions, selectedItems);

        assertEquals(11, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000021"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000022"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000023"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000026"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000028"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000032"));
    }

    @Test
    public void testGetTaskIdsOfCategoryReportWithCategoryFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("AUTOMATIC");
        s1.setLowerAgeLimit(Integer.MIN_VALUE);
        s1.setUpperAgeLimit(-11);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("MANUAL");
        s2.setLowerAgeLimit(0);
        s2.setUpperAgeLimit(0);
        selectedItems.add(s2);

        List<String> ids = taskMonitorService.getTaskIdsOfCategoryReportLineItems(null, null, categories, null, null,
            null, reportLineItemDefinitions, selectedItems);

        assertEquals(3, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000032"));
    }

    @Test
    public void testGetTaskIdsOfCategoryReportWithDomainFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> domains = Arrays.asList("DOMAIN_A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("EXTERN");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("AUTOMATIC");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("MANUAL");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.getTaskIdsOfCategoryReportLineItems(null, null, null, domains, null, null,
            reportLineItemDefinitions, selectedItems);

        assertEquals(4, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000021"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000022"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000028"));
    }

    @Test
    public void testGetTaskIdsOfCategoryReportWithCustomFieldValueFilter() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        CustomField customField = CustomField.CUSTOM_1;
        List<String> customFieldValues = Arrays.asList("Geschaeftsstelle A");
        List<ReportLineItemDefinition> reportLineItemDefinitions = getListOfReportLineItemDefinitions();

        List<SelectedItem> selectedItems = new ArrayList<>();

        SelectedItem s1 = new SelectedItem();
        s1.setKey("EXTERN");
        s1.setLowerAgeLimit(-5);
        s1.setUpperAgeLimit(-2);
        selectedItems.add(s1);

        SelectedItem s2 = new SelectedItem();
        s2.setKey("AUTOMATIC");
        s2.setLowerAgeLimit(Integer.MIN_VALUE);
        s2.setUpperAgeLimit(-11);
        selectedItems.add(s2);

        SelectedItem s3 = new SelectedItem();
        s3.setKey("MANUAL");
        s3.setLowerAgeLimit(0);
        s3.setUpperAgeLimit(0);
        selectedItems.add(s3);

        List<String> ids = taskMonitorService.getTaskIdsOfCategoryReportLineItems(null, null, null, null, customField,
            customFieldValues, reportLineItemDefinitions, selectedItems);

        assertEquals(5, ids.size());
        assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
        assertTrue(ids.contains("TKI:000000000000000000000000000000000032"));
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

}
