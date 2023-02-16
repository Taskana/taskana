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

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.internal.jobs.TaskUpdatePriorityJob;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskUpdatePriorityJobAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(true);

    taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .priorityJobActive(true)
            .priorityJobBatchSize(20)
            .priorityJobRunEvery(Duration.ofMinutes(30))
            .priorityJobFirstRun(Instant.parse("2007-12-03T10:15:30.00Z"))
            .build();
    taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration);
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
  void should_catchException_When_executedWithWrongSettings() throws Exception {
    // given
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .priorityJobBatchSize(0)
            .build();
    TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration));

    // then
    assertThatThrownBy(job::execute).isInstanceOf(SystemException.class);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_doNothing_When_NotActive() throws Exception {
    // given
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .priorityJobActive(false)
            .build();
    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration);
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
  void should_ScheduleNextJob() throws Exception {
    // given
    final Instant someTimeInTheFuture = Instant.now().plus(10, ChronoUnit.DAYS);
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration).build();
    TaskanaEngine taskanaEngine =
        TaskanaEngine.buildTaskanaEngine(
            taskanaEngineConfiguration, ConnectionManagementMode.AUTOCOMMIT);
    // when
    TaskUpdatePriorityJob.initializeSchedule(taskanaEngine);

    // then
    assertThat(getJobMapper(taskanaEngine).findJobsToRun(someTimeInTheFuture))
        .hasSizeGreaterThanOrEqualTo(1)
        .extracting(ScheduledJob::getType)
        .contains(TaskUpdatePriorityJob.class.getName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_readConfigurationForBatchSize() throws Exception {
    // given
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .priorityJobBatchSize(20)
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration));

    // then
    assertThat(job.getBatchSize()).isEqualTo(20);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_readConfigurationForIsActive() throws Exception {
    // given
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .priorityJobActive(false)
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration));

    // then
    assertThat(job.isJobActive()).isFalse();
  }

  @Test
  void should_containInformation_When_convertedToString() throws Exception {
    // given
    TaskanaConfiguration taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaEngineConfiguration)
            .priorityJobBatchSize(543)
            .priorityJobRunEvery(Duration.ofMinutes(30))
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration));

    // then
    assertThat(job).asString().contains("543").contains(Duration.ofMinutes(30).toString());
  }
}
