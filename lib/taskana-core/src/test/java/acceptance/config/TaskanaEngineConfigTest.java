/*-
 * #%L
 * pro.taskana:taskana-core
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
package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.config.DataSourceGenerator;

class TaskanaEngineConfigTest {

  @Test
  void should_ReturnTaskanaEngine_When_BuildingWithConfiguration() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName())
            .initTaskanaProperties()
            .build();

    TaskanaEngine te = TaskanaEngine.buildTaskanaEngine(taskEngineConfiguration);

    assertThat(te).isNotNull();
  }

  @Test
  void should_SetCorpusChristiEnabled_When_PropertyIsSet() {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), true)
            .initTaskanaProperties("/corpusChristiEnabled.properties", "|")
            .build();

    assertThat(taskEngineConfiguration.isCorpusChristiEnabled()).isTrue();
  }

  @Test
  void should_ReturnTheTwoCustomHolidays_When_TwoCustomHolidaysAreConfiguredInThePropertiesFile() {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), true)
            .initTaskanaProperties("/custom_holiday_taskana.properties", "|")
            .build();
    assertThat(taskEngineConfiguration.getCustomHolidays())
        .contains(CustomHoliday.of(31, 7), CustomHoliday.of(16, 12));
  }

  @Test
  void should_ReturnEmptyList_When_AllCustomHolidaysAreInWrongFormatInPropertiesFile() {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), true)
            .initTaskanaProperties("/custom_holiday_with_wrong_format_taskana.properties", "|")
            .build();
    assertThat(taskEngineConfiguration.getCustomHolidays()).isEmpty();
  }
}
