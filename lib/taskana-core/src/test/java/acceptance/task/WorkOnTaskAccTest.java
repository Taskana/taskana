package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;

/** Acceptance test for all "work on task" scenarios. This includes claim, complete... */
@ExtendWith(JaasExtension.class)
class WorkOnTaskAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-2")
  @Test
  void testClaimTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000025");

    taskService.claim(task.getId());

    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000025");
    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(claimedTask.getModified()).isNotEqualTo(claimedTask.getCreated());
    assertThat(claimedTask.getModified()).isEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testThrowsExceptionIfTaskIsAlreadyClaimed()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000026");

    ThrowingCallable call =
        () -> {
          taskService.claim(task.getId());
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testClaimAlreadyClaimedByCallerTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

    taskService.claim(task.getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testForceClaimTaskWhichIsAlreadyClaimedByAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000028");

    ThrowingCallable call =
        () -> {
          taskService.claim(task.getId());
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testCancelClaimTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000029");

    taskService.cancelClaim(claimedTask.getId());

    Task unclaimedTask = taskService.getTask("TKI:000000000000000000000000000000000029");
    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testThrowsExceptionIfCancelClaimOfTaskFromAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000030");

    ThrowingCallable call =
        () -> {
          taskService.cancelClaim(claimedTask.getId());
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testForceCancelClaimOfTaskFromAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000031");

    taskService.forceCancelClaim(claimedTask.getId());

    Task unclaimedTask = taskService.getTask("TKI:000000000000000000000000000000000031");
    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testCompleteTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    final Instant before = Instant.now().minus(Duration.ofSeconds(3L));
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000032");

    taskService.completeTask(claimedTask.getId());

    Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000032");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getModified()).isEqualTo(completedTask.getCompleted());
    assertThat(completedTask.getCompleted().isAfter(before)).isTrue();
    assertThat(completedTask.getModified().isAfter(before)).isTrue();
    assertThat(completedTask.isRead()).isTrue();
    assertThat(completedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testForceCompleteUnclaimedTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000033");

    taskService.forceCompleteTask(claimedTask.getId());

    Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000033");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getModified()).isEqualTo(completedTask.getCompleted());
    assertThat(completedTask.isRead()).isTrue();
    assertThat(completedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testThrowsExceptionIfCompletingClaimedTaskOfAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000034");

    ThrowingCallable call =
        () -> {
          taskService.completeTask(claimedTask.getId());
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testForceCompleteClaimedTaskOfAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000035");

    taskService.forceCompleteTask(claimedTask.getId());

    Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000035");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getModified()).isEqualTo(completedTask.getCompleted());
    assertThat(completedTask.isRead()).isTrue();
    assertThat(completedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "admin")
  @Test
  void testBulkDeleteTasksWithException() throws InvalidArgumentException, NotAuthorizedException {

    TaskService taskService = taskanaEngine.getTaskService();
    String id1 = "TKI:000000000000000000000000000000000102";
    String id2 = "TKI:000000000000000000000000000000003333";
    List<String> taskIdList = Arrays.asList(id1, id2);

    BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id1, id2);
    assertThat(results.getErrorForId(id1)).isInstanceOf(InvalidStateException.class);
    assertThat(results.getErrorForId(id2)).isInstanceOf(TaskNotFoundException.class);
  }
}
