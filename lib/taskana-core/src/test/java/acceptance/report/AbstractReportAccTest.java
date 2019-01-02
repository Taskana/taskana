package acceptance.report;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.BeforeClass;

import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.database.TestDataGenerator;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;

/**
 * Abstract test class for all report building tests.
 */
public class AbstractReportAccTest {

    protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected static TaskanaEngine taskanaEngine;

    // checkstyle needs this constructor, since this is only a "utility" class
    protected AbstractReportAccTest() {
    }

    @BeforeClass
    public static void setupTest() throws Exception {
        resetDb();
    }

    private static void resetDb() throws SQLException, IOException {
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
}
