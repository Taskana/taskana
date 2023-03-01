package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.task.internal.jobs.TaskUpdatePriorityJob;
import pro.taskana.testapi.TaskanaEngineConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.workbasket.internal.jobs.WorkbasketCleanupJob;

@TaskanaIntegrationTest
class JobSchedulerInitAccTest implements TaskanaEngineConfigurationModifier {

  @TaskanaInject JobMapper jobMapper;

  Instant firstRun = Instant.now().minus(2, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MILLIS);
  Duration runEvery = Duration.ofMinutes(5);

  @Override
  public TaskanaConfiguration.Builder modify(
      TaskanaConfiguration.Builder taskanaConfigurationBuilder) {
    return taskanaConfigurationBuilder
        .cleanupJobRunEvery(runEvery)
        .cleanupJobFirstRun(firstRun)
        // config for TaskUpdatePriorityJob
        .priorityJobActive(true)
        .priorityJobRunEvery(runEvery)
        .priorityJobFirstRun(firstRun)
        .jobSchedulerEnabled(true)
        .jobSchedulerInitialStartDelay(100)
        .jobSchedulerPeriod(100)
        .jobSchedulerPeriodTimeUnit(TimeUnit.SECONDS)
        .jobSchedulerEnableTaskCleanupJob(true)
        .jobSchedulerEnableTaskUpdatePriorityJob(true)
        .jobSchedulerEnableWorkbasketCleanupJob(true);
  }

  @Test
  void should_StartTheJobsImmediately_When_StartMethodIsCalled() throws Exception {
    List<ScheduledJob> nextJobs = jobMapper.findJobsToRun(Instant.now().plus(runEvery));
    assertThat(nextJobs).extracting(ScheduledJob::getDue).containsOnly(firstRun.plus(runEvery));
    assertThat(nextJobs)
        .extracting(ScheduledJob::getType)
        .containsExactlyInAnyOrder(
            TaskUpdatePriorityJob.class.getName(),
            TaskCleanupJob.class.getName(),
            WorkbasketCleanupJob.class.getName());
  }
}
