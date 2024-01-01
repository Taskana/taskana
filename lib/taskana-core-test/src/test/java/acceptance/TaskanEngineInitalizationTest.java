package acceptance;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import java.util.OptionalInt;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;

@TaskanaIntegrationTest
public class TaskanEngineInitalizationTest {

  static class MyPriorityServiceProvider implements PriorityServiceProvider {

    private TaskanaEngine taskanaEngine;

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {
      this.taskanaEngine = taskanaEngine;
    }

    @Override
    public OptionalInt calculatePriority(TaskSummary taskSummary) {
      return OptionalInt.empty();
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = MyPriorityServiceProvider.class)
  class PriorityServiceManagerTest {
    @TaskanaInject TaskanaEngineImpl taskanaEngine;

    @Test
    @SuppressWarnings("unchecked")
    void should_InitializePriorityServiceProviders() throws Exception {
      PriorityServiceManager priorityServiceManager = taskanaEngine.getPriorityServiceManager();
      Field priorityServiceProvidersField =
          PriorityServiceManager.class.getDeclaredField("priorityServiceProviders");
      priorityServiceProvidersField.setAccessible(true);
      List<PriorityServiceProvider> serviceProviders =
          (List<PriorityServiceProvider>) priorityServiceProvidersField.get(priorityServiceManager);

      assertThat(priorityServiceManager.isEnabled()).isTrue();
      assertThat(serviceProviders)
          .asInstanceOf(InstanceOfAssertFactories.LIST)
          .hasOnlyElementsOfType(MyPriorityServiceProvider.class)
          .extracting(MyPriorityServiceProvider.class::cast)
          .extracting(sp -> sp.taskanaEngine)
          .containsOnly(taskanaEngine);
    }
  }
}
