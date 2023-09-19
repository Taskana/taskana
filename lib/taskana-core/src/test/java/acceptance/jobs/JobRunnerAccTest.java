package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.invocation.InvocationOnMock;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.internal.jobs.PlainJavaTransactionProvider;
import pro.taskana.common.internal.util.CheckedConsumer;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.common.test.util.ParallelThreadHelper;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.task.internal.jobs.TaskUpdatePriorityJob;
import pro.taskana.workbasket.internal.jobs.WorkbasketCleanupJob;

class JobRunnerAccTest extends AbstractAccTest {

  private final JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
  private static final Duration TASK_CLEANUP_JOB_LOCK_EXPIRATION_PERIOD = Duration.ofMinutes(4);
  private static final Duration WORKBASKET_CLEANUP_JOB_LOCK_EXPIRATION_PERIOD =
      Duration.ofMinutes(3);
  private static final Duration TASK_UPDATE_PRIORITY_LOCK_EXPIRATION_PERIOD = Duration.ofMinutes(1);

  @Disabled
  @Test
  void should_onlyExecuteJobOnce_When_MultipleThreadsTryToRunJobsAtTheSameTime() throws Exception {
    resetDb(true); // for some reason clearing the job table is not enough..

    assertThat(jobService.findJobsToRun()).isEmpty();
    ScheduledJob job =
        createJob(Instant.now().minus(5, ChronoUnit.MINUTES), TaskCleanupJob.class.getName());
    assertThat(jobService.findJobsToRun()).containsExactly(job);

    ParallelThreadHelper.runInThread(
        () -> {
          try {
            TaskanaEngine taskanaEngine =
                TaskanaEngine.buildTaskanaEngine(
                    taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
            DataSource dataSource = DataSourceGenerator.getDataSource();
            // We have to slow down the transaction.
            // This is necessary to guarantee the execution of
            // both test threads and therefore test the database lock.
            // Without the slow down the test threads would execute too fast and
            // would not request executable jobs from the database at the same time.
            // TODO: please fix this. With the slowdown the test suite fails often
            // dataSource = slowDownDatabaseTransaction(dataSource);
            PlainJavaTransactionProvider transactionProvider =
                new PlainJavaTransactionProvider(taskanaEngine, dataSource);
            JobRunner runner = new JobRunner(taskanaEngine);
            runner.registerTransactionProvider(transactionProvider);
            runner.runJobs();
          } catch (Exception e) {
            throw new SystemException("Caught Exception", e);
          }
        },
        2);

    // runEvery is set to P1D Therefore we need to check which jobs run tomorrow.
    // Just to be sure the jobs are found we will look for any job scheduled in the next 2 days.
    List<ScheduledJob> jobsToRun =
        getJobMapper(taskanaEngine).findJobsToRun(Instant.now().plus(2, ChronoUnit.DAYS));

    assertThat(jobsToRun).hasSize(1).doesNotContain(job);
  }

  @TestFactory
  Stream<DynamicTest> should_setTheLockExpirationDateCorrectly_When_CreatingJobs() {
    List<Pair<String, Duration>> list =
        List.of(
            Pair.of(TaskCleanupJob.class.getName(), TASK_CLEANUP_JOB_LOCK_EXPIRATION_PERIOD),
            Pair.of(
                TaskUpdatePriorityJob.class.getName(), TASK_UPDATE_PRIORITY_LOCK_EXPIRATION_PERIOD),
            Pair.of(
                WorkbasketCleanupJob.class.getName(),
                WORKBASKET_CLEANUP_JOB_LOCK_EXPIRATION_PERIOD));
    ThrowingConsumer<Pair<String, Duration>> testSettingLockExpirationDate =
        p -> {
          resetDb(true); // for some reason clearing the job table is not enough..
          assertThat(jobService.findJobsToRun()).isEmpty();
          createJob(Instant.now().minus(5, ChronoUnit.MINUTES), p.getLeft());
          ParallelThreadHelper.runInThread(
              () -> {
                TaskanaEngine taskanaEngine;
                try {
                  taskanaEngine =
                      TaskanaEngine.buildTaskanaEngine(
                          taskanaConfiguration, ConnectionManagementMode.AUTOCOMMIT);
                } catch (SQLException e) {
                  throw new RuntimeException("Could not build the TaskanaEngine");
                }
                DataSource dataSource = DataSourceGenerator.getDataSource();
                PlainJavaTransactionProvider transactionProvider =
                    new PlainJavaTransactionProvider(taskanaEngine, dataSource);
                JobRunner runner = new JobRunner(taskanaEngine);
                runner.registerTransactionProvider(transactionProvider);
                runner.runJobs();
              },
              1);
          List<ScheduledJob> resultJobs =
              getJobMapper(taskanaEngine).findJobsToRun(Instant.now().plus(2, ChronoUnit.DAYS));
          assertThat(resultJobs).hasSize(1);
          assertThat(resultJobs.get(0).getType()).isEqualTo(p.getLeft());
          assertThat(resultJobs.get(0).getLockExpires())
              .isBetween(
                  resultJobs.get(0).getCreated().plus(p.getRight()),
                  resultJobs.get(0).getCreated().plus(p.getRight()).plusSeconds(1));
        };
    return DynamicTest.stream(list.iterator(), Pair::getLeft, testSettingLockExpirationDate);
  }

  private ScheduledJob createJob(Instant firstDue, String type) {
    ScheduledJob job = new ScheduledJob();
    job.setType(type);
    job.setDue(firstDue);
    jobService.createJob(job);
    return job;
  }

  private Runnable getRunnableTest(
      TaskanaEngine taskanaEngine, PlainJavaTransactionProvider transactionProvider) {
    return () -> {
      Consumer<JobRunner> consumer =
          CheckedConsumer.wrap(
              jobRunner -> {
                jobRunner.registerTransactionProvider(transactionProvider);
                jobRunner.runJobs();
              });
      PrivilegedAction<Void> action =
          () -> {
            consumer.accept(new JobRunner(taskanaEngine));
            return null;
          };
    };
  }

  private DataSource slowDownDatabaseTransaction(DataSource dataSource) throws Exception {
    dataSource = Mockito.spy(dataSource);
    Mockito.doAnswer(
            invocationOnMock -> {
              Connection connection = (Connection) invocationOnMock.callRealMethod();
              connection = Mockito.spy(connection);
              Mockito.doAnswer(new CallsRealMethodsWithDelay(100)).when(connection).commit();
              return connection;
            })
        .when(dataSource)
        .getConnection();
    return dataSource;
  }

  private static class CallsRealMethodsWithDelay extends CallsRealMethods {
    private final int delay;

    private CallsRealMethodsWithDelay(int delay) {
      this.delay = delay;
    }

    @Override
    public Object answer(InvocationOnMock invocation) throws Throwable {
      Thread.sleep(delay);
      return super.answer(invocation);
    }
  }
}
