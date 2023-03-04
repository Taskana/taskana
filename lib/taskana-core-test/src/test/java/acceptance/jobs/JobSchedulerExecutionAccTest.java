package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.TaskanaConfiguration.Builder;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.jobs.JobScheduler;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaEngineConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class JobSchedulerExecutionAccTest implements TaskanaEngineConfigurationModifier {
  @TaskanaInject TaskanaConfiguration taskanaConfiguration;
  @TaskanaInject TaskService taskService;
  @TaskanaInject JobMapper jobMapper;
  WorkbasketSummary workbasket;
  ClassificationSummary classification;
  ObjectReference primaryObjRef;

  @Override
  public Builder modify(Builder taskanaEngineConfigurationBuilder) {
    return taskanaEngineConfigurationBuilder
        .jobSchedulerEnableTaskCleanupJob(true)
        .cleanupJobFirstRun(Instant.now().minus(10, ChronoUnit.MILLIS))
        .cleanupJobRunEvery(Duration.ofMillis(1))
        .cleanupJobMinimumAge(Duration.ofMillis(10));
  }

  @WithAccessId(user = "businessadmin")
  @BeforeEach
  void setup(WorkbasketService workbasketService, ClassificationService classificationService)
      throws Exception {
    workbasket =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    classification =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    primaryObjRef = DefaultTestEntities.defaultTestObjectReference().build();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ExecuteAJobSuccessfully() throws Exception {
    Instant timeStampAnyJobIsOverdue = Instant.now().plus(10, ChronoUnit.DAYS);
    TaskanaEngine taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.EXPLICIT);
    JobScheduler jobScheduler = new JobScheduler(taskanaEngine, new FakeClock());
    TaskBuilder.newTask()
        .workbasketSummary(workbasket)
        .classificationSummary(classification)
        .primaryObjRef(primaryObjRef)
        .state(TaskState.COMPLETED)
        .completed(Instant.now().minus(5, ChronoUnit.DAYS))
        .buildAndStoreAsSummary(taskService);
    final List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(timeStampAnyJobIsOverdue);

    Thread.sleep(2); // to make sure that TaskCleanupJob is overdue
    jobScheduler.start();

    List<TaskSummary> existingTasks = taskService.createTaskQuery().list();
    assertThat(existingTasks).isEmpty();
    List<ScheduledJob> jobsToRunAfter = jobMapper.findJobsToRun(timeStampAnyJobIsOverdue);
    assertThat(jobsToRunAfter).isNotEmpty().doesNotContainAnyElementsOf(jobsToRun);
  }

  public static class AlwaysFailJob extends AbstractTaskanaJob {

    public AlwaysFailJob(
        TaskanaEngine taskanaEngine, TaskanaTransactionProvider txProvider, ScheduledJob job) {
      super(taskanaEngine, txProvider, job, true);
    }

    @Override
    protected String getType() {
      return AlwaysFailJob.class.getName();
    }

    @Override
    protected void execute() {
      throw new SystemException("I always fail. Muahhahaa!");
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class AJobFails implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskanaConfiguration taskanaConfiguration;
    @TaskanaInject TaskService taskService;
    @TaskanaInject JobMapper jobMapper;
    WorkbasketSummary workbasket;
    ClassificationSummary classification;
    ObjectReference primaryObjRef;

    @Override
    public Builder modify(Builder taskanaEngineConfigurationBuilder) {
      return taskanaEngineConfigurationBuilder
          .jobSchedulerEnableTaskCleanupJob(false)
          .cleanupJobFirstRun(Instant.now().minus(10, ChronoUnit.MILLIS))
          .cleanupJobRunEvery(Duration.ofMillis(1))
          .cleanupJobMinimumAge(Duration.ofMillis(10))
          .jobSchedulerCustomJobs(
              List.of(AlwaysFailJob.class.getName(), TaskCleanupJob.class.getName()));
    }

    @WithAccessId(user = "businessadmin")
    @BeforeEach
    void setup(WorkbasketService workbasketService, ClassificationService classificationService)
        throws Exception {
      workbasket =
          DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      classification =
          DefaultTestEntities.defaultTestClassification()
              .buildAndStoreAsSummary(classificationService);
      primaryObjRef = DefaultTestEntities.defaultTestObjectReference().build();
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ContinueExecutingJobs_When_ASingeJobFails() throws Exception {
      Instant timeStampAnyJobIsOverdue = Instant.now().plus(10, ChronoUnit.DAYS);
      TaskanaEngine taskanaEngine =
          TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.EXPLICIT);
      JobScheduler jobScheduler = new JobScheduler(taskanaEngine, new FakeClock());
      TaskBuilder.newTask()
          .workbasketSummary(workbasket)
          .classificationSummary(classification)
          .primaryObjRef(primaryObjRef)
          .state(TaskState.COMPLETED)
          .completed(Instant.now().minus(5, ChronoUnit.DAYS))
          .buildAndStoreAsSummary(taskService);
      final List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(timeStampAnyJobIsOverdue);

      Thread.sleep(2); // to make sure that TaskCleanupJob is overdue
      jobScheduler.start();

      List<TaskSummary> existingTasks = taskService.createTaskQuery().list();
      assertThat(existingTasks).isEmpty();
      List<ScheduledJob> jobsToRunAfter = jobMapper.findJobsToRun(timeStampAnyJobIsOverdue);
      assertThat(jobsToRunAfter).isNotEmpty().doesNotContainAnyElementsOf(jobsToRun);
      assertThat(jobsToRunAfter)
          .filteredOn(job -> AlwaysFailJob.class.getName().equals(job.getType()))
          .extracting(ScheduledJob::getRetryCount)
          .containsExactly(2);
    }
  }
}
