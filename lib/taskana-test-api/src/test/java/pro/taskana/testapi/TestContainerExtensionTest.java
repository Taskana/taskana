package pro.taskana.testapi;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.spi.priority.api.PriorityServiceProvider;

@TaskanaIntegrationTest
class TestContainerExtensionTest {

  @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Test
  void should_CreateDataSource_For_TopLevelTestClass() {
    DataSource datasource = taskanaEngineConfiguration.getDatasource();

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

    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClass() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

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

    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

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

      @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

      @Test
      void should_ReuseDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
        DataSource nestedNestedDataSource = taskanaEngineConfiguration.getDatasource();
        DataSource nestedDataSource =
            NestedNestedTestClassAnnotatedWithCleanTaskanaContext.this.taskanaEngineConfiguration
                .getDatasource();
        DataSource topLevelDataSource =
            TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

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

    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Override
    public void modify(TaskanaEngineConfiguration taskanaEngineConfiguration) {
      // do nothing
    }

    @Test
    void should_ReuseNewDataSource_For_NestedTestAnnotatedWithCleanTaskanaContext() {
      DataSource nestedDataSource = taskanaEngineConfiguration.getDatasource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getDatasource();

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

    @Test
    void should_ReuseSchemaName_For_NestedTestClassWithServiceProvider() {
      String nestedSchemaName = taskanaEngineConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.taskanaEngineConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isEqualTo(topLevelSchemaName);
    }
  }
}
