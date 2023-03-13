package acceptance.jobs.helper;

import static acceptance.jobs.helper.TaskUpdatePriorityWorkerAccTest.WithSpi.DummyPriorityServiceProvider.SPI_PRIORITY;
import static org.assertj.core.api.Assertions.assertThat;

import acceptance.jobs.helper.TaskUpdatePriorityWorkerAccTest.WithSpi.DummyPriorityServiceProvider;
import java.time.Instant;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntPredicate;
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
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.jobs.helper.TaskUpdatePriorityWorker;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.testapi.DefaultTestEntities;
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
class TaskUpdatePriorityWorkerAccTest {

  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;

  TaskUpdatePriorityWorker worker;

  TaskSummary task1;
  TaskSummary task2;
  Task completedTask;
  ClassificationSummary classificationSummary;
  WorkbasketSummary workbasketSummary;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setUp(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    classificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    workbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    // Currently, we have a bug: TSK-1736
    // Because of that we need at least one WorkbasketAccessItem with the correct permissions.
    // Otherwise, the DB2 will not work.
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasketSummary.getId())
        .accessId("whatever")
        .permission(WorkbasketPermission.READ)
        .buildAndStore(workbasketService);

    TaskBuilder taskBuilder =
        TaskBuilder.newTask()
            .classificationSummary(classificationSummary)
            .workbasketSummary(workbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());

    task1 = taskBuilder.buildAndStoreAsSummary(taskService);
    task2 = taskBuilder.buildAndStoreAsSummary(taskService);
    completedTask = taskBuilder.state(TaskState.COMPLETED).buildAndStore(taskService);
    worker = new TaskUpdatePriorityWorker(taskanaEngine);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_LoadAnyRelevantTaskIds() {
    // when
    final List<String> allRelevantTaskIds = worker.getAllRelevantTaskIds();

    // then
    assertThat(allRelevantTaskIds).containsExactlyInAnyOrder(task1.getId(), task2.getId());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_LoadExistingTaskIds() {
    // when
    final List<TaskSummary> taskSummariesByIds =
        worker.getTaskSummariesByIds(List.of(task1.getId(), task2.getId()));

    // then
    assertThat(taskSummariesByIds).containsExactlyInAnyOrder(task1, task2);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotLoadAnyIrrelevantTaskIds() {
    // when
    final List<String> allRelevantTaskIds = worker.getAllRelevantTaskIds();

    // then
    assertThat(allRelevantTaskIds).isNotEmpty().doesNotContain(completedTask.getId());
  }

  @Test
  void should_NoticeDifferentPriority_When_PriorityHasChanged() {
    // given
    TaskImpl task = (TaskImpl) taskService.newTask();
    task.setPriority(232);

    // when
    IntPredicate differentPriority = TaskUpdatePriorityWorker.hasDifferentPriority(task);

    // then
    assertThat(differentPriority).rejects(232).accepts(2, 3, 4, 5);
  }

  @Nested
  @WithServiceProvider(
      serviceProviderInterface = PriorityServiceProvider.class,
      serviceProviders = DummyPriorityServiceProvider.class)
  @TestInstance(Lifecycle.PER_CLASS)
  class WithSpi {

    @TaskanaInject TaskService taskService;
    TaskUpdatePriorityWorker worker;

    @BeforeAll
    void setup(TaskanaEngine taskanaEngine) {
      worker = new TaskUpdatePriorityWorker(taskanaEngine);
    }

    @Test
    @WithAccessId(user = "admin")
    void should_ExecuteBatch() throws Exception {
      Task oldTask =
          TaskBuilder.newTask()
              .classificationSummary(classificationSummary)
              .workbasketSummary(workbasketSummary)
              .created(Instant.parse("2020-04-30T07:12:00.000Z"))
              .priority(1337)
              .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
              .buildAndStore(taskService);

      List<String> updatedTaskIds = worker.executeBatch(List.of(oldTask.getId()));

      Task updatedTask = taskService.getTask(oldTask.getId());
      assertThat(updatedTaskIds).containsExactly(oldTask.getId());
      assertThat(updatedTask.getPriority()).isEqualTo(SPI_PRIORITY);
    }

    class DummyPriorityServiceProvider implements PriorityServiceProvider {
      static final int SPI_PRIORITY = 10;

      @Override
      public OptionalInt calculatePriority(TaskSummary taskSummary) {
        return OptionalInt.of(SPI_PRIORITY);
      }
    }
  }
}
