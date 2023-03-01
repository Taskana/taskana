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
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.internal.jobs.ClassificationChangedJob;
import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.task.internal.jobs.TaskRefreshJob;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.jobs.WorkbasketCleanupJob;

/** Acceptance test for all "jobs workbasket runner" scenarios. */
@ExtendWith(JaasExtension.class)
class WorkbasketCleanupJobAccTest extends AbstractAccTest {

  WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

  @AfterEach
  void after() throws Exception {
    resetDb(true);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldCleanWorkbasketMarkedForDeletionWithoutTasks() throws Exception {
    long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
    assertThat(totalWorkbasketCount).isEqualTo(26);
    List<WorkbasketSummary> workbaskets =
        workbasketService
            .createWorkbasketQuery()
            .keyIn("TEAMLEAD-1")
            .orderByKey(BaseQuery.SortDirection.ASCENDING)
            .list();

    assertThat(getNumberTaskNotCompleted(workbaskets.get(0).getId())).isZero();
    assertThat(getNumberTaskCompleted(workbaskets.get(0).getId())).isOne();

    // Workbasket with completed task will be marked for deletion.
    workbasketService.deleteWorkbasket(workbaskets.get(0).getId());

    // Run taskCleanupJob for deleting completing tasks before running workbasketCleanupJob
    TaskCleanupJob taskCleanupJob = new TaskCleanupJob(taskanaEngine, null, null);
    taskCleanupJob.run();

    assertThat(getNumberTaskCompleted(workbaskets.get(0).getId())).isZero();

    WorkbasketCleanupJob workbasketCleanupJob = new WorkbasketCleanupJob(taskanaEngine, null, null);
    workbasketCleanupJob.run();

    totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
    assertThat(totalWorkbasketCount).isEqualTo(25);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldNotCleanWorkbasketMarkedForDeletionIfWorkbasketHasTasks() throws Exception {
    long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
    assertThat(totalWorkbasketCount).isEqualTo(26);
    List<WorkbasketSummary> workbaskets =
        workbasketService
            .createWorkbasketQuery()
            .keyIn("TEAMLEAD-1")
            .orderByKey(BaseQuery.SortDirection.ASCENDING)
            .list();

    assertThat(getNumberTaskCompleted(workbaskets.get(0).getId())).isPositive();

    // Workbasket with completed task will be marked for deletion.
    workbasketService.deleteWorkbasket(workbaskets.get(0).getId());

    WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
    job.run();

    totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
    assertThat(totalWorkbasketCount).isEqualTo(26);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteOldWorkbasketCleanupJobs_When_InitializingSchedule() throws Exception {

    for (int i = 0; i < 10; i++) {
      ScheduledJob job = new ScheduledJob();
      job.setType(WorkbasketCleanupJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
      job.setType(TaskRefreshJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
      job.setType(ClassificationChangedJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
    }

    List<ScheduledJob> jobsToRun = getJobMapper(taskanaEngine).findJobsToRun(Instant.now());

    assertThat(jobsToRun).hasSize(30);

    List<ScheduledJob> workbasketCleanupJobs =
        jobsToRun.stream()
            .filter(
                scheduledJob -> scheduledJob.getType().equals(WorkbasketCleanupJob.class.getName()))
            .collect(Collectors.toList());

    AbstractTaskanaJob.initializeSchedule(taskanaEngine, WorkbasketCleanupJob.class);

    jobsToRun = getJobMapper(taskanaEngine).findJobsToRun(Instant.now());

    assertThat(jobsToRun).doesNotContainAnyElementsOf(workbasketCleanupJobs);
  }

  private long getNumberTaskNotCompleted(String workbasketId) {
    return taskService
        .createTaskQuery()
        .workbasketIdIn(workbasketId)
        .stateNotIn(TaskState.COMPLETED)
        .count();
  }

  private long getNumberTaskCompleted(String workbasketId) {
    return taskService
        .createTaskQuery()
        .workbasketIdIn(workbasketId)
        .stateIn(TaskState.COMPLETED)
        .count();
  }
}
