package io.kadai.testapi;

import static io.kadai.common.api.SharedConstants.MASTER_DOMAIN;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.spi.priority.api.PriorityServiceProvider;
import io.kadai.spi.priority.internal.PriorityServiceManager;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.testapi.KadaiInitializationExtensionTest.NestedTestClassWithServiceProvider.DummyPriorityServiceProvider;
import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@KadaiIntegrationTest
class KadaiInitializationExtensionTest {

  @KadaiInject KadaiConfiguration kadaiConfiguration;

  @Test
  void should_UseDefaultKadaiEngine_When_TestIsCreated() {
    assertThat(kadaiConfiguration.getDomains())
        .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B", MASTER_DOMAIN);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ReuseKadai {

    @KadaiInject KadaiConfiguration kadaiConfiguration;

    @Test
    void should_useTopLevelKadaiInstance_For_NestedTestClasses() {
      assertThat(kadaiConfiguration)
          .isSameAs(KadaiInitializationExtensionTest.this.kadaiConfiguration);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ModifiedKadaiConfig implements KadaiConfigurationModifier {

    @KadaiInject KadaiConfiguration kadaiConfiguration;

    @Override
    public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
      return builder.domains(List.of("A", "B"));
    }

    @Test
    void should_OverrideConfiguration_When_ClassImplementsKadaiConfigurationModifier() {
      assertThat(kadaiConfiguration.getDomains()).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void should_createNewKadaiInstance_For_NestedTestClassImplementingModifierInterface() {
      assertThat(kadaiConfiguration)
          .isNotSameAs(KadaiInitializationExtensionTest.this.kadaiConfiguration);
    }
  }

  @CleanKadaiContext
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassAnnotatedWithCleanKadaiContext {

    @KadaiInject KadaiConfiguration kadaiConfiguration;

    @Test
    void should_createNewKadaiInstance_For_NestedTestClassAnnotatedWithCleanKadaiContext() {
      assertThat(kadaiConfiguration)
          .isNotSameAs(KadaiInitializationExtensionTest.this.kadaiConfiguration);
    }

    @Test
    void should_UseDefaultKadaiEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(kadaiConfiguration.getDomains())
          .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B", MASTER_DOMAIN);
    }
  }

  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = DummyPriorityServiceProvider.class)
  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NestedTestClassWithServiceProvider {
    @KadaiInject KadaiConfiguration kadaiConfiguration;
    @KadaiInject KadaiEngine kadaiEngine;

    @Test
    void should_LoadServiceProviders() throws Exception {
      PriorityServiceManager priorityServiceManager =
          new KadaiEngineProxy(kadaiEngine).getEngine().getPriorityServiceManager();

      assertThat(priorityServiceManager.isEnabled()).isTrue();
    }

    @Test
    void should_createNewKadaiInstance_For_NestedTestClassAnnotatedWithCleanKadaiContext() {
      assertThat(kadaiConfiguration)
          .isNotSameAs(KadaiInitializationExtensionTest.this.kadaiConfiguration);
    }

    @Test
    void should_UseDefaultKadaiEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(kadaiConfiguration.getDomains())
          .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B", MASTER_DOMAIN);
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
    @KadaiInject KadaiConfiguration kadaiConfiguration;
    @KadaiInject KadaiEngine kadaiEngine;

    @Test
    void should_LoadServiceProviders() throws Exception {
      PriorityServiceManager priorityServiceManager =
          new KadaiEngineProxy(kadaiEngine).getEngine().getPriorityServiceManager();

      assertThat(priorityServiceManager.isEnabled()).isTrue();
    }

    @Test
    void should_createNewKadaiInstance_For_NestedTestClassAnnotatedWithCleanKadaiContext() {
      assertThat(kadaiConfiguration)
          .isNotSameAs(KadaiInitializationExtensionTest.this.kadaiConfiguration);
    }

    @Test
    void should_UseDefaultKadaiEngine_When_NestedClassDoesNotImplementModifier() {
      assertThat(kadaiConfiguration.getDomains())
          .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B", MASTER_DOMAIN);
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
