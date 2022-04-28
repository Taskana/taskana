package pro.taskana.testapi.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.testapi.CleanTaskanaContext;
import pro.taskana.testapi.TaskanaEngineConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;

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
    void should_CreateNewDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext()
        throws Exception {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();
      String nestedDataSourceUrl;
      String topLevelDataSourceUrl;
      try (Connection connection = nestedDataSource.getConnection()) {
        nestedDataSourceUrl = connection.getMetaData().getURL();
      }
      try (Connection connection = topLevelDataSource.getConnection()) {
        topLevelDataSourceUrl = connection.getMetaData().getURL();
      }

      assertThat(nestedDataSourceUrl).isNotEqualTo(topLevelDataSourceUrl).isNotNull();
    }

    @CleanTaskanaContext
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class NestedNestedTestClassAnnotatedWithCleanTaskanaContext {

      @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

      @Test
      void should_CreateNewDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext()
          throws Exception {
        DataSource nestedNestedDataSource = taskanaEngineConfiguration.getDatasource();
        DataSource nestedDataSource =
            NestedTestClassAnnotatedWithCleanTaskanaContext.this.taskanaEngineConfiguration
                .getDatasource();
        DataSource topLevelDataSource =
            TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();
        String nestedNestedDataSourceUrl;
        String nestedDataSourceUrl;
        String topLevelDataSourceUrl;
        try (Connection connection = nestedNestedDataSource.getConnection()) {
          nestedNestedDataSourceUrl = connection.getMetaData().getURL();
        }
        try (Connection connection = nestedDataSource.getConnection()) {
          nestedDataSourceUrl = connection.getMetaData().getURL();
        }
        try (Connection connection = topLevelDataSource.getConnection()) {
          topLevelDataSourceUrl = connection.getMetaData().getURL();
        }

        assertThat(nestedNestedDataSourceUrl)
            .isNotEqualTo(nestedDataSourceUrl)
            .isNotEqualTo(topLevelDataSourceUrl)
            .isNotNull();
      }
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
