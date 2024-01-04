package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.TaskanaConfiguration.Builder;
import pro.taskana.classification.internal.jobs.ClassificationChangedJob;
import pro.taskana.common.api.JobService;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.simplehistory.impl.jobs.HistoryCleanupJob;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.task.internal.jobs.TaskRefreshJob;
import pro.taskana.testapi.TaskanaConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;

@TaskanaIntegrationTest
class AbstractTaskanaJobAccTest {

  @TaskanaInject JobMapper jobMapper;

  @TaskanaInject JobService jobService;

  @TaskanaInject TaskanaEngine taskanaEngine;

  @TaskanaInject TaskanaConfiguration taskanaConfiguration;

  @AfterEach
  void cleanupJobs() {
    // Dirty Hack, please refactor me with ticket https://github.com/Taskana/taskana/issues/2238
    jobMapper.deleteMultiple(TaskCleanupJob.class.getName());
    jobMapper.deleteMultiple(TaskRefreshJob.class.getName());
    jobMapper.deleteMultiple(ClassificationChangedJob.class.getName());
    jobMapper.deleteMultiple(HistoryCleanupJob.class.getName());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetNextScheduledJobBasedOnDueDateOfPredecessor_When_RunningTaskCleanupJob() {
    List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(Instant.now());
    assertThat(jobsToRun).isEmpty();

    Instant firstDue = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    ScheduledJob scheduledJob = new ScheduledJob();
    scheduledJob.setType(TaskCleanupJob.class.getName());
    scheduledJob.setDue(firstDue);

    jobService.createJob(scheduledJob);
    jobsToRun = jobMapper.findJobsToRun(Instant.now());

    assertThat(jobsToRun).extracting(ScheduledJob::getDue).containsExactly(firstDue);

    JobRunner runner = new JobRunner(taskanaEngine);
    runner.runJobs();
    Duration runEvery = taskanaConfiguration.getJobRunEvery();
    jobsToRun = jobMapper.findJobsToRun(Instant.now().plus(runEvery));

    assertThat(jobsToRun).extracting(ScheduledJob::getDue).containsExactly(firstDue.plus(runEvery));
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CleanCompletedTasks implements TaskanaConfigurationModifier {
    @TaskanaInject TaskanaEngine taskanaEngine;

    @TaskanaInject JobMapper jobMapper;

    @Override
    public Builder modify(Builder builder) {
      return builder
          .taskCleanupJobEnabled(true)
          .jobRunEvery(Duration.ofMillis(1))
          .jobFirstRun(Instant.now().plus(5, ChronoUnit.MINUTES));
    }

    @WithAccessId(user = "admin")
    @Test
    void should_FindNoJobsToRunUntilFirstRunIsReached_When_CleanupScheduleIsInitialized()
        throws Exception {
      AbstractTaskanaJob.initializeSchedule(taskanaEngine, TaskCleanupJob.class);

      List<ScheduledJob> nextJobs = jobMapper.findJobsToRun(Instant.now());
      assertThat(nextJobs).isEmpty();
    }
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_DeleteOldCleanupJobs_When_InitializingSchedule() throws Exception {
    List<Pair<String, Class<?>>> testCases =
        List.of(
            Pair.of("Delete Old Task Cleanup Jobs", TaskCleanupJob.class),
            Pair.of("Delete Old History Cleanup Jobs", HistoryCleanupJob.class));
    ThrowingConsumer<Pair<String, Class<?>>> test =
        t -> {
          for (int i = 0; i < 10; i++) {
            ScheduledJob job = new ScheduledJob();
            job.setType(t.getRight().getName());
            taskanaEngine.getJobService().createJob(job);
            job.setType(TaskRefreshJob.class.getName());
            taskanaEngine.getJobService().createJob(job);
            job.setType(ClassificationChangedJob.class.getName());
            taskanaEngine.getJobService().createJob(job);
          }

          List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(Instant.now());

          List<ScheduledJob> cleanupJobs =
              jobsToRun.stream()
                  .filter(scheduledJob -> scheduledJob.getType().equals(t.getRight().getName()))
                  .collect(Collectors.toList());

          AbstractTaskanaJob.initializeSchedule(taskanaEngine, t.getRight());

          jobsToRun = jobMapper.findJobsToRun(Instant.now());

          assertThat(jobsToRun).doesNotContainAnyElementsOf(cleanupJobs);
          cleanupJobs();
        };

    return DynamicTest.stream(testCases.iterator(), Pair::getLeft, test);
  }

  @Test
  void should_CreateSampleTaskanaJob_When_JobHasMoreThenOneConstructor() {

    ScheduledJob scheduledJob = new ScheduledJob();
    scheduledJob.setType(SampleTaskanaJob.class.getName());

    ThrowingCallable call =
        () -> AbstractTaskanaJob.createFromScheduledJob(taskanaEngine, null, scheduledJob);

    assertThatCode(call).doesNotThrowAnyException();
  }

  public static class SampleTaskanaJob extends AbstractTaskanaJob {

    public SampleTaskanaJob() {
      super(null, null, null, true);
    }

    public SampleTaskanaJob(
        TaskanaEngine taskanaEngine,
        TaskanaTransactionProvider txProvider,
        ScheduledJob scheduledJob) {
      super(taskanaEngine, txProvider, scheduledJob, true);
    }

    public SampleTaskanaJob(
        TaskanaEngine taskanaEngine,
        TaskanaTransactionProvider txProvider,
        ScheduledJob job,
        boolean async) {
      super(taskanaEngine, txProvider, job, async);
    }

    @Override
    protected String getType() {
      return SampleTaskanaJob.class.getName();
    }

    @Override
    protected void execute() throws TaskanaException {}
  }
}
