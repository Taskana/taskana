package pro.taskana.testapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.testapi.TaskanaInitializationExtensionTest.NestedTestClassWithServiceProvider.DummyPriorityServiceProvider;

@TaskanaIntegrationTest
class TaskanaInitializationExtensionTest {

  @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Test
  void should_UseDefaultTaskanaEngine_When_TestIsCreated() {
    assertThat(taskanaEngineConfiguration.getDomains())
        .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ReuseTaskana {

    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Test
    void should_useTopLevelTaskanaInstance_For_NestedTestClasses() {
      assertThat(taskanaEngineConfiguration)
          .isSameAs(TaskanaInitializationExtensionTest.this.taskanaEngineConfiguration);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ModifiedTaskanaEngineConfig implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Override
    public void modify(TaskanaEngineConfiguration taskanaEngineConfiguration) {
      taskanaEngineConfiguration.setDomains(List.of("A", "B"));
    }

    @Test
    void should_OverrideConfiguration_When_ClassImplementsTaskanaEngineConfigurationModifier() {
      assertThat(taskanaEngineConfiguration.getDomains()).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void should_createNewTaskanaInstance_For_NestedTestClassImplementingModifierInterface() {
      assertThat(taskanaEngineConfiguration)
          .isNotSameAs(TaskanaInitializationExtensionTest.this.taskanaEngineConfiguration);
    }
  }

  @CleanTaskanaContext
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassAnnotatedWithCleanTaskanaContext {

    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Test
    void should_createNewTaskanaInstance_For_NestedTestClassAnnotatedWithCleanTaskanaContext() {
      assertThat(taskanaEngineConfiguration)
          .isNotSameAs(TaskanaInitializationExtensionTest.this.taskanaEngineConfiguration);
    }

    @Test
    void should_UseDefaultTaskanaEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(taskanaEngineConfiguration.getDomains())
          .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
    }
  }

  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = DummyPriorityServiceProvider.class)
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithServiceProvider {
    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;
    @TaskanaInject TaskanaEngine taskanaEngine;

    @Test
    void should_LoadServiceProviders() throws Exception {
      PriorityServiceManager priorityServiceManager =
          new TaskanaEngineProxy(taskanaEngine).getEngine().getPriorityServiceManager();

      assertThat(priorityServiceManager.isEnabled()).isTrue();
    }

    @Test
    void should_createNewTaskanaInstance_For_NestedTestClassAnnotatedWithCleanTaskanaContext() {
      assertThat(taskanaEngineConfiguration)
          .isNotSameAs(TaskanaInitializationExtensionTest.this.taskanaEngineConfiguration);
    }

    @Test
    void should_UseDefaultTaskanaEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(taskanaEngineConfiguration.getDomains())
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
    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;
    @TaskanaInject TaskanaEngine taskanaEngine;

    @Test
    void should_LoadServiceProviders() throws Exception {
      PriorityServiceManager priorityServiceManager =
          new TaskanaEngineProxy(taskanaEngine).getEngine().getPriorityServiceManager();

      assertThat(priorityServiceManager.isEnabled()).isTrue();
    }

    @Test
    void should_createNewTaskanaInstance_For_NestedTestClassAnnotatedWithCleanTaskanaContext() {
      assertThat(taskanaEngineConfiguration)
          .isNotSameAs(TaskanaInitializationExtensionTest.this.taskanaEngineConfiguration);
    }

    @Test
    void should_UseDefaultTaskanaEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(taskanaEngineConfiguration.getDomains())
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
