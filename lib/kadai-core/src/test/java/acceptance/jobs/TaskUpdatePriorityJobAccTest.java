package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import acceptance.AbstractAccTest;
import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiEngine.ConnectionManagementMode;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.task.internal.jobs.TaskUpdatePriorityJob;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskUpdatePriorityJobAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(true);

    kadaiConfiguration =
        new KadaiConfiguration.Builder(AbstractAccTest.kadaiConfiguration)
            .taskUpdatePriorityJobBatchSize(20)
            .taskUpdatePriorityJobRunEvery(Duration.ofMinutes(30))
            .taskUpdatePriorityJobFirstRun(Instant.parse("2007-12-03T10:15:30.00Z"))
            .build();
    kadaiEngine = KadaiEngine.buildKadaiEngine(kadaiConfiguration);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_workWithoutException() {
    // given
    TaskUpdatePriorityJob job = new TaskUpdatePriorityJob(kadaiEngine);

    // then
    assertThatCode(job::execute).doesNotThrowAnyException();
  }

  @Test
  @WithAccessId(user = "admin")
  void should_ScheduleNextJob() throws Exception {
    // given
    final Instant someTimeInTheFuture = Instant.now().plus(10, ChronoUnit.DAYS);
    KadaiConfiguration kadaiConfiguration =
        new KadaiConfiguration.Builder(AbstractAccTest.kadaiConfiguration).build();
    KadaiEngine kadaiEngine =
        KadaiEngine.buildKadaiEngine(kadaiConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    // when
    AbstractKadaiJob.initializeSchedule(kadaiEngine, TaskUpdatePriorityJob.class);

    // then
    assertThat(getJobMapper(kadaiEngine).findJobsToRun(someTimeInTheFuture))
        .isNotEmpty()
        .extracting(ScheduledJob::getType)
        .contains(TaskUpdatePriorityJob.class.getName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_readConfigurationForBatchSize() throws Exception {
    // given
    KadaiConfiguration kadaiConfiguration =
        new KadaiConfiguration.Builder(AbstractAccTest.kadaiConfiguration)
            .taskUpdatePriorityJobBatchSize(20)
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(KadaiEngine.buildKadaiEngine(kadaiConfiguration));

    // then
    assertThat(job.getBatchSize()).isEqualTo(20);
  }

  @Test
  void should_containInformation_When_convertedToString() throws Exception {
    // given
    Instant expectedFirstRun = Instant.now();
    int expectedBatchSize = 543;
    Duration expectedRunEvery = Duration.ofMinutes(30);
    KadaiConfiguration kadaiConfiguration =
        new KadaiConfiguration.Builder(AbstractAccTest.kadaiConfiguration)
            .taskUpdatePriorityJobFirstRun(expectedFirstRun)
            .taskUpdatePriorityJobBatchSize(expectedBatchSize)
            .taskUpdatePriorityJobRunEvery(expectedRunEvery)
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(KadaiEngine.buildKadaiEngine(kadaiConfiguration));

    // then
    assertThat(job.getFirstRun()).isEqualTo(expectedFirstRun);
    assertThat(job.getBatchSize()).isEqualTo(expectedBatchSize);
    assertThat(job.getRunEvery()).isEqualTo(expectedRunEvery);
  }
}
