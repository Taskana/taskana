package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.TaskanaEngineTestConfiguration;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.invocation.InvocationOnMock;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.internal.jobs.PlainJavaTransactionProvider;

class JobRunnerAccTest extends AbstractAccTest {

  private final JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();

  @Test
  void should_onlyExecuteJobOnce_When_MultipleThreadsTryToRunJobsAtTheSameTime() throws Exception {
    resetDb(true); // for some reason clearing the job table is not enough..

    assertThat(jobService.findJobsToRun()).isEmpty();
    ScheduledJob job = createJob(Instant.now().minus(5, ChronoUnit.MINUTES));
    assertThat(jobService.findJobsToRun()).containsExactly(job);

    runInThread(
        () -> {
          try {
            TaskanaEngine taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
            taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
            // We have to slow down the sql session, so that the transaction takes longer.
            // This is necessary to guarantee the execution of both test threads
            // and therefore test the database lock.
            // Without the slow down the test threads would execute too fast and
            // would not request executable jobs from the database at the same time.
            slowDownSqlSession(taskanaEngine);
            PlainJavaTransactionProvider transactionProvider =
                new PlainJavaTransactionProvider(
                    taskanaEngine, TaskanaEngineTestConfiguration.getDataSource());
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
        getJobMapper().findJobsToRun(Instant.now().plus(2, ChronoUnit.DAYS));

    assertThat(jobsToRun).hasSize(1).doesNotContain(job);
  }

  private void runInThread(Runnable runnable, int threadCount) throws Exception {
    Thread[] threads = new Thread[threadCount];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(runnable);
      threads[i].start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  private ScheduledJob createJob(Instant firstDue) {
    ScheduledJob job = new ScheduledJob();
    job.setType(ScheduledJob.Type.TASK_CLEANUP_JOB);
    job.setDue(firstDue);
    jobService.createJob(job);
    return job;
  }

  private void slowDownSqlSession(TaskanaEngine taskanaEngine) throws Exception {
    SqlSession sqlSession = Mockito.spy(taskanaEngine.getSqlSession());
    Field field = TaskanaEngineImpl.class.getDeclaredField("sessionManager");
    field.setAccessible(true);
    field.set(taskanaEngine, sqlSession);
    Mockito.doAnswer(new CallsRealMethodsWithDelay(100)).when(sqlSession).commit();
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
