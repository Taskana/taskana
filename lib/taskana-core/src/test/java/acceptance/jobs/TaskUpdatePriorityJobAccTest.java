package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.internal.jobs.TaskUpdatePriorityJob;

@ExtendWith(JaasExtension.class)
class TaskUpdatePriorityJobAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(true);

    taskanaEngineConfiguration.setPriorityJobActive(true);
    taskanaEngineConfiguration.setPriorityJobBatchSize(20);
    taskanaEngineConfiguration.setPriorityJobRunEvery(Duration.ofMinutes(30));
    taskanaEngineConfiguration.setPriorityJobFirstRun(Instant.parse("2007-12-03T10:15:30.00Z"));
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
  void should_catchException_When_executedWithWrongSettings() {
    // given
    taskanaEngineConfiguration.setPriorityJobBatchSize(0);
    TaskUpdatePriorityJob job = new TaskUpdatePriorityJob(taskanaEngine);

    // then
    assertThatThrownBy(job::execute).isInstanceOf(SystemException.class);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_doNothing_When_NotActive() {
    // given
    taskanaEngineConfiguration.setPriorityJobActive(false);
    TaskUpdatePriorityJob job = new TaskUpdatePriorityJob(taskanaEngine);
    List<String> priorities =
        taskanaEngine
            .getTaskService()
            .createTaskQuery()
            .listValues(TaskQueryColumnName.PRIORITY, SortDirection.ASCENDING);

    // when
    job.execute();

    // then
    List<String> prioritiesUnchanged =
        taskanaEngine
            .getTaskService()
            .createTaskQuery()
            .listValues(TaskQueryColumnName.PRIORITY, SortDirection.ASCENDING);

    assertThat(priorities).isEqualTo(prioritiesUnchanged);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_ScheduleNextJob() throws NoSuchFieldException, IllegalAccessException {
    // given
    final Instant someTimeInTheFuture = Instant.now().plus(10, ChronoUnit.DAYS);

    // when
    TaskUpdatePriorityJob.initializeSchedule(taskanaEngine);

    // then
    assertThat(getJobMapper().findJobsToRun(someTimeInTheFuture))
        .hasSizeGreaterThanOrEqualTo(1)
        .extracting(ScheduledJob::getType)
        .contains(TaskUpdatePriorityJob.class.getName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_readConfigurationForBatchSize() {
    // given
    taskanaEngineConfiguration.setPriorityJobBatchSize(20);

    // when
    final TaskUpdatePriorityJob job = new TaskUpdatePriorityJob(taskanaEngine);

    // then
    assertThat(job.getBatchSize()).isEqualTo(20);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_readConfigurationForIsActive() {
    // given
    taskanaEngineConfiguration.setPriorityJobActive(false);

    // when
    final TaskUpdatePriorityJob job = new TaskUpdatePriorityJob(taskanaEngine);

    // then
    assertThat(job.isJobActive()).isFalse();
  }

  @Test
  void should_containInformation_When_convertedToString() {
    // given
    taskanaEngineConfiguration.setPriorityJobBatchSize(543);
    taskanaEngineConfiguration.setPriorityJobRunEvery(Duration.ofMinutes(30));

    // when
    final TaskUpdatePriorityJob job = new TaskUpdatePriorityJob(taskanaEngine);

    // then
    assertThat(job).asString().contains("543").contains(Duration.ofMinutes(30).toString());
  }
}
