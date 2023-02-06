package acceptance.report;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.sampledata.SampleDataGenerator;

/** Abstract test class for all report building tests. */
public abstract class AbstractReportAccTest {

  protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
  protected static TaskanaEngine taskanaEngine;

  protected static void resetDb() throws Exception {
    DataSource dataSource = DataSourceGenerator.getDataSource();
    String schemaName = DataSourceGenerator.getSchemaName();
    taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(false);
    taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration);
    taskanaEngine.setConnectionManagementMode(TaskanaEngine.ConnectionManagementMode.AUTOCOMMIT);
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateMonitorData();
  }

  @BeforeAll
  static void setupTest() throws Exception {
    resetDb();
  }
}
