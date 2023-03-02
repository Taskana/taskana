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

  @TaskanaInject TaskanaConfiguration taskanaEngineConfiguration;

  @Test
  void should_CreateDataSource_For_TopLevelTestClass() {
    DataSource datasource = taskanaEngineConfiguration.getDataSource();

    assertThat(datasource).isNotNull();
  }

  @Test
  void should_CreateSchemaName_For_TopLevelTestClass() {
    String schemaName = taskanaEngineConfiguration.getSchemaName();

    assertThat(schemaName).isNotNull();
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClass {

    @TaskanaInject TaskanaConfiguration taskanaEngineConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClass() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDataSource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClass() {
      String nestedSchemaName = taskanaEngineConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isEqualTo(topLevelSchemaName);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithConfigurationModifier implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskanaConfiguration taskanaEngineConfiguration;

    @Override
    public TaskanaConfiguration.Builder modify(
        TaskanaConfiguration.Builder taskanaEngineConfigurationBuilder) {
      return taskanaEngineConfigurationBuilder;
    }

    @Test
    void should_ReuseDataSource_For_NestedTestClassWhichImplementsConfigurationModifier() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDataSource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClassWhichImplementsConfigurationModifier() {
      String nestedSchemaName = taskanaEngineConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isEqualTo(topLevelSchemaName);
    }
  }

  @CleanTaskanaContext
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassAnnotatedWithCleanTaskanaContext {

    @TaskanaInject TaskanaConfiguration taskanaEngineConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDataSource();

      assertThat(nestedDataSource).isNotNull().isSameAs(topLevelDataSource);
    }

    @Test
    void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      String nestedSchemaName = taskanaEngineConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isNotEqualTo(topLevelSchemaName);
    }

    @CleanTaskanaContext
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class NestedNestedTestClassAnnotatedWithCleanTaskanaContext {

      @TaskanaInject TaskanaConfiguration taskanaEngineConfiguration;

      @Test
      void should_ReuseDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
        DataSource nestedNestedDataSource = taskanaEngineConfiguration.getDataSource();
        DataSource nestedDataSource =
            NestedNestedTestClassAnnotatedWithCleanTaskanaContext.this.taskanaEngineConfiguration
                .getDataSource();
        DataSource topLevelDataSource =
            TestContainerExtensionTest.this.taskanaEngineConfiguration.getDataSource();

        assertThat(nestedNestedDataSource)
            .isNotNull()
            .isSameAs(topLevelDataSource)
            .isSameAs(nestedDataSource);
      }

      @Test
      void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanTaskanaContext() {
        String nestedNestedSchemaName = taskanaEngineConfiguration.getSchemaName();
        String nestedSchemaName =
            NestedTestClassAnnotatedWithCleanTaskanaContext.this.taskanaEngineConfiguration
                .getSchemaName();
        String topLevelSchemaName =
            TestContainerExtensionTest.this.taskanaEngineConfiguration.getSchemaName();

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

    @TaskanaInject TaskanaConfiguration taskanaEngineConfiguration;

    @Override
    public TaskanaConfiguration.Builder modify(
        TaskanaConfiguration.Builder taskanaEngineConfigurationBuilder) {
      return taskanaEngineConfigurationBuilder;
    }

    @Test
    void should_ReuseNewDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDataSource();

      assertThat(nestedDataSource).isNotNull().isSameAs(topLevelDataSource);
    }

    @Test
    void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      String nestedSchemaName = taskanaEngineConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isNotEqualTo(topLevelSchemaName);
    }
  }

  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = DummyPriorityServiceProvider.class)
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithServiceProvider {

    @TaskanaInject TaskanaConfiguration taskanaEngineConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClassWithServiceProvider() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDataSource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClassWithServiceProvider() {
      String nestedSchemaName = taskanaEngineConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getSchemaName();

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
