/*-
 * #%L
 * pro.taskana:taskana-test-api
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
package pro.taskana.testapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.OptionalInt;
import javax.sql.DataSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.testapi.TestContainerExtensionTest.NestedTestClassWithServiceProvider.DummyPriorityServiceProvider;

@TaskanaIntegrationTest
class TestContainerExtensionTest {

  @TaskanaInject TaskanaConfiguration taskanaConfiguration;

  @Test
  void should_CreateDataSource_For_TopLevelTestClass() {
    DataSource datasource = taskanaConfiguration.getDatasource();

    assertThat(datasource).isNotNull();
  }

  @Test
  void should_CreateSchemaName_For_TopLevelTestClass() {
    String schemaName = taskanaConfiguration.getSchemaName();

    assertThat(schemaName).isNotNull();
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClass {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClass() {
      DataSource nestedDataSource = taskanaConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaConfiguration.getDatasource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClass() {
      String nestedSchemaName = taskanaConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isEqualTo(topLevelSchemaName);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithConfigurationModifier implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;

    @Override
    public TaskanaConfiguration.Builder modify(
        TaskanaConfiguration.Builder taskanaConfigurationBuilder) {
      return taskanaConfigurationBuilder;
    }

    @Test
    void should_ReuseDataSource_For_NestedTestClassWhichImplementsConfigurationModifier() {
      DataSource nestedDataSource = taskanaConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaConfiguration.getDatasource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClassWhichImplementsConfigurationModifier() {
      String nestedSchemaName = taskanaConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isEqualTo(topLevelSchemaName);
    }
  }

  @CleanTaskanaContext
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassAnnotatedWithCleanTaskanaContext {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      DataSource nestedDataSource = taskanaConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaConfiguration.getDatasource();

      assertThat(nestedDataSource).isNotNull().isSameAs(topLevelDataSource);
    }

    @Test
    void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      String nestedSchemaName = taskanaConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isNotEqualTo(topLevelSchemaName);
    }

    @CleanTaskanaContext
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class NestedNestedTestClassAnnotatedWithCleanTaskanaContext {

      @TaskanaInject TaskanaConfiguration taskanaConfiguration;

      @Test
      void should_ReuseDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
        DataSource nestedNestedDataSource = taskanaConfiguration.getDatasource();
        DataSource nestedDataSource =
            NestedNestedTestClassAnnotatedWithCleanTaskanaContext.this.taskanaConfiguration
                .getDatasource();
        DataSource topLevelDataSource =
            TestContainerExtensionTest.this.taskanaConfiguration.getDatasource();

        assertThat(nestedNestedDataSource)
            .isNotNull()
            .isSameAs(topLevelDataSource)
            .isSameAs(nestedDataSource);
      }

      @Test
      void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanTaskanaContext() {
        String nestedNestedSchemaName = taskanaConfiguration.getSchemaName();
        String nestedSchemaName =
            NestedTestClassAnnotatedWithCleanTaskanaContext.this.taskanaConfiguration
                .getSchemaName();
        String topLevelSchemaName =
            TestContainerExtensionTest.this.taskanaConfiguration.getSchemaName();

        assertThat(nestedSchemaName).isNotNull().isNotEqualTo(topLevelSchemaName);

        assertThat(nestedNestedSchemaName)
            .isNotNull()
            .isNotEqualTo(nestedSchemaName)
            .isNotEqualTo(topLevelSchemaName);
      }
    }
  }

  @CleanTaskanaContext
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassAnnotatedWithCleanTaskanaContextAndConfigModifier
      implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;

    @Override
    public TaskanaConfiguration.Builder modify(
        TaskanaConfiguration.Builder taskanaConfigurationBuilder) {
      return taskanaConfigurationBuilder;
    }

    @Test
    void should_ReuseNewDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      DataSource nestedDataSource = taskanaConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaConfiguration.getDatasource();

      assertThat(nestedDataSource).isNotNull().isSameAs(topLevelDataSource);
    }

    @Test
    void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      String nestedSchemaName = taskanaConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isNotEqualTo(topLevelSchemaName);
    }
  }

  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = DummyPriorityServiceProvider.class)
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithServiceProvider {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClassWithServiceProvider() {
      DataSource nestedDataSource = taskanaConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaConfiguration.getDatasource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClassWithServiceProvider() {
      String nestedSchemaName = taskanaConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isEqualTo(topLevelSchemaName);
    }

    class DummyPriorityServiceProvider implements PriorityServiceProvider {
      @Override
      public OptionalInt calculatePriority(TaskSummary taskSummary) {
        // implementation not important for the tests
        return OptionalInt.empty();
      }
    }
  }
}
