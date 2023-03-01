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
import static org.assertj.core.api.Assertions.assertThatCode;

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
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
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

    taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .priorityJobActive(true)
            .priorityJobBatchSize(20)
            .priorityJobRunEvery(Duration.ofMinutes(30))
            .priorityJobFirstRun(Instant.parse("2007-12-03T10:15:30.00Z"))
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
  void should_doNothing_When_NotActive() throws Exception {
    // given
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .priorityJobActive(false)
            .build();
    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaConfiguration);
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
            .priorityJobBatchSize(20)
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaConfiguration));

    // then
    assertThat(job.getBatchSize()).isEqualTo(20);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_readConfigurationForIsActive() throws Exception {
    // given
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .priorityJobActive(false)
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaConfiguration));

    // then
    assertThat(job.isJobActive()).isFalse();
  }

  @Test
  void should_containInformation_When_convertedToString() throws Exception {
    // given
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .priorityJobBatchSize(543)
            .priorityJobRunEvery(Duration.ofMinutes(30))
            .build();

    // when
    final TaskUpdatePriorityJob job =
        new TaskUpdatePriorityJob(TaskanaEngine.buildTaskanaEngine(taskanaConfiguration));

    // then
    assertThat(job).asString().contains("543").contains(Duration.ofMinutes(30).toString());
  }
}
