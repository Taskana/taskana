package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.TaskanaConfiguration.Builder;
import pro.taskana.classification.internal.jobs.ClassificationChangedJob;
import pro.taskana.common.api.JobService;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.jobs.JobRunner;
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

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteOldTaskCleanupJobs_When_InitializingSchedule() {

    for (int i = 0; i < 10; i++) {
      ScheduledJob job = new ScheduledJob();
      job.setType(TaskCleanupJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
      job.setType(TaskRefreshJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
      job.setType(ClassificationChangedJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
    }

    List<ScheduledJob> jobsToRun = jobMapper.findJobsToRun(Instant.now());

    assertThat(jobsToRun).hasSize(30);

    List<ScheduledJob> taskCleanupJobs =
        jobsToRun.stream()
            .filter(scheduledJob -> scheduledJob.getType().equals(TaskCleanupJob.class.getName()))
            .collect(Collectors.toList());

    AbstractTaskanaJob.initializeSchedule(taskanaEngine, TaskCleanupJob.class);

    jobsToRun = jobMapper.findJobsToRun(Instant.now());

    assertThat(jobsToRun).doesNotContainAnyElementsOf(taskCleanupJobs);
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
}
