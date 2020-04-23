package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance tests for all claim and complete scenarios. */
@ExtendWith(JaasExtension.class)
class TerminateTaskAccTest extends AbstractAccTest {
  private static TaskService taskService;

  @BeforeEach
  public void setupIndividualTest() throws Exception {
    resetDb(false);
  }

  @BeforeAll
  static void setup() {
    taskService = taskanaEngine.getTaskService();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testQueryTerminatedTasks() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries).hasSize(5);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testTerminateReadyTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    List<TaskSummary> taskSummaries = taskService.createTaskQuery().stateIn(TaskState.READY).list();
    assertThat(taskSummaries).hasSize(47);
    taskService.terminateTask(taskSummaries.get(0).getId());
    long numTasks = taskService.createTaskQuery().stateIn(TaskState.READY).count();
    assertThat(numTasks).isEqualTo(46);
    numTasks = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasks).isEqualTo(6);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testTerminateClaimedTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CLAIMED).list();
    assertThat(taskSummaries).hasSize(16);

    long numTasksTerminated = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasksTerminated).isEqualTo(5);

    taskService.terminateTask(taskSummaries.get(0).getId());
    long numTasksClaimed = taskService.createTaskQuery().stateIn(TaskState.CLAIMED).count();
    assertThat(numTasksClaimed).isEqualTo(15);
    numTasksTerminated = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasksTerminated).isEqualTo(6);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testTerminateCompletedTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
    assertThat(taskSummaries).hasSize(6);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testTerminateTerminatedTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testTerminateCancelledTask() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CANCELLED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());
    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }
}
