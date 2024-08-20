package acceptance.task.update;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;

import acceptance.task.update.UpdateManualPriorityWithSpiAccTest.TestStaticValuePriorityServiceProvider;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.spi.priority.api.PriorityServiceProvider;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.internal.jobs.helper.TaskUpdatePriorityWorker;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@KadaiIntegrationTest
@WithServiceProvider(
    serviceProviderInterface = PriorityServiceProvider.class,
    serviceProviders = TestStaticValuePriorityServiceProvider.class)
class UpdateManualPriorityWithSpiAccTest {

  private static final int SPI_PRIORITY = 5;

  @KadaiInject KadaiEngine kadaiEngine;
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;

  static class TestStaticValuePriorityServiceProvider implements PriorityServiceProvider {
    @Override
    public OptionalInt calculatePriority(TaskSummary taskSummary) {
      return OptionalInt.of(UpdateManualPriorityWithSpiAccTest.SPI_PRIORITY);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class UpdateManualPriorityTest {

    ClassificationSummary defaultClassificationSummary;
    WorkbasketSummary defaultWorkbasketSummary;
    ObjectReference defaultObjectReference;
    TaskUpdatePriorityWorker worker;

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      worker = new TaskUpdatePriorityWorker(kadaiEngine);
      defaultClassificationSummary =
          defaultTestClassification().buildAndStoreAsSummary(classificationService);
      defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

      WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
          .workbasketId(defaultWorkbasketSummary.getId())
          .accessId("user-1-1")
          .permission(WorkbasketPermission.OPEN)
          .permission(WorkbasketPermission.READ)
          .permission(WorkbasketPermission.READTASKS)
          .permission(WorkbasketPermission.EDITTASKS)
          .permission(WorkbasketPermission.APPEND)
          .buildAndStore(workbasketService);
      defaultObjectReference = defaultTestObjectReference().build();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_SetPriorityToManualAndNotUpdateAccordingToSpi_When_ManualPriorityPositive()
        throws Exception {
      Task task = taskService.newTask(defaultWorkbasketSummary.getId());
      task.setClassificationKey(defaultClassificationSummary.getKey());
      task.setPrimaryObjRef(defaultObjectReference);
      task.setManualPriority(123);
      taskService.createTask(task);
      worker.executeBatch(List.of(task.getId()));

      Task result = taskService.getTask(task.getId());
      assertThat(result.getPriority()).isEqualTo(result.getManualPriority()).isEqualTo(123);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_SetPriorityUsingSpi_When_ManualPriorityNegative() throws Exception {
      Task task = taskService.newTask(defaultWorkbasketSummary.getId());
      task.setClassificationKey(defaultClassificationSummary.getKey());
      task.setPrimaryObjRef(defaultObjectReference);
      task.setManualPriority(-1);
      Task result = taskService.createTask(task);

      assertThat(result.getPriority()).isEqualTo(UpdateManualPriorityWithSpiAccTest.SPI_PRIORITY);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_UpdatePriorityAccordingToSpi_When_UpdatedTaskHasNegativeManualPriority()
        throws Exception {
      Task task = createDefaultTask().manualPriority(123).buildAndStore(taskService);

      task.setManualPriority(-3);
      Task result = taskService.updateTask(task);

      assertThat(result.getPriority()).isEqualTo(UpdateManualPriorityWithSpiAccTest.SPI_PRIORITY);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_UpdateManualPriorityAnd_When_UpdatedTaskHasZeroManualPriority() throws Exception {
      Task task = createDefaultTask().manualPriority(123).buildAndStore(taskService);

      task.setManualPriority(0);
      Task result = taskService.updateTask(task);

      assertThat(result.getPriority()).isZero();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_UpdateManualPriority_When_UpdatedTaskHasNewPositiveManualPriority()
        throws Exception {
      Task task = createDefaultTask().manualPriority(123).buildAndStore(taskService);

      task.setManualPriority(42);
      Task result = taskService.updateTask(task);

      assertThat(result.getPriority()).isEqualTo(result.getManualPriority()).isEqualTo(42);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void
        should_NotUpdatePriorityWithSpi_When_TaskWithoutManualPriorityHasManualPriorityAfterUpdate()
            throws Exception {
      Task task = createDefaultTask().manualPriority(-1).buildAndStore(taskService);
      task.setManualPriority(123);

      taskService.updateTask(task);
      worker.executeBatch(List.of(task.getId()));

      Task result = taskService.getTask(task.getId());
      assertThat(result.getPriority()).isEqualTo(result.getManualPriority()).isEqualTo(123);
    }

    private TaskBuilder createDefaultTask() {
      return TaskBuilder.newTask()
          .classificationSummary(defaultClassificationSummary)
          .workbasketSummary(defaultWorkbasketSummary)
          .primaryObjRef(defaultObjectReference);
    }
  }
}
