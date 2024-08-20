package acceptance.jobs.helper;

import static acceptance.jobs.helper.TaskUpdatePriorityWorkerAccTest.WithSpi.DummyPriorityServiceProvider.SPI_PRIORITY;
import static org.assertj.core.api.Assertions.assertThat;

import acceptance.jobs.helper.TaskUpdatePriorityWorkerAccTest.WithSpi.DummyPriorityServiceProvider;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.spi.priority.api.PriorityServiceProvider;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.internal.jobs.helper.TaskUpdatePriorityWorker;
import io.kadai.task.internal.models.TaskImpl;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntPredicate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@KadaiIntegrationTest
class TaskUpdatePriorityWorkerAccTest {

  @KadaiInject KadaiEngine kadaiEngine;
  @KadaiInject TaskService taskService;

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
        .permission(WorkbasketPermission.READTASKS)
        .buildAndStore(workbasketService);

    TaskBuilder taskBuilder =
        TaskBuilder.newTask()
            .classificationSummary(classificationSummary)
            .workbasketSummary(workbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());

    task1 = taskBuilder.buildAndStoreAsSummary(taskService);
    task2 = taskBuilder.buildAndStoreAsSummary(taskService);
    completedTask = taskBuilder.state(TaskState.COMPLETED).buildAndStore(taskService);
    worker = new TaskUpdatePriorityWorker(kadaiEngine);
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

    @KadaiInject TaskService taskService;
    TaskUpdatePriorityWorker worker;

    @BeforeAll
    void setup(KadaiEngine kadaiEngine) {
      worker = new TaskUpdatePriorityWorker(kadaiEngine);
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
