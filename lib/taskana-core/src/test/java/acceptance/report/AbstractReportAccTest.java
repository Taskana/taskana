package acceptance.report;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;

import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.impl.configuration.TaskanaEngineTestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;

/**
 * Abstract test class for all report building tests.
 */
public class AbstractReportAccTest {

    protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected static TaskanaEngine taskanaEngine;

    // checkstyle needs this constructor, since this is only a "utility" class
    protected AbstractReportAccTest() {
    }

    @BeforeAll
    public static void setupTest() throws Exception {
        resetDb();
    }

    private static void resetDb() throws SQLException {
        DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
        String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false,
            schemaName);
        taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngine.setConnectionManagementMode(TaskanaEngine.ConnectionManagementMode.AUTOCOMMIT);
        sampleDataGenerator.clearDb();
        sampleDataGenerator.generateMonitorData();
    }
}
