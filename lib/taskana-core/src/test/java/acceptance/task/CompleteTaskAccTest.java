package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance tests for all claim and complete scenarios. */
@ExtendWith(JaasExtension.class)
class CompleteTaskAccTest extends AbstractAccTest {

  private static final TaskService TASK_SERVICE = taskanaEngine.getTaskService();

  @WithAccessId(user = "user-1-1")
  @Test
  void testCompleteTask()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    assertThat(TASK_SERVICE.getTask("TKI:000000000000000000000000000000000001").getState())
        .isEqualTo(TaskState.CLAIMED);
    Task completedTask = TASK_SERVICE.completeTask("TKI:000000000000000000000000000000000001");
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

    assertThat(TASK_SERVICE.getTask("TKI:000000000000000000000000000000000000").getState())
        .isEqualTo(TaskState.CLAIMED);
    Task completedTask = TASK_SERVICE.forceCompleteTask("TKI:000000000000000000000000000000000000");
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getModified()).isNotEqualTo(completedTask.getCreated());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testCompleteTaskTwice()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    Task completedTask = TASK_SERVICE.completeTask("TKI:000000000000000000000000000000000002");
    Task completedTask2 = TASK_SERVICE.completeTask("TKI:000000000000000000000000000000000002");
    assertThat(completedTask2).isEqualTo(completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testForceCompleteAlreadyClaimed()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidOwnerException, InvalidStateException {
    Task newTask = TASK_SERVICE.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setOwner("other");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    TaskImpl newTaskImpl = (TaskImpl) newTask;
    newTaskImpl.setState(TaskState.CLAIMED);
    newTaskImpl.setClaimed(Instant.now());

    Task createdTask = TASK_SERVICE.createTask(newTaskImpl);
    Task completedTask = TASK_SERVICE.forceCompleteTask(createdTask.getId());

    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getCreated()).isBeforeOrEqualTo(completedTask.getModified());
    assertThat(completedTask.getCompleted()).isEqualTo(completedTask.getModified());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testForceCompleteNotClaimed()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidOwnerException, InvalidStateException {
    Task newTask = TASK_SERVICE.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setOwner("other");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    TaskImpl newTaskImpl = (TaskImpl) newTask;
    newTaskImpl.setClaimed(Instant.now());

    Task createdTask = TASK_SERVICE.createTask(newTaskImpl);
    Task completedTask = TASK_SERVICE.forceCompleteTask(createdTask.getId());

    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getCompleted()).isNotNull();
    assertThat(completedTask.getCreated()).isBeforeOrEqualTo(completedTask.getModified());
    assertThat(completedTask.getCompleted()).isEqualTo(completedTask.getModified());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_TaskIsNotFound() {
    ThrowingCallable call =
        () -> {
          TASK_SERVICE.completeTask("TKI:0000000000000000000000000000000000xx");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserIsNotAuthorizedOnTask() {
    ThrowingCallable call =
        call =
            () -> {
              TASK_SERVICE.completeTask("TKI:000000000000000000000000000000000004");
            };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskIsInStateReady() {
    ThrowingCallable call =
        call =
            () -> {
              TASK_SERVICE.completeTask("TKI:000000000000000000000000000000000025");
            };
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskCallerIsNotTheOwner() {
    ThrowingCallable call =
        call =
            () -> {
              TASK_SERVICE.completeTask("TKI:000000000000000000000000000000000026");
            };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testClaimTaskWithDefaultFlag()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {
    Task newTask = TASK_SERVICE.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner(null);
    Task createdTask = TASK_SERVICE.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getClaimed()).isNull();

    final Instant before = createdTask.getCreated();
    Task claimedTask = TASK_SERVICE.claim(createdTask.getId());

    assertThat(claimedTask.getOwner()).isNotNull();
    assertThat(CurrentUserContext.getUserid()).isEqualTo(claimedTask.getOwner());
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(before).isBeforeOrEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.getCreated()).isBeforeOrEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.getClaimed()).isBeforeOrEqualTo(Instant.now());
    assertThat(claimedTask.getModified()).isEqualTo(claimedTask.getClaimed());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testForceClaimTaskFromOtherUser()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {
    Task newTask = TASK_SERVICE.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner("other_user");
    Task createdTask = TASK_SERVICE.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat("other_user").isEqualTo(createdTask.getOwner());

    Instant beforeForceClaim = Instant.now();
    Task taskAfterClaim = TASK_SERVICE.forceClaim(createdTask.getId());

    assertThat(taskAfterClaim.getOwner()).isEqualTo(CurrentUserContext.getUserid());
    assertThat(beforeForceClaim).isBeforeOrEqualTo(taskAfterClaim.getModified());
    assertThat(beforeForceClaim).isBeforeOrEqualTo(taskAfterClaim.getClaimed());
    assertThat(taskAfterClaim.getCreated()).isBeforeOrEqualTo(taskAfterClaim.getModified());

    assertThat(taskAfterClaim.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(taskAfterClaim.isRead()).isTrue();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testClaimTaskNotExisting() {
    ThrowingCallable call =
        () -> {
          TASK_SERVICE.claim("NOT_EXISTING");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testClaimTaskWithInvalidState() {
    ThrowingCallable call =
        () -> {
          TASK_SERVICE.forceClaim("TKI:000000000000000000000000000000000036");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testClaimTaskWithInvalidOwner() {
    ThrowingCallable call =
        () -> {
          TASK_SERVICE.claim("TKI:000000000000000000000000000000000035");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testCancelClaimForcedWithInvalidState() {
    ThrowingCallable call =
        () -> {
          TASK_SERVICE.forceCancelClaim("TKI:000000000000000000000000000000000036");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testCancelClaimDefaultFlag()
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {
    Task newTask = TASK_SERVICE.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = TASK_SERVICE.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(TaskState.READY).isEqualTo(createdTask.getState());

    createdTask = TASK_SERVICE.cancelClaim(createdTask.getId());

    assertThat(createdTask).isNotNull();
    assertThat(TaskState.READY).isEqualTo(createdTask.getState());
  }

  @WithAccessId(user = "admin", groups = "admin")
  @Test
  void testForceCancelClaimSuccessfull()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException, InterruptedException {
    Task taskBefore = TASK_SERVICE.getTask("TKI:000000000000000000000000000000000043");

    assertThat(taskBefore).isNotNull();
    assertThat(taskBefore.getState()).isEqualTo(TaskState.CLAIMED);

    final Instant before = Instant.now();
    Thread.sleep(1);
    Task taskAfter = TASK_SERVICE.forceCancelClaim("TKI:000000000000000000000000000000000043");

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
    ThrowingCallable call =
        () -> {
          TASK_SERVICE.cancelClaim("TKI:000000000000000000000000000000000035");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @Test
  void should_ThrowException_When_BulkCompleteWithNullList() {
    assertThatThrownBy(() -> TASK_SERVICE.completeTasks(null))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CompleteAllTasks_When_BulkCompletingTasks() throws Exception {
    String id1 = "TKI:000000000000000000000000000000000102";
    String id2 = "TKI:000000000000000000000000000000000101";
    List<String> taskIdList = Arrays.asList(id1, id2);

    Instant beforeBulkComplete = Instant.now();
    BulkOperationResults<String, TaskanaException> results = TASK_SERVICE.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();

    Task completedTask1 = TASK_SERVICE.getTask(id1);
    assertThat(completedTask1.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask1.getCompleted())
        .isEqualTo(completedTask1.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(completedTask1.getOwner()).isEqualTo("user-1-2");

    Task completedTask2 = TASK_SERVICE.getTask(id2);
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
    List<String> taskIdList = Arrays.asList(invalid, validId);

    Instant beforeBulkComplete = Instant.now();
    BulkOperationResults<String, TaskanaException> results = TASK_SERVICE.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    Task completedTask = TASK_SERVICE.getTask(validId);
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
    List<String> taskIdList = Arrays.asList(invalid1, invalid2, invalid3, notAuthorized);

    BulkOperationResults<String, TaskanaException> results = TASK_SERVICE.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds())
        .containsExactlyInAnyOrder(invalid1, invalid2, invalid3, notAuthorized);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorForTaskWhichIsNotClaimed_When_BulkCompletingTasks() throws Exception {
    String id = "TKI:000000000000000000000000000000000025"; // task is not claimed
    List<String> taskIdList = Collections.singletonList(id);

    BulkOperationResults<String, TaskanaException> results = TASK_SERVICE.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(InvalidStateException.class);
    assertThat(results.getErrorForId(id))
        .hasMessage("Task with Id %s has to be claimed before.", id);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorForTasksInEndState_When_BulkCompletingTasks() throws Exception {
    String id1 = "TKI:300000000000000000000000000000000000"; // task is canceled
    String id2 = "TKI:300000000000000000000000000000000010"; // task is terminated
    List<String> taskIdList = Arrays.asList(id1, id2);

    BulkOperationResults<String, TaskanaException> results = TASK_SERVICE.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id1, id2);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(InvalidStateException.class);
    assertThat(results.getErrorForId(id1))
        .hasMessage("Cannot complete task %s because it is in state CANCELLED.", id1);
    assertThat(results.getErrorForId(id2))
        .hasMessage("Cannot complete task %s because it is in state TERMINATED.", id2);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_DoNothingForCompletedTask_When_BulkCompletingTasks() throws Exception {
    String id = "TKI:000000000000000000000000000000000036"; // task is completed
    List<String> taskIdList = Collections.singletonList(id);

    Task before = TASK_SERVICE.getTask(id);
    BulkOperationResults<String, TaskanaException> results = TASK_SERVICE.completeTasks(taskIdList);
    Task after = TASK_SERVICE.getTask(id);

    assertThat(results.containsErrors()).isFalse();
    assertThat(before).isEqualTo(after);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorForTaskIfOwnerDoesNotMach_When_BulkCompletingTasks() throws Exception {
    String id1 = "TKI:000000000000000000000000000000000035";
    List<String> taskIdList = Collections.singletonList(id1);

    BulkOperationResults<String, TaskanaException> results = TASK_SERVICE.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id1);
    assertThat(results.getErrorForId(id1)).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CompleteAllTasks_When_BulkForceCompletingTasks() throws Exception {
    String id1 = "TKI:000000000000000000000000000000000026";
    String id2 = "TKI:000000000000000000000000000000000027";
    List<String> taskIdList = Arrays.asList(id1, id2);

    Instant beforeBulkComplete = Instant.now();
    BulkOperationResults<String, TaskanaException> results =
        TASK_SERVICE.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();

    Task completedTask1 = TASK_SERVICE.getTask(id1);
    assertThat(completedTask1.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask1.getCompleted())
        .isEqualTo(completedTask1.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(completedTask1.getOwner()).isEqualTo("user-1-2");

    Task completedTask2 = TASK_SERVICE.getTask(id2);
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
    List<String> taskIdList = Arrays.asList(invalid, validId);

    Instant beforeBulkComplete = Instant.now();
    BulkOperationResults<String, TaskanaException> results =
        TASK_SERVICE.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    Task completedTask = TASK_SERVICE.getTask(validId);
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
    List<String> taskIdList = Arrays.asList(invalid1, invalid2, invalid3, notAuthorized);

    BulkOperationResults<String, TaskanaException> results =
        TASK_SERVICE.forceCompleteTasks(taskIdList);

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
    List<String> taskIdList = Arrays.asList(id1, id2);

    BulkOperationResults<String, TaskanaException> results =
        TASK_SERVICE.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(id1, id2);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(InvalidStateException.class);
    assertThat(results.getErrorForId(id1))
        .hasMessage("Cannot complete task %s because it is in state CANCELLED.", id1);
    assertThat(results.getErrorForId(id2))
        .hasMessage("Cannot complete task %s because it is in state TERMINATED.", id2);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_DoNothingForCompletedTask_When_BulkForceCompletingTasks() throws Exception {
    String id = "TKI:000000000000000000000000000000000036"; // task is completed
    List<String> taskIdList = Collections.singletonList(id);

    Task before = TASK_SERVICE.getTask(id);
    BulkOperationResults<String, TaskanaException> results =
        TASK_SERVICE.forceCompleteTasks(taskIdList);
    Task after = TASK_SERVICE.getTask(id);

    assertThat(results.containsErrors()).isFalse();
    assertThat(before).isEqualTo(after);
  }

  @WithAccessId(
      user = "user-1-2",
      groups = {"user-1-1"}) // to read task
  @Test
  void should_CompleteTaskWhenAlreadyClaimedByDifferentUser_When_BulkForceCompletingTasks()
      throws Exception {
    String id = "TKI:000000000000000000000000000000000002";
    List<String> taskIdList = Collections.singletonList(id);

    Task beforeClaim = TASK_SERVICE.getTask(id);
    assertThat(beforeClaim.getOwner()).isNotEqualTo("user-1-2");
    final Instant beforeBulkComplete = Instant.now();
    BulkOperationResults<String, TaskanaException> results =
        TASK_SERVICE.forceCompleteTasks(taskIdList);
    Task afterClaim = TASK_SERVICE.getTask(id);

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
    List<String> taskIdList = Collections.singletonList(id);

    Task task = TASK_SERVICE.getTask(id);
    assertThat(task.getState()).isSameAs(TaskState.READY);
    assertThat(task.getClaimed()).isNull();

    final Instant beforeBulkComplete = Instant.now();

    BulkOperationResults<String, TaskanaException> results =
        TASK_SERVICE.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();
    task = TASK_SERVICE.getTask(id);
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
    List<String> taskIdList = Arrays.asList(id1, id2);

    Task task = TASK_SERVICE.getTask(id2);
    assertThat(task.getState()).isSameAs(TaskState.READY);
    assertThat(task.getClaimed()).isNull();

    final Instant beforeBulkComplete = Instant.now();

    BulkOperationResults<String, TaskanaException> results =
        TASK_SERVICE.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();

    task = TASK_SERVICE.getTask(id1);
    assertThat(task.getState()).isEqualTo(TaskState.COMPLETED);
    // do not update claimed timestamp for already claimed task
    assertThat(task.getClaimed()).isBefore(beforeBulkComplete);
    assertThat(task.getCompleted())
        .isEqualTo(task.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(task.getOwner()).isEqualTo("user-b-2");

    task = TASK_SERVICE.getTask(id2);
    assertThat(task.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(task.getCompleted())
        .isEqualTo(task.getClaimed())
        .isEqualTo(task.getModified())
        .isAfterOrEqualTo(beforeBulkComplete);
    assertThat(task.getOwner()).isEqualTo("user-b-2");
  }
}
