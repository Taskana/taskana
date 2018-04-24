package acceptance.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.database.TestDataGenerator;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;

/**
 * Acceptance test for all "classification report" scenarios.
 */
public class GetCustomAttributeValuesForReportAcctest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCustomAttributeValuesForReportAcctest.class);
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
    public void testGetCustomAttributeValuesForOneWorkbasket() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> values = taskMonitorService.getCustomAttributeValuesForReport(
            Collections.singletonList("WBI:000000000000000000000000000000000001"), null,
            null, null, null, null, null,
            "2");

        assertNotNull(values);
        assertEquals(2, values.size());
        assertTrue(values.contains("Vollkasko"));
        assertTrue(values.contains("Teilkasko"));
    }

    @Test
    public void testGetCustomAttributeValuesForOneDomain() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> values = taskMonitorService.getCustomAttributeValuesForReport(
            null, null,
            null, Collections.singletonList("DOMAIN_A"), null, null, null,
            "16");

        assertNotNull(values);
        assertEquals(26, values.size());
    }

    @Test
    public void testGetCustomAttributeValuesForCustomAttribute() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        Map<String, String> props = new HashMap<>();
        props.put("2", "Vollkasko");
        props.put("1", "Geschaeftsstelle A");
        List<String> values = taskMonitorService.getCustomAttributeValuesForReport(
            null, null,
            null, null, null, null, props,
            "16");

        assertNotNull(values);
        assertEquals(12, values.size());
    }

    @Test
    public void testGetCustomAttributeValuesForExcludedClassifications() throws InvalidArgumentException {
        TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

        List<String> domains = new ArrayList<>();
        domains.add("DOMAIN_A");
        domains.add("DOMAIN_B");
        domains.add("DOMAIN_C");
        List<String> values = taskMonitorService.getCustomAttributeValuesForReport(
            null, null,
            null, domains, null, Collections.singletonList("CLI:000000000000000000000000000000000003"), null,
            "16");

        assertNotNull(values);
        assertEquals(43, values.size());
    }

}
