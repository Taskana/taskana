package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.time.Instant;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/**
 * Acceptance tests for all claim and complete scenarios.
 */
@ExtendWith(JaasExtension.class)
class CompleteTaskAccTest extends AbstractAccTest {

  CompleteTaskAccTest() {
    super();
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testCompleteTask()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
                 NotAuthorizedException {
    TaskService taskService = taskanaEngine.getTaskService();

    assertThat(taskService.getTask("TKI:000000000000000000000000000000000001").getState())
        .isEqualTo(TaskState.CLAIMED);
    Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000001");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getModified()).isNotEqualTo(completedTask.getCreated());
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ForceCompleteTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
                 NotAuthorizedException, SQLException {

    resetDb(false);
    TaskService taskService = taskanaEngine.getTaskService();

    assertThat(taskService.getTask("TKI:000000000000000000000000000000000000").getState())
        .isEqualTo(TaskState.CLAIMED);
    Task completedTask = taskService.forceCompleteTask("TKI:000000000000000000000000000000000000");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getModified()).isNotEqualTo(completedTask.getCreated());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testCompleteTaskTwice()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
                 NotAuthorizedException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000002");
    Task completedTask2 = taskService.completeTask("TKI:000000000000000000000000000000000002");
    assertThat(completedTask2).isEqualTo(completedTask);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testForceCompleteAlreadyClaimed()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
                 TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
                 InvalidOwnerException, InvalidStateException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setOwner("other");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    TaskImpl newTaskImpl = (TaskImpl) newTask;
    newTaskImpl.setState(TaskState.CLAIMED);
    newTaskImpl.setClaimed(Instant.now());

    Task createdTask = taskService.createTask(newTaskImpl);
    Task completedTask = taskService.forceCompleteTask(createdTask.getId());

    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(isBeforeOrEqual(completedTask.getCreated(), completedTask.getModified())).isTrue();
    assertThat(completedTask.getCompleted()).isEqualTo(completedTask.getModified());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testForceCompleteNotClaimed()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
                 TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
                 InvalidOwnerException, InvalidStateException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setOwner("other");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    TaskImpl newTaskImpl = (TaskImpl) newTask;
    newTaskImpl.setClaimed(Instant.now());

    Task createdTask = taskService.createTask(newTaskImpl);
    Task completedTask = taskService.forceCompleteTask(createdTask.getId());

    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(isBeforeOrEqual(completedTask.getCreated(), completedTask.getModified())).isTrue();
    assertThat(completedTask.getCompleted()).isEqualTo(completedTask.getModified());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testCompleteTaskThrowsErrors() {
    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable call =
        () -> {
          taskService.completeTask("TKI:0000000000000000000000000000000000xx");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);

    call =
        () -> {
          taskService.completeTask("TKI:000000000000000000000000000000000004");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);

    call =
        () -> {
          taskService.completeTask("TKI:000000000000000000000000000000000025");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);

    call =
        () -> {
          taskService.completeTask("TKI:000000000000000000000000000000000027");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testClaimTaskWithDefaultFlag()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
                 TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
                 InvalidStateException, InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner(null);
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getClaimed()).isNull();

    final Instant before = createdTask.getCreated();
    Task claimedTask = taskService.claim(createdTask.getId());

    assertThat(claimedTask.getOwner()).isNotNull();
    assertThat(CurrentUserContext.getUserid()).isEqualTo(claimedTask.getOwner());
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(isBeforeOrEqual(before, claimedTask.getClaimed())).isTrue();
    assertThat(isBeforeOrEqual(claimedTask.getCreated(), claimedTask.getClaimed())).isTrue();
    assertThat(isBeforeOrEqual(claimedTask.getClaimed(), Instant.now())).isTrue();
    assertThat(claimedTask.getModified()).isEqualTo(claimedTask.getClaimed());
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testForceClaimTaskFromOtherUser()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
                 TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
                 InvalidStateException, InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner("other_user");
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat("other_user").isEqualTo(createdTask.getOwner());

    Instant beforeForceClaim = Instant.now();
    Task taskAfterClaim = taskService.forceClaim(createdTask.getId());

    assertThat(taskAfterClaim.getOwner()).isEqualTo(CurrentUserContext.getUserid());
    assertThat(isBeforeOrEqual(beforeForceClaim, taskAfterClaim.getModified())).isTrue();
    assertThat(isBeforeOrEqual(beforeForceClaim, taskAfterClaim.getClaimed())).isTrue();
    assertThat(isBeforeOrEqual(taskAfterClaim.getCreated(), taskAfterClaim.getModified())).isTrue();

    assertThat(taskAfterClaim.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(taskAfterClaim.isRead()).isTrue();
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testClaimTaskNotExisting() {

    TaskService taskService = taskanaEngine.getTaskService();
    ThrowingCallable call =
        () -> {
          taskService.claim("NOT_EXISTING");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testClaimTaskWithInvalidState() {

    TaskService taskService = taskanaEngine.getTaskService();
    ThrowingCallable call =
        () -> {
          taskService.forceClaim("TKI:000000000000000000000000000000000036");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testClaimTaskWithInvalidOwner() {

    TaskService taskService = taskanaEngine.getTaskService();
    ThrowingCallable call =
        () -> {
          taskService.claim("TKI:000000000000000000000000000000000100");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testCancelClaimForcedWithInvalidState() {

    TaskService taskService = taskanaEngine.getTaskService();
    ThrowingCallable call =
        () -> {
          taskService.forceCancelClaim("TKI:000000000000000000000000000000000036");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testCancelClaimDefaultFlag()
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
                 TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
                 InvalidStateException, InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(TaskState.READY).isEqualTo(createdTask.getState());

    createdTask = taskService.cancelClaim(createdTask.getId());

    assertThat(createdTask).isNotNull();
    assertThat(TaskState.READY).isEqualTo(createdTask.getState());
  }

  @WithAccessId(user = "admin", groups = "admin")
  @Test
  void testForceCancelClaimSuccessfull()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
                 NotAuthorizedException, InterruptedException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task taskBefore = taskService.getTask("TKI:000000000000000000000000000000000043");

    assertThat(taskBefore).isNotNull();
    assertThat(taskBefore.getState()).isEqualTo(TaskState.CLAIMED);

    final Instant before = Instant.now();
    Thread.sleep(1);
    Task taskAfter = taskService.forceCancelClaim("TKI:000000000000000000000000000000000043");

    assertThat(taskAfter).isNotNull();
    assertThat(taskAfter.getState()).isEqualTo(TaskState.READY);
    assertThat(taskAfter.getClaimed()).isNull();
    assertThat(taskAfter.getModified().isAfter(before)).isTrue();
    assertThat(taskAfter.getOwner()).isNull();
    assertThat(taskAfter.isRead()).isTrue();
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void testCancelClaimWithInvalidOwner() {

    TaskService taskService = taskanaEngine.getTaskService();
    ThrowingCallable call =
        () -> {
          taskService.cancelClaim("TKI:000000000000000000000000000000000100");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  private boolean isBeforeOrEqual(Instant before, Instant after) {
    return before.isBefore(after) || before.equals(after);
  }
}
