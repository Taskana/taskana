package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance tests for all "cancel task" scenarios. */
@ExtendWith(JaasExtension.class)
class CancelTaskAccTest extends AbstractAccTest {

  private TaskService taskService;

  CancelTaskAccTest() {
    super();
    taskService = taskanaEngine.getTaskService();
  }

  @BeforeEach
  void setupIndividualTest() throws Exception {
    resetDb(false);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testQueryCancelledTasks() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CANCELLED).list();
    assertThat(taskSummaries).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @Test
  void testCancelReadyTask() throws Exception {
    List<TaskSummary> taskSummaries = taskService.createTaskQuery().stateIn(TaskState.READY).list();
    assertThat(taskSummaries).hasSize(48);
    taskService.cancelTask(taskSummaries.get(0).getId());
    long numTasks = taskService.createTaskQuery().stateIn(TaskState.READY).count();
    assertThat(numTasks).isEqualTo(47);
    numTasks = taskService.createTaskQuery().stateIn(TaskState.CANCELLED).count();
    assertThat(numTasks).isEqualTo(6);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_CancelTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    resetDb(false);
    Task tasktoCancel = taskService.getTask("TKI:000000000000000000000000000000000001");
    assertThat(tasktoCancel.getState()).isEqualTo(TaskState.CLAIMED);

    Task cancelledTask = taskService.cancelTask(tasktoCancel.getId());
    assertThat(cancelledTask.getState()).isEqualTo(TaskState.CANCELLED);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testCancelClaimedTask() throws Exception {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CLAIMED).list();

    long numTasksCancelled = taskService.createTaskQuery().stateIn(TaskState.CANCELLED).count();

    taskService.cancelTask(taskSummaries.get(0).getId());
    long numTasksClaimed = taskService.createTaskQuery().stateIn(TaskState.CLAIMED).count();
    assertThat(numTasksClaimed).isEqualTo(taskSummaries.size() - 1);
    long newNumTasksCancelled = taskService.createTaskQuery().stateIn(TaskState.CANCELLED).count();
    assertThat(newNumTasksCancelled).isEqualTo(numTasksCancelled + 1);
  }

  @WithAccessId(user = "admin")
  @Test
  void testCancelCompletedTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
    assertThat(taskSummaries).hasSize(10);

    ThrowingCallable taskanaCall = () -> taskService.cancelTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testCancelTerminatedTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.cancelTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testCancelCancelledTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CANCELLED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.cancelTask(taskSummaries.get(0).getId());
    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }
}
