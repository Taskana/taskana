package pro.taskana;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

public abstract class AbstractAccTest {

  protected static TaskanaConfiguration taskanaConfiguration;
  protected static TaskanaEngine taskanaEngine;

  @BeforeAll
  protected static void setupTest() throws Exception {
    resetDb(false);
  }

  protected static void resetDb(boolean dropTables) throws Exception {

    DataSource dataSource = DataSourceGenerator.getDataSource();
    String schemaName = DataSourceGenerator.getSchemaName();
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    if (dropTables) {
      sampleDataGenerator.dropDb();
    }
    dataSource = DataSourceGenerator.getDataSource();
    taskanaConfiguration =
        new TaskanaConfiguration.Builder(dataSource, false, schemaName)
            .initTaskanaProperties()
            .germanPublicHolidaysEnabled(true)
            .build();
    DbSchemaCreator dbSchemaCreator =
        new DbSchemaCreator(dataSource, taskanaConfiguration.getSchemaName());
    dbSchemaCreator.run();
    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
    taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
  }

  protected ObjectReference createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReferenceImpl objectReference = new ObjectReferenceImpl();
    objectReference.setCompany(company);
    objectReference.setSystem(system);
    objectReference.setSystemInstance(systemInstance);
    objectReference.setType(type);
    objectReference.setValue(value);
    return objectReference;
  }
}
