package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance tests for all claim and complete scenarios. */
@ExtendWith(JaasExtension.class)
class CancelTaskAccTest extends AbstractAccTest {
  private TaskService taskService;

  CancelTaskAccTest() {
    super();
    taskService = taskanaEngine.getTaskService();
  }

  @BeforeEach
  public void setupIndividualTest() throws Exception {
    resetDb(false);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testQeryCancelledTasks() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CANCELLED).list();
    assertThat(taskSummaries).hasSize(5);
  }

  @WithAccessId(user = "admin", groups = "group_1")
  @Test
  void testCancelReadyTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    List<TaskSummary> taskSummaries = taskService.createTaskQuery().stateIn(TaskState.READY).list();
    assertThat(taskSummaries).hasSize(47);
    taskService.cancelTask(taskSummaries.get(0).getId());
    long numTasks = taskService.createTaskQuery().stateIn(TaskState.READY).count();
    assertThat(numTasks).isEqualTo(46);
    numTasks = taskService.createTaskQuery().stateIn(TaskState.CANCELLED).count();
    assertThat(numTasks).isEqualTo(6);
  }

  @WithAccessId(user = "user_1_2", groups = "group_1")
  @Test
  void testCancelClaimedTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CLAIMED).list();
    assertThat(taskSummaries).hasSize(16);

    long numTasksCancelled = taskService.createTaskQuery().stateIn(TaskState.CANCELLED).count();
    assertThat(numTasksCancelled).isEqualTo(5);

    taskService.cancelTask(taskSummaries.get(0).getId());
    long numTasksClaimed = taskService.createTaskQuery().stateIn(TaskState.CLAIMED).count();
    assertThat(numTasksClaimed).isEqualTo(15);
    numTasksCancelled = taskService.createTaskQuery().stateIn(TaskState.CANCELLED).count();
    assertThat(numTasksCancelled).isEqualTo(6);
  }

  @WithAccessId(user = "admin", groups = "group_1")
  @Test
  void testCancelCompletedTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
    assertThat(taskSummaries).hasSize(7);

    ThrowingCallable taskanaCall = () -> taskService.cancelTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user_1_2", groups = "group_1")
  @Test
  void testCancelTerminatedTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.cancelTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user_1_2", groups = "group_1")
  @Test
  void testCancelCancelledTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CANCELLED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.cancelTask(taskSummaries.get(0).getId());
    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }
}
