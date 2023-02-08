package acceptance.task.complete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance tests for "terminate task" scenarios. */
@ExtendWith(JaasExtension.class)
class TerminateTaskAccTest extends AbstractAccTest {

  @BeforeEach
  void setupIndividualTest() throws Exception {
    resetDb(false);
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
    assertThat(taskSummaries).hasSize(48);
    taskService.terminateTask(taskSummaries.get(0).getId());
    long numTasks = taskService.createTaskQuery().stateIn(TaskState.READY).count();
    assertThat(numTasks).isEqualTo(47);
    numTasks = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasks).isEqualTo(6);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TerminateTask_When_TaskStateIsClaimed() throws Exception {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CLAIMED).list();
    assertThat(taskSummaries).hasSize(21);

    long numTasksTerminated = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasksTerminated).isEqualTo(5);

    taskService.terminateTask(taskSummaries.get(0).getId());
    long numTasksClaimed = taskService.createTaskQuery().stateIn(TaskState.CLAIMED).count();
    assertThat(numTasksClaimed).isEqualTo(20);
    numTasksTerminated = taskService.createTaskQuery().stateIn(TaskState.TERMINATED).count();
    assertThat(numTasksTerminated).isEqualTo(6);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_TerminateTaskWithTaskStateCompleted() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
    assertThat(taskSummaries).hasSize(10);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidTaskStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @WithAccessId(user = "user-taskrouter")
  @TestTemplate
  void should_ThrowException_When_UserIsNotInAdministrativeRole() {
    ThrowingCallable taskanaCall =
        () -> taskService.terminateTask("TKI:000000000000000000000000000000000000");

    assertThatThrownBy(taskanaCall).isInstanceOf(MismatchedRoleException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_TerminateTaskWithTaskStateTerminated() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.TERMINATED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());

    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidTaskStateException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_TerminateTaskWithTaskStateCancelled() {
    List<TaskSummary> taskSummaries =
        taskService.createTaskQuery().stateIn(TaskState.CANCELLED).list();
    assertThat(taskSummaries).hasSize(5);
    ThrowingCallable taskanaCall = () -> taskService.terminateTask(taskSummaries.get(0).getId());
    assertThatThrownBy(taskanaCall).isInstanceOf(InvalidTaskStateException.class);
  }
}
