package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.internal.JobMapper;
import io.kadai.task.internal.jobs.TaskCleanupJob;
import io.kadai.task.internal.jobs.TaskUpdatePriorityJob;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.workbasket.internal.jobs.WorkbasketCleanupJob;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

@KadaiIntegrationTest
class JobSchedulerInitAccTest implements KadaiConfigurationModifier {

  @KadaiInject JobMapper jobMapper;

  Instant firstRun = Instant.now().minus(2, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MILLIS);
  Duration runEvery = Duration.ofMinutes(5);

  @Override
  public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
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
