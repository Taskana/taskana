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
import pro.taskana.testapi.TaskanaConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.workbasket.internal.jobs.WorkbasketCleanupJob;

@TaskanaIntegrationTest
class JobSchedulerInitAccTest implements TaskanaConfigurationModifier {

  @TaskanaInject JobMapper jobMapper;

  Instant firstRun = Instant.now().minus(2, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MILLIS);
  Duration runEvery = Duration.ofMinutes(5);

  @Override
  public TaskanaConfiguration.Builder modify(TaskanaConfiguration.Builder builder) {
    return builder
        .jobRunEvery(runEvery)
        .jobFirstRun(firstRun)
        // config for TaskUpdatePriorityJob
        .taskUpdatePriorityJobRunEvery(runEvery)
        .taskUpdatePriorityJobFirstRun(firstRun)
        .jobSchedulerEnabled(true)
        .jobSchedulerInitialStartDelay(100)
        .jobSchedulerPeriod(100)
        .jobSchedulerPeriodTimeUnit(TimeUnit.SECONDS)
        .taskCleanupJobEnabled(true)
        .taskUpdatePriorityJobEnabled(true)
        .workbasketCleanupJobEnabled(true);
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
