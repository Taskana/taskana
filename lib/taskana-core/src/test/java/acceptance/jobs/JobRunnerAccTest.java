/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.sql.Connection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.common.test.util.ParallelThreadHelper;
import pro.taskana.task.internal.jobs.TaskCleanupJob;

@Disabled
class JobRunnerAccTest extends AbstractAccTest {

  private final JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();

  @Test
  void should_onlyExecuteJobOnce_When_MultipleThreadsTryToRunJobsAtTheSameTime() throws Exception {
    resetDb(true); // for some reason clearing the job table is not enough..

    assertThat(jobService.findJobsToRun()).isEmpty();
    ScheduledJob job = createJob(Instant.now().minus(5, ChronoUnit.MINUTES));
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

  private ScheduledJob createJob(Instant firstDue) {
    ScheduledJob job = new ScheduledJob();
    job.setType(TaskCleanupJob.class.getName());
    job.setDue(firstDue);
    jobService.createJob(job);
    return job;
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
