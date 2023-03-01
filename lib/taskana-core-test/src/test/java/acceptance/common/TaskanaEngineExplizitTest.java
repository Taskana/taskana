/*-
 * #%L
 * pro.taskana:taskana-core-test
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
