package acceptance.jobs.helper;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.DefaultTestEntities;
import acceptance.priorityservice.TestPriorityServiceProvider;
import java.time.Instant;
import java.util.List;
import java.util.function.IntPredicate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;
import testapi.WithServiceProvider;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.builder.TaskBuilder;
import pro.taskana.task.internal.jobs.helper.TaskUpdatePriorityWorker;
import pro.taskana.task.internal.models.TaskImpl;
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

  @WithAccessId(user = "admin")
  @BeforeAll
  void setUp(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    ClassificationSummary classificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStore(classificationService)
            .asSummary();
    WorkbasketSummary workbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService).asSummary();

    TaskBuilder taskBuilder =
        TaskBuilder.newTask()
            .classificationSummary(classificationSummary)
            .workbasketSummary(workbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());

    task1 = taskBuilder.buildAndStore(taskService).asSummary();
    task2 = taskBuilder.buildAndStore(taskService).asSummary();
    completedTask = taskBuilder.state(TaskState.COMPLETED).buildAndStore(taskService);
    worker = new TaskUpdatePriorityWorker(taskanaEngine);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_loadAnyRelevantTaskIds() {
    // when
    final List<String> allRelevantTaskIds = worker.getAllRelevantTaskIds();

    // then
    assertThat(allRelevantTaskIds).containsExactlyInAnyOrder(task1.getId(), task2.getId());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_loadExistingTaskIds() {
    // when
    final List<TaskSummary> taskSummariesByIds =
        worker.getTaskSummariesByIds(List.of(task1.getId(), task2.getId()));

    // then
    assertThat(taskSummariesByIds).containsExactlyInAnyOrder(task1, task2);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_notLoadAnyIrrelevantTaskIds() {
    // when
    final List<String> allRelevantTaskIds = worker.getAllRelevantTaskIds();

    // then
    assertThat(allRelevantTaskIds).isNotEmpty().doesNotContain(completedTask.getId());
  }

  @Test
  void should_noticeDifferentPriority_When_PriorityHasChanged() {
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
      serviceProviders = TestPriorityServiceProvider.class)
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
    void should_executeBatch(
        WorkbasketService workbasketService, ClassificationService classificationService)
        throws Exception {
      // given
      ClassificationSummary classificationSummary =
          DefaultTestEntities.defaultTestClassification()
              .buildAndStore(classificationService)
              .asSummary();
      WorkbasketSummary workbasketSummary =
          DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService).asSummary();
      Task oldTask =
          TaskBuilder.newTask()
              .classificationSummary(classificationSummary)
              .workbasketSummary(workbasketSummary)
              .created(Instant.parse("2020-04-30T07:12:00.000Z"))
              .priority(1337)
              .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
              .buildAndStore(taskService);

      // when
      final List<String> updatedTaskIds = worker.executeBatch(List.of(oldTask.getId()));

      // then
      final Task updatedTask = taskService.getTask(oldTask.getId());

      assertThat(updatedTaskIds).containsExactly(oldTask.getId());
      assertThat(updatedTask.getPriority()).isNotEqualTo(oldTask.getPriority());
    }
  }
}
