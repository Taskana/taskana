package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.classification.internal.jobs.ClassificationChangedJob;
import io.kadai.common.api.BaseQuery;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.task.api.TaskState;
import io.kadai.task.internal.jobs.TaskCleanupJob;
import io.kadai.task.internal.jobs.TaskRefreshJob;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import io.kadai.workbasket.internal.jobs.WorkbasketCleanupJob;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "jobs workbasket runner" scenarios. */
@ExtendWith(JaasExtension.class)
class WorkbasketCleanupJobAccTest extends AbstractAccTest {

  WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

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
    TaskCleanupJob taskCleanupJob = new TaskCleanupJob(kadaiEngine, null, null);
    taskCleanupJob.run();

    assertThat(getNumberTaskCompleted(workbaskets.get(0).getId())).isZero();

    WorkbasketCleanupJob workbasketCleanupJob = new WorkbasketCleanupJob(kadaiEngine, null, null);
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

    WorkbasketCleanupJob job = new WorkbasketCleanupJob(kadaiEngine, null, null);
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
      kadaiEngine.getJobService().createJob(job);
      job.setType(TaskRefreshJob.class.getName());
      kadaiEngine.getJobService().createJob(job);
      job.setType(ClassificationChangedJob.class.getName());
      kadaiEngine.getJobService().createJob(job);
    }

    List<ScheduledJob> jobsToRun = getJobMapper(kadaiEngine).findJobsToRun(Instant.now());

    assertThat(jobsToRun).hasSize(30);

    List<ScheduledJob> workbasketCleanupJobs =
        jobsToRun.stream()
            .filter(
                scheduledJob -> scheduledJob.getType().equals(WorkbasketCleanupJob.class.getName()))
            .collect(Collectors.toList());

    AbstractKadaiJob.initializeSchedule(kadaiEngine, WorkbasketCleanupJob.class);

    jobsToRun = getJobMapper(kadaiEngine).findJobsToRun(Instant.now());

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
