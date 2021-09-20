package testapi.tests;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import testapi.CleanTaskanaContext;
import testapi.TaskanaEngineConfigurationModifier;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;

import pro.taskana.TaskanaEngineConfiguration;

@TaskanaIntegrationTest
class TestContainerExtensionTest {

  @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Test
  void should_CreateNewDataSource_For_TopLevelTestClass() {
    DataSource datasource = taskanaEngineConfiguration.getDatasource();

    assertThat(datasource).isNotNull();
  }

  @Nested
  class NestedTestClass {
    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClass() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }
  }

  @Nested
  class NestedTestClassWithConfigurationModifier implements TaskanaEngineConfigurationModifier {
    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Override
    public void modify(TaskanaEngineConfiguration taskanaEngineConfiguration) {
      // do nothing
    }

    @Test
    void should_ReuseDataSource_For_NestedTestClassWhichImplementsConfigurationModifier() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }
  }

  @CleanTaskanaContext
  @Nested
  class NestedTestClassAnnotatedWithCleanTaskanaContext {
    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Test
    void should_CreateNewDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

      assertThat(nestedDataSource).isNotSameAs(topLevelDataSource).isNotNull();
    }
  }

  @CleanTaskanaContext
  @Nested
  class NestedTestClassAnnotatedWithCleanTaskanaContextAndConfigModifier
      implements TaskanaEngineConfigurationModifier {
    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Override
    public void modify(TaskanaEngineConfiguration taskanaEngineConfiguration) {
      // do nothing
    }

    @Test
    void should_CreateNewDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

      assertThat(nestedDataSource).isNotSameAs(topLevelDataSource).isNotNull();
    }
  }
}
