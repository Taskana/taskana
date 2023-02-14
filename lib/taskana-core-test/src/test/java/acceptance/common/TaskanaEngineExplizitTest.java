package acceptance.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.testapi.OracleSchemaHelper;
import pro.taskana.testapi.extensions.TestContainerExtension;

class TaskanaEngineExplizitTest {

  @Test
  void should_CreateTaskanaEnine_When_ExplizitModeIsActive() throws Exception {

    String schemaName = TestContainerExtension.determineSchemaName();
    if (DB.isOracle(TestContainerExtension.EXECUTION_DATABASE.dbProductId)) {
      OracleSchemaHelper.initOracleSchema(TestContainerExtension.DATA_SOURCE, schemaName);
    }

    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(
                TestContainerExtension.DATA_SOURCE, false, schemaName, true)
            .initTaskanaProperties()
            .build();

    TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration, ConnectionManagementMode.EXPLICIT);

    DbSchemaCreator dsc =
        new DbSchemaCreator(
            taskanaEngineConfiguration.getDataSource(), taskanaEngineConfiguration.getSchemaName());
    assertThat(dsc.isValidSchemaVersion(TaskanaEngineImpl.MINIMAL_TASKANA_SCHEMA_VERSION)).isTrue();
  }
}
