package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
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
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance tests for all claim and complete scenarios. */
@ExtendWith(JaasExtension.class)
class TerminateTaskAccTest extends AbstractAccTest {
  private TaskService taskService;

  TerminateTaskAccTest() {
    super();
    taskService = taskanaEngine.getTaskService();
  }

  @BeforeEach
  public static void setupTest() throws Exception {
    resetDb(false);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testQueryTerminatedTasks() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries.size()).isEqualTo(5);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testTerminateReadyTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    List<TaskSummary> taskSummaries = taskService.createTaskQuery().stateIn(TaskState.READY).list();
    assertThat(taskSummaries.size()).isEqualTo(47);
    Task task = taskService.getTask(taskSummaries.get(0).getId());
    taskService.terminateTask(taskSummaries.get(0).getId());
    long numTasks = taskService.createTaskQuery().stateIn(TaskState.READY).count();
    assertThat(numTasks).isEqualTo(46);
    numTasks = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasks).isEqualTo(6);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testTerminateClaimedTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CLAIMED).list();
    assertThat(taskSummaries.size()).isEqualTo(19);
    Task task = taskService.getTask(taskSummaries.get(0).getId());
    taskService.terminateTask(taskSummaries.get(0).getId());
    long numTasks = taskService.createTaskQuery().stateIn(TaskState.CLAIMED).count();
    assertThat(numTasks).isEqualTo(18);
    numTasks = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasks).isEqualTo(7);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testTerminateCompletedTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
    assertThat(taskSummaries.size()).isEqualTo(7);
    Task task = taskService.getTask(taskSummaries.get(0).getId());
    assertThatThrownBy(
      () -> {
        taskService.terminateTask(taskSummaries.get(0).getId());
      }).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testTerminateTerminatedTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries.size()).isEqualTo(5);
    Task task = taskService.getTask(taskSummaries.get(0).getId());
    assertThatThrownBy(
      () -> {
        taskService.terminateTask(taskSummaries.get(0).getId());
      }).isInstanceOf(InvalidStateException.class);
  }
}
