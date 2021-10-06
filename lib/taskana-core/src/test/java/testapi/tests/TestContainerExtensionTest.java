package testapi.tests;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.priorityservice.TestPriorityServiceProvider;
import javax.sql.DataSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import testapi.CleanTaskanaContext;
import testapi.TaskanaEngineConfigurationModifier;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;
import testapi.WithServiceProvider;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.spi.priority.api.PriorityServiceProvider;

@TaskanaIntegrationTest
class TestContainerExtensionTest {

  @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Test
  void should_CreateNewDataSource_For_TopLevelTestClass() {
    DataSource datasource = taskanaEngineConfiguration.getDatasource();

    assertThat(datasource).isNotNull();
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
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
  @TestInstance(Lifecycle.PER_CLASS)
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
  @TestInstance(Lifecycle.PER_CLASS)
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
  @TestInstance(Lifecycle.PER_CLASS)
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

  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = TestPriorityServiceProvider.class)
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithServiceProvider {
    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClassWithServiceProvider() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }
  }
}
