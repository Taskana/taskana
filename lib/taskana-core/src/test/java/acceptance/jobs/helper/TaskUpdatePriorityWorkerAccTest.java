package acceptance.jobs.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import acceptance.AbstractAccTest;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.jobs.helper.TaskUpdatePriorityWorker;
import pro.taskana.task.internal.models.TaskImpl;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskUpdatePriorityWorkerAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(true);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_loadAnyRelevantTaskId() {
    // given
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(taskanaEngine);

    // when
    final List<String> allRelevantTaskIds = worker.getAllRelevantTaskIds();

    // then
    assertThat(allRelevantTaskIds).hasSizeGreaterThan(0);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_loadExistingTaskIds() {
    // given
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(taskanaEngine);
    final List<String> allRelevantTaskIds = worker.getAllRelevantTaskIds();
    final String foundTaskId = allRelevantTaskIds.get(0);

    // when
    final List<TaskSummary> taskSummariesByIds = worker.getTaskSummariesByIds(List.of(foundTaskId));

    // then
    assertThat(taskSummariesByIds)
        .hasSize(1)
        .extracting(TaskSummary::getId)
        .containsExactly(foundTaskId);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_notLoadAnyIrrelevantTaskIds() {
    // given
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(taskanaEngine);
    String completedTaskId = "TKI:000000000000000000000000000000000038";

    // when
    final List<String> allRelevantTaskIds = worker.getAllRelevantTaskIds();

    // then
    assertThat(allRelevantTaskIds).isNotEmpty().doesNotContain(completedTaskId);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_loadExistingTaskSummariesById() {
    // given
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(taskanaEngine);
    String taskId1 = "TKI:000000000000000000000000000000000050";
    String taskId2 = "TKI:000000000000000000000000000000000051";

    // when
    final List<TaskSummary> taskSummariesByIds =
        worker.getTaskSummariesByIds(List.of(taskId1, taskId2));

    // then
    assertThat(taskSummariesByIds)
        .hasSizeGreaterThan(0)
        .extracting(TaskSummary::getId)
        .containsExactlyInAnyOrder(taskId1, taskId2);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_executeBatch() throws TaskNotFoundException, NotAuthorizedException {
    // given
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(taskanaEngine);
    final List<String> allRelevantTaskIds = worker.getAllRelevantTaskIds();
    String taskId = "TKI:000000000000000000000000000000000050";
    Task taskOld = taskanaEngine.getTaskService().getTask(taskId);

    // when
    final List<String> updatedTaskIds = worker.executeBatch(allRelevantTaskIds);

    // then
    final Task taskUpdated = taskanaEngine.getTaskService().getTask(taskId);

    assumeThat(PriorityServiceManager.getInstance().countRegisteredServices())
        .describedAs("SPI should be provided in order to check for modified priorities.")
        .isPositive();

    assertThat(updatedTaskIds).contains(taskId);
    assertThat(taskUpdated.getPriority()).isNotEqualTo(taskOld.getPriority());
  }

  @Test
  void should_noticeDifferentPriority_When_PriorityHasChanged() {
    // given
    final TaskImpl task = (TaskImpl) taskanaEngine.getTaskService().newTask();
    task.setPriority(232);

    // when
    final Predicate<Integer> differentPriority =
        TaskUpdatePriorityWorker.hasDifferentPriority(task);

    // then
    assertThat(differentPriority).rejects(232).accepts(2, 3, 4, 5);
  }
}
