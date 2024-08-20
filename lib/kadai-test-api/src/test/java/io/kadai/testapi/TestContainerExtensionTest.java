package io.kadai.testapi;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.spi.priority.api.PriorityServiceProvider;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.testapi.TestContainerExtensionTest.NestedTestClassWithServiceProvider.DummyPriorityServiceProvider;
import java.util.OptionalInt;
import javax.sql.DataSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@KadaiIntegrationTest
class TestContainerExtensionTest {

  @KadaiInject KadaiConfiguration kadaiConfiguration;

  @Test
  void should_CreateDataSource_For_TopLevelTestClass() {
    DataSource datasource = kadaiConfiguration.getDataSource();

    assertThat(datasource).isNotNull();
  }

  @Test
  void should_CreateSchemaName_For_TopLevelTestClass() {
    String schemaName = kadaiConfiguration.getSchemaName();

    assertThat(schemaName).isNotNull();
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClass {

    @KadaiInject KadaiConfiguration kadaiConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClass() {
      DataSource nestedDataSource = kadaiConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.kadaiConfiguration.getDataSource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClass() {
      String nestedSchemaName = kadaiConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.kadaiConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isEqualTo(topLevelSchemaName);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithConfigurationModifier implements KadaiConfigurationModifier {

    @KadaiInject KadaiConfiguration kadaiConfiguration;

    @Override
    public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
      return builder;
    }

    @Test
    void should_ReuseDataSource_For_NestedTestClassWhichImplementsConfigurationModifier() {
      DataSource nestedDataSource = kadaiConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.kadaiConfiguration.getDataSource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClassWhichImplementsConfigurationModifier() {
      String nestedSchemaName = kadaiConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.kadaiConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isEqualTo(topLevelSchemaName);
    }
  }

  @CleanKadaiContext
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassAnnotatedWithCleanKadaiContext {

    @KadaiInject KadaiConfiguration kadaiConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestAnnotatedWithCleanKadaiContext() {
      DataSource nestedDataSource = kadaiConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.kadaiConfiguration.getDataSource();

      assertThat(nestedDataSource).isNotNull().isSameAs(topLevelDataSource);
    }

    @Test
    void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanKadaiContext() {
      String nestedSchemaName = kadaiConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.kadaiConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isNotEqualTo(topLevelSchemaName);
    }

    @CleanKadaiContext
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class NestedNestedTestClassAnnotatedWithCleanKadaiContext {

      @KadaiInject KadaiConfiguration kadaiConfiguration;

      @Test
      void should_ReuseDataSource_For_NestedTestAnnotatedWithCleanKadaiContext() {
        DataSource nestedNestedDataSource = kadaiConfiguration.getDataSource();
        DataSource nestedDataSource =
            NestedNestedTestClassAnnotatedWithCleanKadaiContext.this.kadaiConfiguration
                .getDataSource();
        DataSource topLevelDataSource =
            TestContainerExtensionTest.this.kadaiConfiguration.getDataSource();

        assertThat(nestedNestedDataSource)
            .isNotNull()
            .isSameAs(topLevelDataSource)
            .isSameAs(nestedDataSource);
      }

      @Test
      void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanKadaiContext() {
        String nestedNestedSchemaName = kadaiConfiguration.getSchemaName();
        String nestedSchemaName =
            NestedTestClassAnnotatedWithCleanKadaiContext.this.kadaiConfiguration.getSchemaName();
        String topLevelSchemaName =
            TestContainerExtensionTest.this.kadaiConfiguration.getSchemaName();

        assertThat(nestedSchemaName).isNotNull().isNotEqualTo(topLevelSchemaName);

        assertThat(nestedNestedSchemaName)
            .isNotNull()
            .isNotEqualTo(nestedSchemaName)
            .isNotEqualTo(topLevelSchemaName);
      }
    }
  }

  @CleanKadaiContext
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassAnnotatedWithCleanKadaiContextAndConfigModifier
      implements KadaiConfigurationModifier {

    @KadaiInject KadaiConfiguration kadaiConfiguration;

    @Override
    public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
      return builder;
    }

    @Test
    void should_ReuseNewDataSource_For_NestedTestAnnotatedWithCleanKadaiContext() {
      DataSource nestedDataSource = kadaiConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.kadaiConfiguration.getDataSource();

      assertThat(nestedDataSource).isNotNull().isSameAs(topLevelDataSource);
    }

    @Test
    void should_GenerateNewSchemaName_For_NestedTestAnnotatedWithCleanKadaiContext() {
      String nestedSchemaName = kadaiConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.kadaiConfiguration.getSchemaName();

      assertThat(nestedSchemaName).isNotNull().isNotEqualTo(topLevelSchemaName);
    }
  }

  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = DummyPriorityServiceProvider.class)
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithServiceProvider {

    @KadaiInject KadaiConfiguration kadaiConfiguration;

    @Test
    void should_ReuseDataSource_For_NestedTestClassWithServiceProvider() {
      DataSource nestedDataSource = kadaiConfiguration.getDataSource();
      DataSource topLevelDataSource =
          TestContainerExtensionTest.this.kadaiConfiguration.getDataSource();

      assertThat(nestedDataSource).isSameAs(topLevelDataSource).isNotNull();
    }

    @Test
    void should_ReuseSchemaName_For_NestedTestClassWithServiceProvider() {
      String nestedSchemaName = kadaiConfiguration.getSchemaName();
      String topLevelSchemaName =
          TestContainerExtensionTest.this.kadaiConfiguration.getSchemaName();

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
