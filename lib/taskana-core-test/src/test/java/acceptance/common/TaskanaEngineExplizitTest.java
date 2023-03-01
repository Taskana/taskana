package acceptance.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
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

    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(
                TestContainerExtension.DATA_SOURCE, false, schemaName, true)
            .initTaskanaProperties()
            .build();

    TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.EXPLICIT);

    DbSchemaCreator dsc =
        new DbSchemaCreator(
            taskanaConfiguration.getDatasource(), taskanaConfiguration.getSchemaName());
    assertThat(dsc.isValidSchemaVersion(TaskanaEngine.MINIMAL_TASKANA_SCHEMA_VERSION)).isTrue();
  }
}
