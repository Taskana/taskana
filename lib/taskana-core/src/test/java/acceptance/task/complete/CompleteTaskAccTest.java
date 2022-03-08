package acceptance.task.complete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.EnumUtil;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;

/** Acceptance tests for all claim and complete scenarios. */
@ExtendWith(JaasExtension.class)
class CompleteTaskAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @Test
  void testCompleteTask() throws Exception {
    assertThat(taskService.getTask("TKI:000000000000000000000000000000000001").getState())
        .isEqualTo(TaskState.CLAIMED);
    Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000001");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getModified()).isNotEqualTo(completedTask.getCreated());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_completeClaimedTaskByAnotherUser_When_UserIsAdmin() throws Exception {
    assertThat(taskService.getTask("TKI:000000000000000000000000000000000029").getState())
        .isEqualTo(TaskState.CLAIMED);
    Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000029");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getModified()).isNotEqualTo(completedTask.getCreated());
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ForceCompleteTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    resetDb(false);

    assertThat(taskService.getTask("TKI:000000000000000000000000000000000000").getState())
        .isEqualTo(TaskState.CLAIMED);
    Task completedTask = taskService.forceCompleteTask("TKI:000000000000000000000000000000000000");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getModified()).isNotEqualTo(completedTask.getCreated());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testCompleteTaskTwice() throws Exception {
    Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000002");
    Task completedTask2 = taskService.completeTask("TKI:000000000000000000000000000000000002");
    assertThat(completedTask2).isEqualTo(completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testForceCompleteAlreadyClaimed() throws Exception {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
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
    assertThat(completedTask.getCreated()).isBeforeOrEqualTo(completedTask.getModified());
    assertThat(completedTask.getCompleted()).isEqualTo(completedTask.getModified());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testForceCompleteNotClaimed() throws Exception {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
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
    assertThat(completedTask.getCreated()).isBeforeOrEqualTo(completedTask.getModified());
    assertThat(completedTask.getCompleted()).isEqualTo(completedTask.getModified());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_TaskIsNotFound() {
    ThrowingCallable call =
        () -> taskService.completeTask("TKI:0000000000000000000000000000000000xx");
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserIsNotAuthorizedOnTask() {
    ThrowingCallable call =
        () -> taskService.completeTask("TKI:000000000000000000000000000000000004");
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskIsInStateReady() {
    ThrowingCallable call =
        () -> taskService.completeTask("TKI:000000000000000000000000000000000025");
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskCallerIsNotTheOwner() {
    ThrowingCallable call =
        () -> taskService.completeTask("TKI:000000000000000000000000000000000026");
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testClaimTaskWithDefaultFlag() throws Exception {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
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
    assertThat(taskanaEngine.getCurrentUserContext().getUserid()).isEqualTo(claimedTask.getOwner());
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(before).isBeforeOrEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.getCreated()).isBeforeOrEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.getClaimed()).isBeforeOrEqualTo(Instant.now());
    assertThat(claimedTask.getModified()).isEqualTo(claimedTask.getClaimed());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testForceClaimTaskFromOtherUser() throws Exception {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner("other_user");
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getOwner()).isEqualTo("other_user");

    Instant beforeForceClaim = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Task taskAfterClaim = taskService.forceClaim(createdTask.getId());

    assertThat(taskAfterClaim.getOwner())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(beforeForceClaim)
        .isBeforeOrEqualTo(taskAfterClaim.getModified())
        .isBeforeOrEqualTo(taskAfterClaim.getClaimed());
    assertThat(taskAfterClaim.getCreated()).isBeforeOrEqualTo(taskAfterClaim.getModified());

    assertThat(taskAfterClaim.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(taskAfterClaim.isRead()).isTrue();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testClaimTaskNotExisting() {
    assertThatThrownBy(() -> taskService.claim("NOT_EXISTING"))
        .isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testClaimTaskWithInvalidState() {
    assertThatThrownBy(() -> taskService.forceClaim("TKI:000000000000000000000000000000000036"))
        .isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testClaimTaskWithInvalidOwner() {
    assertThatThrownBy(() -> taskService.claim("TKI:000000000000000000000000000000000035"))
        .isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testCancelClaimForcedWithInvalidState() {
    ThrowingCallable call =
        () -> taskService.forceCancelClaim("TKI:000000000000000000000000000000000036");
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testCancelClaimDefaultFlag() throws Exception {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getState()).isSameAs(TaskState.READY);

    createdTask = taskService.cancelClaim(createdTask.getId());

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getState()).isSameAs(TaskState.READY);
  }

  @WithAccessId(user = "admin")
  @Test
  void testForceCancelClaimSuccessfull() throws Exception {
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

  @WithAccessId(user = "user-1-2")
  @Test
  void testCancelClaimWithInvalidOwner() {
    assertThatThrownBy(() -> taskService.cancelClaim("TKI:000000000000000000000000000000000035"))
        .isInstanceOf(InvalidOwnerException.class);
  }

  @Test
  void should_ThrowException_When_BulkCompleteWithNullList() {
    assertThatThrownBy(() -> taskService.completeTasks(null))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CompleteAllTasks_When_BulkCompletingTasks() throws Exception {
    String id1 = "TKI:000000000000000000000000000000000102";
    String id2 = "TKI:000000000000000000000000000000000101";
    List<String> taskIdList = List.of(id1, id2);

    Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();

    Task completedTask1 = taskService.getTask(id1);
    assertThat(completedTask1.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask1.getCompleted())
        .isEqualTo(completedTask1.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(completedTask1.getOwner()).isEqualTo("user-1-2");

    Task completedTask2 = taskService.getTask(id2);
    assertThat(completedTask2.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask2.getCompleted())
        .isEqualTo(completedTask2.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(completedTask2.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CompleteValidTasksEvenIfErrorsExist_When_BulkCompletingTasks() throws Exception {
    String invalid = "invalid-id";
    String validId = "TKI:000000000000000000000000000000000103";
    List<String> taskIdList = List.of(invalid, validId);

    Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    Task completedTask = taskService.getTask(validId);
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted())
        .isEqualTo(completedTask.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(completedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorsForInvalidTaskIds_When_BulkCompletingTasks() throws Exception {
    String invalid1 = "";
    String invalid2 = null;
    String invalid3 = "invalid-id";
    String notAuthorized = "TKI:000000000000000000000000000000000002";
    // we can't use List.of because of the null value we insert
    List<String> taskIdList = Arrays.asList(invalid1, invalid2, invalid3, notAuthorized);

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds())
        .containsExactlyInAnyOrder(invalid1, invalid2, invalid3, notAuthorized);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorForTaskWhichIsNotClaimed_When_BulkCompletingTasks() throws Exception {
    String id = "TKI:000000000000000000000000000000000025"; // task is not claimed
    List<String> taskIdList = List.of(id);

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(InvalidStateException.class);
    assertThat(results.getErrorForId(id))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '[%s]'",
            id, TaskState.READY, TaskState.CLAIMED);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorForTasksInEndState_When_BulkCompletingTasks() throws Exception {
    String id1 = "TKI:300000000000000000000000000000000000"; // task is canceled
    String id2 = "TKI:300000000000000000000000000000000010"; // task is terminated
    List<String> taskIdList = List.of(id1, id2);
    TaskState[] requiredStates =
        EnumUtil.allValuesExceptFor(TaskState.TERMINATED, TaskState.CANCELLED);

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id1, id2);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(InvalidStateException.class);
    assertThat(results.getErrorForId(id1))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            id1, TaskState.CANCELLED, Arrays.toString(requiredStates));
    assertThat(results.getErrorForId(id2))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            id2, TaskState.TERMINATED, Arrays.toString(requiredStates));
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_DoNothingForCompletedTask_When_BulkCompletingTasks() throws Exception {
    String id = "TKI:000000000000000000000000000000000036"; // task is completed
    List<String> taskIdList = List.of(id);

    Task before = taskService.getTask(id);
    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);
    Task after = taskService.getTask(id);

    assertThat(results.containsErrors()).isFalse();
    assertThat(before).isEqualTo(after);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorForTaskIfOwnerDoesNotMach_When_BulkCompletingTasks() throws Exception {
    String id1 = "TKI:000000000000000000000000000000000035";
    List<String> taskIdList = List.of(id1);

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id1);
    assertThat(results.getErrorForId(id1)).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CompleteAllTasks_When_BulkForceCompletingTasks() throws Exception {
    String id1 = "TKI:000000000000000000000000000000000026";
    String id2 = "TKI:000000000000000000000000000000000027";
    List<String> taskIdList = List.of(id1, id2);

    Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();

    Task completedTask1 = taskService.getTask(id1);
    assertThat(completedTask1.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask1.getCompleted())
        .isEqualTo(completedTask1.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(completedTask1.getOwner()).isEqualTo("user-1-2");

    Task completedTask2 = taskService.getTask(id2);
    assertThat(completedTask2.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask2.getCompleted())
        .isEqualTo(completedTask2.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(completedTask2.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CompleteValidTasksEvenIfErrorsExist_When_BulkForceCompletingTasks() throws Exception {
    String invalid = "invalid-id";
    String validId = "TKI:000000000000000000000000000000000028";
    List<String> taskIdList = List.of(invalid, validId);

    Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    Task completedTask = taskService.getTask(validId);
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted())
        .isEqualTo(completedTask.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(completedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorsForInvalidTaskIds_When_BulkForceCompletingTasks() throws Exception {
    String invalid1 = "";
    String invalid2 = null;
    String invalid3 = "invalid-id";
    String notAuthorized = "TKI:000000000000000000000000000000000002";
    // we can't use List.of because of the null value we insert
    List<String> taskIdList = Arrays.asList(invalid1, invalid2, invalid3, notAuthorized);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds())
        .containsExactlyInAnyOrder(invalid1, invalid2, invalid3, notAuthorized);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorForTasksInEndState_When_BulkForceCompletingTasks() throws Exception {
    String id1 = "TKI:300000000000000000000000000000000000"; // task is canceled
    String id2 = "TKI:300000000000000000000000000000000010"; // task is terminated
    List<String> taskIdList = List.of(id1, id2);
    TaskState[] requiredStates =
        EnumUtil.allValuesExceptFor(TaskState.TERMINATED, TaskState.CANCELLED);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id1, id2);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(InvalidStateException.class);
    assertThat(results.getErrorForId(id1))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            id1, TaskState.CANCELLED, Arrays.toString(requiredStates));
    assertThat(results.getErrorForId(id2))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            id2, TaskState.TERMINATED, Arrays.toString(requiredStates));
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_DoNothingForCompletedTask_When_BulkForceCompletingTasks() throws Exception {
    String id = "TKI:000000000000000000000000000000000036"; // task is completed
    List<String> taskIdList = List.of(id);

    Task before = taskService.getTask(id);
    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);
    Task after = taskService.getTask(id);

    assertThat(results.containsErrors()).isFalse();
    assertThat(before).isEqualTo(after);
  }

  @WithAccessId(user = "user-1-2", groups = "user-1-1") // to read task
  @Test
  void should_CompleteTaskWhenAlreadyClaimedByDifferentUser_When_BulkForceCompletingTasks()
      throws Exception {
    String id = "TKI:000000000000000000000000000000000002";
    List<String> taskIdList = List.of(id);

    Task beforeClaim = taskService.getTask(id);
    assertThat(beforeClaim.getOwner()).isNotEqualTo("user-1-2");
    final Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);
    Task afterClaim = taskService.getTask(id);

    assertThat(results.containsErrors()).isFalse();
    assertThat(afterClaim.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(afterClaim.getClaimed()).isEqualTo(beforeClaim.getClaimed());
    assertThat(afterClaim.getCompleted())
        .isEqualTo(afterClaim.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(afterClaim.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTaskWhenNotClaimed_When_BulkForceCompletingTasks() throws Exception {
    String id = "TKI:000000000000000000000000000000000033";
    List<String> taskIdList = List.of(id);

    Task task = taskService.getTask(id);
    assertThat(task.getState()).isSameAs(TaskState.READY);
    assertThat(task.getClaimed()).isNull();

    final Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();
    task = taskService.getTask(id);
    assertThat(task.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(task.getCompleted())
        .isEqualTo(task.getClaimed())
        .isEqualTo(task.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(task.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-b-2")
  @Test
  void should_OnlyClaimTasksWhichAreNotClaimed_When_BulkForceCompletingTasks() throws Exception {
    String id1 = "TKI:000000000000000000000000000000000043"; // task is already claimed
    String id2 = "TKI:000000000000000000000000000000000044"; // task is ready
    List<String> taskIdList = List.of(id1, id2);

    Task task = taskService.getTask(id2);
    assertThat(task.getState()).isSameAs(TaskState.READY);
    assertThat(task.getClaimed()).isNull();

    final Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();

    task = taskService.getTask(id1);
    assertThat(task.getState()).isEqualTo(TaskState.COMPLETED);
    // do not update claimed timestamp for already claimed task
    assertThat(task.getClaimed()).isBefore(beforeBulkComplete);
    assertThat(task.getCompleted())
        .isEqualTo(task.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(task.getOwner()).isEqualTo("user-b-2");

    task = taskService.getTask(id2);
    assertThat(task.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(task.getCompleted())
        .isEqualTo(task.getClaimed())
        .isEqualTo(task.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(task.getOwner()).isEqualTo("user-b-2");
  }
}
