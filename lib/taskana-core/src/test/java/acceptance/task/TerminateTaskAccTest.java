package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance tests for "terminate task" scenarios. */
@ExtendWith(JaasExtension.class)
class TerminateTaskAccTest extends AbstractAccTest {
  private static TaskService taskService;

  @BeforeEach
  void setupIndividualTest() throws Exception {
    resetDb(false);
  }

  @BeforeAll
  static void setup() {
    taskService = taskanaEngine.getTaskService();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ReturnAllTerminatedTasks_When_QueryTerminatedState() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TerminateTask_When_TaskStateIsReady() throws Exception {
    List<TaskSummary> taskSummaries = taskService.createTaskQuery().stateIn(TaskState.READY).list();
    assertThat(taskSummaries).hasSize(47);
    taskService.terminateTask(taskSummaries.get(0).getId());
    long numTasks = taskService.createTaskQuery().stateIn(TaskState.READY).count();
    assertThat(numTasks).isEqualTo(46);
    numTasks = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasks).isEqualTo(6);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TerminateTask_When_TaskStateIsClaimed() throws Exception {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CLAIMED).list();
    assertThat(taskSummaries.size()).isEqualTo(20);

    long numTasksTerminated = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasksTerminated).isEqualTo(5);

    taskService.terminateTask(taskSummaries.get(0).getId());
    long numTasksClaimed = taskService.createTaskQuery().stateIn(TaskState.CLAIMED).count();
    assertThat(numTasksClaimed).isEqualTo(19);
    numTasksTerminated = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasksTerminated).isEqualTo(6);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_TerminateTaskWithTaskStateCompleted() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
    assertThat(taskSummaries.size()).isEqualTo(10);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_UserIsNotInAdministrativeRole() {

    ThrowingCallable taskanaCall =
        () -> taskService.terminateTask("TKI:000000000000000000000000000000000000");

    assertThatThrownBy(taskanaCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_TerminateTaskWithTaskStateTerminated() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_TerminateTaskWithTaskStateCancelled() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CANCELLED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());
    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidStateException.class);
  }
}
