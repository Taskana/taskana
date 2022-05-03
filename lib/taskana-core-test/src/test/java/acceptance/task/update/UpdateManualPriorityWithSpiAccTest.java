package acceptance.task.update;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.jobs.helper.TaskUpdatePriorityWorker;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
@WithServiceProvider(
    serviceProviderInterface = PriorityServiceProvider.class,
    serviceProviders =
        UpdateManualPriorityWithSpiAccTest.TestStaticValuePriorityServiceProvider.class)
class UpdateManualPriorityWithSpiAccTest {

  private static final int SPI_PRIORITY = 5;

  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;

  public static class TestStaticValuePriorityServiceProvider implements PriorityServiceProvider {

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
      worker = new TaskUpdatePriorityWorker(taskanaEngine);
      defaultClassificationSummary =
          defaultTestClassification().buildAndStoreAsSummary(classificationService);
      defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

      WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
          .workbasketId(defaultWorkbasketSummary.getId())
          .accessId("user-1-1")
          .permission(WorkbasketPermission.OPEN)
          .permission(WorkbasketPermission.READ)
          .permission(WorkbasketPermission.APPEND)
          .buildAndStore(workbasketService);
      defaultObjectReference = defaultTestObjectReference().build();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_setPriorityToManualAndNotUpdateAccordingToSpi_When_ManualPriorityPositive()
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
    void should_setPriorityUsingSpi_When_ManualPriorityNegative() throws Exception {
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
