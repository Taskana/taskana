package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.TaskanaConfiguration.Builder;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

// All tests are executed as admin, because the jobrunner needs admin rights.
@TaskanaIntegrationTest
class TaskCleanupJobAccTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;

  ClassificationSummary classification;
  ObjectReference primaryObjRef;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    classification =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    primaryObjRef = DefaultTestEntities.defaultTestObjectReference().build();
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CleanCompletedTasks implements TaskanaConfigurationModifier {

    WorkbasketSummary workbasket;

    @TaskanaInject TaskanaEngine taskanaEngine;

    @Override
    public Builder modify(Builder builder) {
      return builder
          .taskCleanupJobEnabled(true)
          .jobFirstRun(Instant.now().minus(10, ChronoUnit.MILLIS))
          .jobRunEvery(Duration.ofMillis(1))
          .taskCleanupJobMinimumAge(Duration.ofDays(5));
    }

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      workbasket =
          DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_CleanCompletedTasks_When_CompletedTimestampIsOlderThenTaskCleanupJobMinimumAge()
        throws Exception {
      TaskBuilder.newTask()
          .workbasketSummary(workbasket)
          .classificationSummary(classification)
          .primaryObjRef(primaryObjRef)
          .state(TaskState.COMPLETED)
          .completed(Instant.now().minus(6, ChronoUnit.DAYS))
          .buildAndStoreAsSummary(taskService);

      TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
      job.run();

      List<TaskSummary> taskSummaries = taskService.createTaskQuery().list();
      assertThat(taskSummaries)
          .filteredOn(t -> t.getWorkbasketSummary().equals(workbasket))
          .isEmpty();
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class NotCleanCompletedTasksWhereDateIsNotReached implements TaskanaConfigurationModifier {

    @TaskanaInject TaskanaEngine taskanaEngine;

    @Override
    public Builder modify(Builder builder) {
      return builder
          .taskCleanupJobEnabled(true)
          .jobFirstRun(Instant.now().minus(10, ChronoUnit.MILLIS))
          .jobRunEvery(Duration.ofMillis(1))
          .taskCleanupJobMinimumAge(Duration.ofDays(5));
    }

    @WithAccessId(user = "admin")
    @Test
    void should_NotCleanCompletedTasksAfterDefinedDay() throws Exception {
      WorkbasketSummary workbasket =
          DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      TaskSummary taskSummary =
          TaskBuilder.newTask()
              .workbasketSummary(workbasket)
              .classificationSummary(classification)
              .primaryObjRef(primaryObjRef)
              .state(TaskState.COMPLETED)
              .completed(Instant.now().minus(3, ChronoUnit.DAYS))
              .buildAndStoreAsSummary(taskService);

      TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
      job.run();

      List<TaskSummary> taskSummaries = taskService.createTaskQuery().list();
      assertThat(taskSummaries)
          .filteredOn(t -> t.getWorkbasketSummary().equals(workbasket))
          .containsExactlyInAnyOrder(taskSummary);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_OnlyCleanCompletedTasks_When_DefinedCompletedExceedThreshold() throws Exception {
      WorkbasketSummary workbasket =
          DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      TaskBuilder taskBuilder =
          TaskBuilder.newTask()
              .workbasketSummary(workbasket)
              .classificationSummary(classification)
              .primaryObjRef(primaryObjRef)
              .state(TaskState.COMPLETED)
              .completed(Instant.now().minus(3, ChronoUnit.DAYS));
      TaskSummary taskSummary1 = taskBuilder.buildAndStoreAsSummary(taskService);
      TaskSummary taskSummary2 =
          taskBuilder
              .completed(Instant.now().minus(10, ChronoUnit.DAYS))
              .buildAndStoreAsSummary(taskService);
      TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
      job.run();

      List<TaskSummary> taskSummaries = taskService.createTaskQuery().list();
      assertThat(taskSummaries)
          .filteredOn(t -> t.getWorkbasketSummary().equals(workbasket))
          .containsExactlyInAnyOrder(taskSummary1)
          .doesNotContain(taskSummary2);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CleanCompletedTasksWithBusinessProcessId implements TaskanaConfigurationModifier {

    @TaskanaInject TaskanaEngine taskanaEngine;

    @Override
    public Builder modify(Builder builder) {
      return builder
          .taskCleanupJobEnabled(true)
          .jobFirstRun(Instant.now().minus(10, ChronoUnit.MILLIS))
          .jobRunEvery(Duration.ofMillis(1))
          .taskCleanupJobMinimumAge(Duration.ofDays(5))
          .taskCleanupJobAllCompletedSameParentBusiness(true);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_CleanCompletedTasksWithSameParentBusiness() throws Exception {
      WorkbasketSummary workbasket =
          DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      TaskBuilder taskBuilder =
          TaskBuilder.newTask()
              .workbasketSummary(workbasket)
              .classificationSummary(classification)
              .primaryObjRef(primaryObjRef)
              .state(TaskState.COMPLETED)
              .completed(Instant.now().minus(10, ChronoUnit.DAYS))
              .parentBusinessProcessId("ParentProcessId_1");
      taskBuilder.buildAndStoreAsSummary(taskService);
      taskBuilder.buildAndStoreAsSummary(taskService);

      TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
      job.run();

      List<TaskSummary> taskSummaries = taskService.createTaskQuery().list();
      assertThat(taskSummaries)
          .filteredOn(t -> t.getWorkbasketSummary().equals(workbasket))
          .isEmpty();
    }

    @WithAccessId(user = "admin")
    @Test
    void should_NotCleanCompletedTasksWithSameParentBusiness_When_OneSubTaskIsIncomplete()
        throws Exception {
      WorkbasketSummary workbasket =
          DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      TaskBuilder taskBuilder =
          TaskBuilder.newTask()
              .workbasketSummary(workbasket)
              .classificationSummary(classification)
              .primaryObjRef(primaryObjRef)
              .state(TaskState.COMPLETED)
              .completed(Instant.now().minus(10, ChronoUnit.DAYS))
              .parentBusinessProcessId("ParentProcessId_3");

      TaskSummary taskSummaryCompleted1 = taskBuilder.buildAndStoreAsSummary(taskService);
      TaskSummary taskSummaryCompleted2 = taskBuilder.buildAndStoreAsSummary(taskService);

      TaskSummary taskSummaryCompleted =
          taskBuilder
              .parentBusinessProcessId("ParentProcessId_4")
              .buildAndStoreAsSummary(taskService);
      TaskSummary taskSummaryClaimed =
          taskBuilder
              .parentBusinessProcessId("ParentProcessId_4")
              .state(TaskState.CLAIMED)
              .completed(null)
              .buildAndStoreAsSummary(taskService);

      TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
      job.run();

      List<TaskSummary> taskSummaries = taskService.createTaskQuery().list();
      assertThat(taskSummaries)
          .filteredOn(t -> t.getWorkbasketSummary().equals(workbasket))
          .containsExactlyInAnyOrder(taskSummaryCompleted, taskSummaryClaimed)
          .doesNotContain(taskSummaryCompleted1, taskSummaryCompleted2);
    }

    @WithAccessId(user = "admin")
    @TestFactory
    Stream<DynamicTest>
        should_DeleteCompletedTaskWithParentBusinessEmptyOrNull_When_RunningCleanupJob()
            throws Exception {
      Iterator<String> iterator = Arrays.asList("", null).iterator();

      ThrowingConsumer<String> test =
          parentBusinessId -> {
            WorkbasketSummary workbasket =
                DefaultTestEntities.defaultTestWorkbasket()
                    .buildAndStoreAsSummary(workbasketService);
            TaskBuilder taskBuilder =
                TaskBuilder.newTask()
                    .workbasketSummary(workbasket)
                    .classificationSummary(classification)
                    .primaryObjRef(primaryObjRef)
                    .state(TaskState.COMPLETED)
                    .completed(Instant.now().minus(10, ChronoUnit.DAYS))
                    .parentBusinessProcessId(parentBusinessId);

            TaskSummary taskSummaryCompleted = taskBuilder.buildAndStoreAsSummary(taskService);
            TaskSummary taskSummaryClaimed =
                taskBuilder
                    .state(TaskState.CLAIMED)
                    .completed(null)
                    .buildAndStoreAsSummary(taskService);

            TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
            job.run();

            List<TaskSummary> taskSummaries = taskService.createTaskQuery().list();
            assertThat(taskSummaries)
                .filteredOn(t -> t.getWorkbasketSummary().equals(workbasket))
                .containsExactlyInAnyOrder(taskSummaryClaimed)
                .doesNotContain(taskSummaryCompleted);
          };
      return DynamicTest.stream(iterator, c -> "for parentBusinessProcessId = '" + c + "'", test);
    }
  }
}
