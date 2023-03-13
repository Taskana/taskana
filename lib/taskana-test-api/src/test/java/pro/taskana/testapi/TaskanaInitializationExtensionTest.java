package pro.taskana.testapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.testapi.TaskanaInitializationExtensionTest.NestedTestClassWithServiceProvider.DummyPriorityServiceProvider;

@TaskanaIntegrationTest
class TaskanaInitializationExtensionTest {

  @TaskanaInject TaskanaConfiguration taskanaConfiguration;

  @Test
  void should_UseDefaultTaskanaEngine_When_TestIsCreated() {
    assertThat(taskanaConfiguration.getDomains()).containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ReuseTaskana {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;

    @Test
    void should_useTopLevelTaskanaInstance_For_NestedTestClasses() {
      assertThat(taskanaConfiguration)
          .isSameAs(TaskanaInitializationExtensionTest.this.taskanaConfiguration);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ModifiedTaskanaConfig implements TaskanaConfigurationModifier {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;

    @Override
    public TaskanaConfiguration.Builder modify(TaskanaConfiguration.Builder builder) {
      return builder.domains(List.of("A", "B"));
    }

    @Test
    void should_OverrideConfiguration_When_ClassImplementsTaskanaConfigurationModifier() {
      assertThat(taskanaConfiguration.getDomains()).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void should_createNewTaskanaInstance_For_NestedTestClassImplementingModifierInterface() {
      assertThat(taskanaConfiguration)
          .isNotSameAs(TaskanaInitializationExtensionTest.this.taskanaConfiguration);
    }
  }

  @CleanTaskanaContext
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassAnnotatedWithCleanTaskanaContext {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;

    @Test
    void should_createNewTaskanaInstance_For_NestedTestClassAnnotatedWithCleanTaskanaContext() {
      assertThat(taskanaConfiguration)
          .isNotSameAs(TaskanaInitializationExtensionTest.this.taskanaConfiguration);
    }

    @Test
    void should_UseDefaultTaskanaEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(taskanaConfiguration.getDomains())
          .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
    }
  }

  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = DummyPriorityServiceProvider.class)
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithServiceProvider {
    @TaskanaInject TaskanaConfiguration taskanaConfiguration;
    @TaskanaInject TaskanaEngine taskanaEngine;

    @Test
    void should_LoadServiceProviders() throws Exception {
      PriorityServiceManager priorityServiceManager =
          new TaskanaEngineProxy(taskanaEngine).getEngine().getPriorityServiceManager();

      assertThat(priorityServiceManager.isEnabled()).isTrue();
    }

    @Test
    void should_createNewTaskanaInstance_For_NestedTestClassAnnotatedWithCleanTaskanaContext() {
      assertThat(taskanaConfiguration)
          .isNotSameAs(TaskanaInitializationExtensionTest.this.taskanaConfiguration);
    }

    @Test
    void should_UseDefaultTaskanaEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(taskanaConfiguration.getDomains())
          .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
    }

    class DummyPriorityServiceProvider implements PriorityServiceProvider {
      @Override
      public OptionalInt calculatePriority(TaskSummary taskSummary) {
        // implementation not important for the tests
        return OptionalInt.empty();
      }
    }
  }

  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders =
          NestedTestClassWithMultipleServiceProviders.DummyPriorityServiceProvider.class)
  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders =
          NestedTestClassWithMultipleServiceProviders.DummyPriorityServiceProvider.class)
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithMultipleServiceProviders {
    @TaskanaInject TaskanaConfiguration taskanaConfiguration;
    @TaskanaInject TaskanaEngine taskanaEngine;

    @Test
    void should_LoadServiceProviders() throws Exception {
      PriorityServiceManager priorityServiceManager =
          new TaskanaEngineProxy(taskanaEngine).getEngine().getPriorityServiceManager();

      assertThat(priorityServiceManager.isEnabled()).isTrue();
    }

    @Test
    void should_createNewTaskanaInstance_For_NestedTestClassAnnotatedWithCleanTaskanaContext() {
      assertThat(taskanaConfiguration)
          .isNotSameAs(TaskanaInitializationExtensionTest.this.taskanaConfiguration);
    }

    @Test
    void should_UseDefaultTaskanaEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(taskanaConfiguration.getDomains())
          .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
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
