/*-
 * #%L
 * pro.taskana.history:taskana-simplehistory-provider
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
package configuration;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.config.DataSourceGenerator;

/** Unit Test for TaskanaEngineConfigurationTest. */
class TaskanaEngineConfigurationTest extends AbstractAccTest {

  @Test
  void testCreateTaskanaEngine() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), false)
            .initTaskanaProperties()
            .build();

    TaskanaEngine te = TaskanaEngine.buildTaskanaEngine(taskEngineConfiguration);

    assertThat(te).isNotNull();
  }

  @Test
  void testCreateTaskanaHistoryEventWithNonDefaultSchemaName() throws Exception {
    resetDb("SOMECUSTOMSCHEMANAME");
    long count = getHistoryService().createTaskHistoryQuery().workbasketKeyIn("wbKey1").count();
    assertThat(count).isZero();
    getHistoryService()
        .create(
            AbstractAccTest.createTaskHistoryEvent(
                "wbKey1", "taskId1", "type1", "Some comment", "wbKey2", "someUserId"));
    count = getHistoryService().createTaskHistoryQuery().workbasketKeyIn("wbKey1").count();
    assertThat(count).isOne();
  }
}
