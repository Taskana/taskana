package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.jobs.TaskCleanupJob;
import pro.taskana.common.internal.jobs.WorkbasketCleanupJob;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "jobs workbasket runner" scenarios. */
@ExtendWith(JaasExtension.class)
class WorkbasketCleanupJobAccTest extends AbstractAccTest {

  WorkbasketService workbasketService;
  TaskService taskService;

  @BeforeEach
  void before() {
    workbasketService = taskanaEngine.getWorkbasketService();
    taskService = taskanaEngine.getTaskService();
  }

  @AfterEach
  void after() throws Exception {
    resetDb(true);
  }

  @WithAccessId(userName = "admin")
  @Test
  void shouldCleanWorkbasketMarkedForDeletionWithoutTasks() throws TaskanaException {
    long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
    assertThat(totalWorkbasketCount).isEqualTo(25);
    List<WorkbasketSummary> workbaskets =
        workbasketService
            .createWorkbasketQuery()
            .keyIn("TEAMLEAD_1")
            .orderByKey(BaseQuery.SortDirection.ASCENDING)
            .list();

    assertThat(0).isEqualTo(getNumberTaskNotCompleted(workbaskets.get(0).getId()));
    assertThat(1).isEqualTo(getNumberTaskCompleted(workbaskets.get(0).getId()));

    // Workbasket with completed task will be marked for deletion.
    workbasketService.deleteWorkbasket(workbaskets.get(0).getId());

    // Run taskCleanupJob for deleting completing tasks before running workbasketCleanupJob
    TaskCleanupJob taskCleanupJob = new TaskCleanupJob(taskanaEngine, null, null);
    taskCleanupJob.run();

    assertThat(0).isEqualTo(getNumberTaskCompleted(workbaskets.get(0).getId()));

    WorkbasketCleanupJob workbasketCleanupJob = new WorkbasketCleanupJob(taskanaEngine, null, null);
    workbasketCleanupJob.run();

    totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
    assertThat(totalWorkbasketCount).isEqualTo(24);
  }

  @WithAccessId(userName = "admin")
  @Test
  void shouldNotCleanWorkbasketMarkedForDeletionIfWorkbasketHasTasks() throws Exception {
    long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
    assertThat(totalWorkbasketCount).isEqualTo(25);
    List<WorkbasketSummary> workbaskets =
        workbasketService
            .createWorkbasketQuery()
            .keyIn("TEAMLEAD_1")
            .orderByKey(BaseQuery.SortDirection.ASCENDING)
            .list();

    assertThat(0).isNotEqualTo(getNumberTaskCompleted(workbaskets.get(0).getId()));

    // Workbasket with completed task will be marked for deletion.
    workbasketService.deleteWorkbasket(workbaskets.get(0).getId());

    WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
    job.run();

    totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
    assertThat(totalWorkbasketCount).isEqualTo(25);
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
