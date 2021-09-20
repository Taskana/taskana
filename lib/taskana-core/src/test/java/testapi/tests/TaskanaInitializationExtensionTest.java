package testapi.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import testapi.CleanTaskanaContext;
import testapi.TaskanaEngineConfigurationModifier;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;

import pro.taskana.TaskanaEngineConfiguration;

@TaskanaIntegrationTest
class TaskanaInitializationExtensionTest {

  @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Test
  void should_UseDefaultTaskanaEngine_When_TestIsCreated() {
    assertThat(taskanaEngineConfiguration.getDomains())
        .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
  }

  @Nested
  class ReuseTaskana {

    @TaskanaInject TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Test
    void should_useTopLevelTaskanaInstance_For_NestedTestClasses() {
      assertThat(taskanaEngineConfiguration)
          .isSameAs(TaskanaInitializationExtensionTest.this.taskanaEngineConfiguration);
    }
  }

  @Nested
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
}
