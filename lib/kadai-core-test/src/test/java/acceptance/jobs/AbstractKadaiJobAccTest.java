package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.kadai.KadaiConfiguration;
import io.kadai.KadaiConfiguration.Builder;
import io.kadai.classification.internal.jobs.ClassificationChangedJob;
import io.kadai.common.api.JobService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.internal.JobMapper;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.internal.jobs.JobRunner;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.common.internal.util.Pair;
import io.kadai.simplehistory.impl.jobs.HistoryCleanupJob;
import io.kadai.task.internal.jobs.TaskCleanupJob;
import io.kadai.task.internal.jobs.TaskRefreshJob;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.security.WithAccessId;
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

@KadaiIntegrationTest
class AbstractKadaiJobAccTest {

  @KadaiInject JobMapper jobMapper;

  @KadaiInject JobService jobService;

  @KadaiInject KadaiEngine kadaiEngine;

  @KadaiInject KadaiConfiguration kadaiConfiguration;

  @AfterEach
  void cleanupJobs() {
    // Dirty Hack, please refactor me with ticket https://github.com/kadai-iokadai/issues/2238
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

    JobRunner runner = new JobRunner(kadaiEngine);
    runner.runJobs();
    Duration runEvery = kadaiConfiguration.getJobRunEvery();
    jobsToRun = jobMapper.findJobsToRun(Instant.now().plus(runEvery));

    assertThat(jobsToRun).extracting(ScheduledJob::getDue).containsExactly(firstDue.plus(runEvery));
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
            kadaiEngine.getJobService().createJob(job);
            job.setType(TaskRefreshJob.class.getName());
            kadaiEngine.getJobService().createJob(job);
            job.setType(ClassificationChangedJob.class.getName());
            kadaiEngine.getJobService().createJob(job);
          }

          List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(Instant.now());

          List<ScheduledJob> cleanupJobs =
              jobsToRun.stream()
                  .filter(scheduledJob -> scheduledJob.getType().equals(t.getRight().getName()))
                  .collect(Collectors.toList());

          AbstractKadaiJob.initializeSchedule(kadaiEngine, t.getRight());

          jobsToRun = jobMapper.findJobsToRun(Instant.now());

          assertThat(jobsToRun).doesNotContainAnyElementsOf(cleanupJobs);
          cleanupJobs();
        };

    return DynamicTest.stream(testCases.iterator(), Pair::getLeft, test);
  }

  @Test
  void should_CreateSampleKadaiJob_When_JobHasMoreThenOneConstructor() {

    ScheduledJob scheduledJob = new ScheduledJob();
    scheduledJob.setType(SampleKadaiJob.class.getName());

    ThrowingCallable call =
        () -> AbstractKadaiJob.createFromScheduledJob(kadaiEngine, null, scheduledJob);

    assertThatCode(call).doesNotThrowAnyException();
  }

  public static class SampleKadaiJob extends AbstractKadaiJob {

    public SampleKadaiJob() {
      super(null, null, null, true);
    }

    public SampleKadaiJob(
        KadaiEngine kadaiEngine, KadaiTransactionProvider txProvider, ScheduledJob scheduledJob) {
      super(kadaiEngine, txProvider, scheduledJob, true);
    }

    public SampleKadaiJob(
        KadaiEngine kadaiEngine,
        KadaiTransactionProvider txProvider,
        ScheduledJob job,
        boolean async) {
      super(kadaiEngine, txProvider, job, async);
    }

    @Override
    protected String getType() {
      return SampleKadaiJob.class.getName();
    }

    @Override
    protected void execute() throws KadaiException {}
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CleanCompletedTasks implements KadaiConfigurationModifier {
    @KadaiInject KadaiEngine kadaiEngine;

    @KadaiInject JobMapper jobMapper;

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
      AbstractKadaiJob.initializeSchedule(kadaiEngine, TaskCleanupJob.class);

      List<ScheduledJob> nextJobs = jobMapper.findJobsToRun(Instant.now());
      assertThat(nextJobs).isEmpty();
    }
  }
}
