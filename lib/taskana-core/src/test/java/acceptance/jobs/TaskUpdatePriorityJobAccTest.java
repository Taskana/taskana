package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.internal.jobs.TaskUpdatePriorityJob;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskUpdatePriorityJobAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(true);

    taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .taskUpdatePriorityJobBatchSize(20)
            .taskUpdatePriorityJobRunEvery(Duration.ofMinutes(30))
            .taskUpdatePriorityJobFirstRun(Instant.parse("2007-12-03T10:15:30.00Z"))
            .build();
    taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaConfiguration);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_workWithoutException() {
    // given
    TaskUpdatePriorityJob job = new TaskUpdatePriorityJob(taskanaEngine);

    // then
    assertThatCode(job::execute).doesNotThrowAnyException();
  }

  @Test
  @WithAccessId(user = "admin")
  void should_ScheduleNextJob() throws Exception {
    // given
    final Instant someTimeInTheFuture = Instant.now().plus(10, ChronoUnit.DAYS);
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration).build();
    TaskanaEngine taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    // when
    AbstractTaskanaJob.initializeSchedule(taskanaEngine, TaskUpdatePriorityJob.class);

    // then
    assertThat(getJobMapper(taskanaEngine).findJobsToRun(someTimeInTheFuture))
        .isNotEmpty()
        .extracting(ScheduledJob::getType)
        .contains(TaskUpdatePriorityJob.class.getName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_readConfigurationForBatchSize() throws Exception {
    // given
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .taskUpdatePriorityJobBatchSize(20)
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaConfiguration));

    // then
    assertThat(job.getBatchSize()).isEqualTo(20);
  }

  @Test
  void should_containInformation_When_convertedToString() throws Exception {
    // given
    Instant expectedFirstRun = Instant.now();
    int expectedBatchSize = 543;
    Duration expectedRunEvery = Duration.ofMinutes(30);
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .taskUpdatePriorityJobFirstRun(expectedFirstRun)
            .taskUpdatePriorityJobBatchSize(expectedBatchSize)
            .taskUpdatePriorityJobRunEvery(expectedRunEvery)
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaConfiguration));

    // then
    assertThat(job.getFirstRun()).isEqualTo(expectedFirstRun);
    assertThat(job.getBatchSize()).isEqualTo(expectedBatchSize);
    assertThat(job.getRunEvery()).isEqualTo(expectedRunEvery);
  }
}
