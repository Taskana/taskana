package acceptance;

import javax.sql.DataSource;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.test.config.DataSourceGenerator;

public class FooBar {
  public static TaskanaEngine getTaskanaEngineForTests() throws Exception {
    DataSource dataSource = DataSourceGenerator.getDataSource();
    String schemaName = DataSourceGenerator.getSchemaName();
    TaskanaEngineConfiguration taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(true);
    TaskanaEngine taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    return taskanaEngine;
  }
}
