package acceptance;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.common.test.config.TaskanaEngineTestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.models.ObjectReference;

public abstract class AbstractAccTest {

  protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
  protected static TaskanaEngine taskanaEngine;
  protected static WorkingDaysToDaysConverter converter;

  @BeforeAll
  protected static void setupTest() throws Exception {
    resetDb(false);
  }

  protected static void resetDb(boolean dropTables) throws Exception {

    DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    if (dropTables) {
      sampleDataGenerator.dropDb();
    }
    dataSource = TaskanaEngineTestConfiguration.getDataSource();
    taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(true);
    DbSchemaCreator dbSchemaCreator =
        new DbSchemaCreator(dataSource, taskanaEngineConfiguration.getSchemaName());
    dbSchemaCreator.run();
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
    taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    converter = taskanaEngine.getWorkingDaysToDaysConverter();
  }

  protected ObjectReference createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany(company);
    objectReference.setSystem(system);
    objectReference.setSystemInstance(systemInstance);
    objectReference.setType(type);
    objectReference.setValue(value);
    return objectReference;
  }
}
