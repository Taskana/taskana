package acceptance.task.complete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.util.EnumUtil;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.UserBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class CompleteTaskAccTest implements TaskanaConfigurationModifier {

  @TaskanaInject TaskService taskService;
  @TaskanaInject CurrentUserContext currentUserContext;

  @TaskanaInject UserService userService;

  @TaskanaInject TaskanaEngine taskanaEngine;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @Override
  public TaskanaConfiguration.Builder modify(TaskanaConfiguration.Builder builder) {
    return builder.addAdditionalUserInfo(true);
  }

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = DefaultTestEntities.defaultTestObjectReference().build();

    UserBuilder user11 =
        UserBuilder.newUser()
            .id("user-1-1")
            .longName("Mustermann, Max - (user-1-1)")
            .firstName("Max")
            .lastName("Mustermann");
    user11.buildAndStore(userService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CompleteTask_When_TaskIsClaimed() throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task completedTask = taskService.completeTask(claimedTask.getId());

    assertTaskIsComplete(before, completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CompleteTask_When_TaskIsInReview() throws Exception {
    final Instant before = Instant.now().minus(Duration.ofSeconds(3L));
    Task claimedTask = createTaskInReviewByUser_1_1().buildAndStore(taskService);

    Task completedTask = taskService.completeTask(claimedTask.getId());

    assertTaskIsComplete(before, completedTask);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CompleteClaimedTaskByAnotherUser_When_UserIsAdmin() throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task completedTask = taskService.completeTask(claimedTask.getId());

    assertTaskIsComplete(before, completedTask);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ForceCompleteClaimedTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task completedTask = taskService.forceCompleteTask(claimedTask.getId());

    assertTaskIsComplete(before, completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CompleteTaskTwice() throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService);

    Task completedTask = taskService.completeTask(claimedTask.getId());
    Task completedTask2 = taskService.completeTask(claimedTask.getId());

    assertThat(completedTask2).isEqualTo(completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ForceCompleteTask_When_ClaimedByAnotherUser() throws Exception {
    Task task = createTaskClaimedByUser_1_1().owner("other").buildAndStore(taskService);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task completedTask = taskService.forceCompleteTask(task.getId());

    assertTaskIsComplete(before, completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ForceCompleteTask_When_InReviewByAnotherUser() throws Exception {
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Task claimedTask = createTaskInReviewByUser_1_1().owner("other").buildAndStore(taskService);

    Task completedTask = taskService.forceCompleteTask(claimedTask.getId());

    assertTaskIsComplete(before, completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ForceCompleteTask_When_TaskIsNotClaimed() throws Exception {
    Task task = createDefaultTask().owner("other").buildAndStore(taskService);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task completedTask = taskService.forceCompleteTask(task.getId());

    assertTaskIsComplete(before, completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ForceCompleteTask_When_TaskIsReadyForReview() throws Exception {
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Task readyForReviewTask =
        createDefaultTask().state(TaskState.READY_FOR_REVIEW).buildAndStore(taskService);

    Task completedTask = taskService.forceCompleteTask(readyForReviewTask.getId());

    assertTaskIsComplete(before, completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CompletingNonExistingTask() {
    ThrowingCallable call = () -> taskService.completeTask("NOT_EXISTING");

    assertThatThrownBy(call)
        .isInstanceOf(TaskNotFoundException.class)
        .extracting(TaskNotFoundException.class::cast)
        .extracting(TaskNotFoundException::getTaskId)
        .isEqualTo("NOT_EXISTING");
  }

  @WithAccessId(user = "user-1-2")
  @WithAccessId(user = "user-taskrouter")
  @TestTemplate
  void should_ThrowException_When_UserIsNotAuthorizedOnTask() throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService, "admin");

    ThrowingCallable call = () -> taskService.completeTask(claimedTask.getId());

    MismatchedWorkbasketPermissionException e =
        catchThrowableOfType(call, MismatchedWorkbasketPermissionException.class);
    assertThat(e.getCurrentUserId()).isEqualTo(currentUserContext.getUserid());
    WorkbasketSummary workbasket = claimedTask.getWorkbasketSummary();
    assertThat(e.getWorkbasketId()).isEqualTo(workbasket.getId());
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_TaskIsInStateReady() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.completeTask(task.getId());

    InvalidTaskStateException e = catchThrowableOfType(call, InvalidTaskStateException.class);
    assertThat(e.getTaskId()).isEqualTo(task.getId());
    assertThat(e.getTaskState()).isEqualTo(task.getState());
    assertThat(e.getRequiredTaskStates())
        .containsExactlyInAnyOrder(TaskState.CLAIMED, TaskState.IN_REVIEW);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_TaskCallerIsNotTheOwner() throws Exception {
    Task task = createTaskClaimedByUser_1_1().owner("other").buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.completeTask(task.getId());

    InvalidOwnerException e = catchThrowableOfType(call, InvalidOwnerException.class);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getTaskId()).isEqualTo(task.getId());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CompletingTaskInReviewByAnotherUser() throws Exception {
    Task inReviewTask = createTaskInReviewByUser_1_1().owner("other").buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.completeTask(inReviewTask.getId());

    assertThatThrownBy(call).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ClaimTask_When_UsingDefaultFlag() throws Exception {
    Task task = createDefaultTask().owner(null).buildAndStore(taskService);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task claimedTask = taskService.claim(task.getId());

    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-1");
    assertThat(claimedTask.getOwnerLongName()).isEqualTo("Mustermann, Max - (user-1-1)");
    assertThat(claimedTask.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getClaimed())
        .isNotNull()
        .isAfterOrEqualTo(before)
        .isBeforeOrEqualTo(Instant.now())
        .isAfterOrEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getModified());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ForceClaimTask_When_TaskCallerIsNotTheOwner() throws Exception {
    Task task = createDefaultTask().owner("other_user").buildAndStore(taskService);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task claimedTask = taskService.forceClaim(task.getId());

    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-1");
    assertThat(claimedTask.getOwnerLongName()).isEqualTo("Mustermann, Max - (user-1-1)");
    assertThat(claimedTask.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getClaimed())
        .isNotNull()
        .isAfterOrEqualTo(before)
        .isBeforeOrEqualTo(Instant.now())
        .isAfterOrEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getModified());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ClaimingNonExistingTask() {
    ThrowingCallable call = () -> taskService.claim("NOT_EXISTING");

    assertThatThrownBy(call)
        .isInstanceOf(TaskNotFoundException.class)
        .extracting(TaskNotFoundException.class::cast)
        .extracting(TaskNotFoundException::getTaskId)
        .isEqualTo("NOT_EXISTING");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ClaimingTaskInInvalidState() throws Exception {
    Task task =
        createDefaultTask()
            .state(TaskState.COMPLETED)
            .completed(Instant.now())
            .buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.forceClaim(task.getId());

    InvalidTaskStateException e = catchThrowableOfType(call, InvalidTaskStateException.class);
    assertThat(e.getTaskId()).isEqualTo(task.getId());
    assertThat(e.getTaskState()).isEqualTo(task.getState());
    assertThat(e.getRequiredTaskStates())
        .containsExactlyInAnyOrder(EnumUtil.allValuesExceptFor(TaskState.END_STATES));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ClaimingTaskWithInvalidOwner() throws Exception {
    Task task = createTaskClaimedByUser_1_1().owner("user-1-2").buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.claim(task.getId());

    InvalidOwnerException e = catchThrowableOfType(call, InvalidOwnerException.class);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getTaskId()).isEqualTo(task.getId());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ForceCancelClaimTaskWithInvalidState() throws Exception {
    Task task =
        createDefaultTask()
            .state(TaskState.COMPLETED)
            .completed(Instant.now())
            .buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.forceCancelClaim(task.getId());

    InvalidTaskStateException e = catchThrowableOfType(call, InvalidTaskStateException.class);
    assertThat(e.getTaskId()).isEqualTo(task.getId());
    assertThat(e.getTaskState()).isEqualTo(task.getState());
    assertThat(e.getRequiredTaskStates())
        .containsExactlyInAnyOrder(EnumUtil.allValuesExceptFor(TaskState.END_STATES));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CancelClaim_When_UsingDefaultFlag() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService);

    task = taskService.cancelClaim(task.getId());

    assertThat(task).isNotNull();
    assertThat(task.getState()).isSameAs(TaskState.READY);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ForceCancelClaimTask() throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task taskAfter = taskService.forceCancelClaim(claimedTask.getId());

    assertThat(taskAfter).isNotNull();
    assertThat(taskAfter.getState()).isEqualTo(TaskState.READY);
    assertThat(taskAfter.getClaimed()).isNull();
    assertThat(taskAfter.getModified()).isAfter(before);
    assertThat(taskAfter.getOwner()).isNull();
    assertThat(taskAfter.isRead()).isTrue();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CancelClaimWithInvalidOwner() throws Exception {
    Task task = createTaskClaimedByUser_1_1().owner("user-1-2").buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.cancelClaim(task.getId());

    InvalidOwnerException e = catchThrowableOfType(call, InvalidOwnerException.class);
    assertThat(e.getTaskId()).isEqualTo(task.getId());
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
  }

  @Test
  void should_ThrowException_When_BulkCompleteWithNullList() {
    ThrowingCallable call = () -> taskService.completeTasks(null);

    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CompleteAllTasks_When_BulkCompletingTasks() throws Exception {
    Task claimedTask1 = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    Task claimedTask2 = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    List<String> taskIdList = List.of(claimedTask1.getId(), claimedTask2.getId());
    Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();
    Task completedTask1 = taskService.getTask(claimedTask1.getId());
    assertTaskIsComplete(beforeBulkComplete, completedTask1);
    Task completedTask2 = taskService.getTask(claimedTask2.getId());
    assertTaskIsComplete(beforeBulkComplete, completedTask2);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CompleteValidTasksEvenIfErrorsExist_When_BulkCompletingTasks() throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    List<String> taskIdList = List.of("invalid-id", claimedTask.getId());
    final Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(1);
    assertThat(results.getErrorForId("invalid-id")).isOfAnyClassIn(TaskNotFoundException.class);
    Task completedTask = taskService.getTask(claimedTask.getId());
    assertTaskIsComplete(beforeBulkComplete, completedTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorsForInvalidTaskIds_When_BulkCompletingTasks() throws Exception {
    String invalid1 = "";
    String invalid2 = null;
    String invalid3 = "invalid-id";
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService, "admin");
    String notAuthorized = claimedTask.getId();
    // we can't use List.of because of the null value we insert
    List<String> taskIdList = Arrays.asList(invalid1, invalid2, invalid3, notAuthorized);

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds())
        .containsExactlyInAnyOrder(invalid1, invalid2, invalid3, notAuthorized);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_AddErrorForTaskWhichIsNotClaimed_When_BulkCompletingTasks() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService);
    List<String> taskIdList = List.of(task.getId());

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(task.getId());
    assertThat(results.getErrorMap().values())
        .hasOnlyElementsOfType(InvalidTaskStateException.class);
    assertThat(results.getErrorForId(task.getId()))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '[%s, %s]'",
            task.getId(), TaskState.READY, TaskState.CLAIMED, TaskState.IN_REVIEW);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_AddErrorForTasksInEndState_When_BulkCompletingTasks() throws Exception {
    Task task1 = createDefaultTask().state(TaskState.CANCELLED).buildAndStore(taskService);
    Task task2 = createDefaultTask().state(TaskState.TERMINATED).buildAndStore(taskService);
    List<String> taskIdList = List.of(task1.getId(), task2.getId());
    TaskState[] requiredStates =
        EnumUtil.allValuesExceptFor(TaskState.TERMINATED, TaskState.CANCELLED);

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(task1.getId(), task2.getId());
    assertThat(results.getErrorMap().values())
        .hasOnlyElementsOfType(InvalidTaskStateException.class);
    assertThat(results.getErrorForId(task1.getId()))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            task1.getId(), TaskState.CANCELLED, Arrays.toString(requiredStates));
    assertThat(results.getErrorForId(task2.getId()))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            task2.getId(), TaskState.TERMINATED, Arrays.toString(requiredStates));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_DoNothingForCompletedTask_When_BulkCompletingTasks() throws Exception {
    Task task =
        createDefaultTask()
            .state(TaskState.COMPLETED)
            .completed(Instant.now())
            .buildAndStore(taskService);
    List<String> taskIdList = List.of(task.getId());

    Task before = taskService.getTask(task.getId());
    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);
    Task after = taskService.getTask(task.getId());

    assertThat(results.containsErrors()).isFalse();
    assertThat(before).isEqualTo(after);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_AddErrorForTaskIfOwnerDoesNotMach_When_BulkCompletingTasks() throws Exception {
    Task task = createTaskClaimedByUser_1_1().owner("user-1-2").buildAndStore(taskService);
    List<String> taskIdList = List.of(task.getId());

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(task.getId());
    assertThat(results.getErrorForId(task.getId())).isInstanceOf(InvalidOwnerException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CompleteAllTasks_When_BulkForceCompletingTasks() throws Exception {
    Task claimedTask1 = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    Task claimedTask2 = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    List<String> taskIdList = List.of(claimedTask1.getId(), claimedTask2.getId());
    Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();
    Task completedTask1 = taskService.getTask(claimedTask1.getId());
    assertTaskIsComplete(beforeBulkComplete, completedTask1);
    Task completedTask2 = taskService.getTask(claimedTask2.getId());
    assertTaskIsComplete(beforeBulkComplete, completedTask2);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CompleteValidTasksEvenIfErrorsExist_When_BulkForceCompletingTasks() throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    List<String> taskIdList = List.of("invalid-id", claimedTask.getId());
    final Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(1);
    assertThat(results.getErrorForId("invalid-id")).isOfAnyClassIn(TaskNotFoundException.class);
    Task completedTask = taskService.getTask(claimedTask.getId());
    assertTaskIsComplete(beforeBulkComplete, completedTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_AddErrorsForInvalidTaskIds_When_BulkForceCompletingTasks() throws Exception {
    String invalid1 = "";
    String invalid2 = null;
    String invalid3 = "invalid-id";
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService, "admin");
    String notAuthorized = claimedTask.getId();
    // we can't use List.of because of the null value we insert
    List<String> taskIdList = Arrays.asList(invalid1, invalid2, invalid3, notAuthorized);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds())
        .containsExactlyInAnyOrder(invalid1, invalid2, invalid3, notAuthorized);
    assertThat(results.getErrorMap().values()).hasOnlyElementsOfType(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_AddErrorForTasksInEndState_When_BulkForceCompletingTasks() throws Exception {
    Task task1 = createDefaultTask().state(TaskState.CANCELLED).buildAndStore(taskService);
    Task task2 = createDefaultTask().state(TaskState.TERMINATED).buildAndStore(taskService);
    List<String> taskIdList = List.of(task1.getId(), task2.getId());
    TaskState[] requiredStates =
        EnumUtil.allValuesExceptFor(TaskState.TERMINATED, TaskState.CANCELLED);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getFailedIds()).containsExactlyInAnyOrder(task1.getId(), task2.getId());
    assertThat(results.getErrorMap().values())
        .hasOnlyElementsOfType(InvalidTaskStateException.class);
    assertThat(results.getErrorForId(task1.getId()))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            task1.getId(), TaskState.CANCELLED, Arrays.toString(requiredStates));
    assertThat(results.getErrorForId(task2.getId()))
        .hasMessage(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            task2.getId(), TaskState.TERMINATED, Arrays.toString(requiredStates));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_DoNothingForCompletedTask_When_BulkForceCompletingTasks() throws Exception {
    Task task =
        createDefaultTask()
            .state(TaskState.COMPLETED)
            .completed(Instant.now())
            .buildAndStore(taskService);
    List<String> taskIdList = List.of(task.getId());

    Task before = taskService.getTask(task.getId());
    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);
    Task after = taskService.getTask(task.getId());

    assertThat(results.containsErrors()).isFalse();
    assertThat(before).isEqualTo(after);
  }

  @WithAccessId(user = "user-1-2", groups = "user-1-1") // to read task
  @Test
  void should_CompleteTaskWhenAlreadyClaimedByDifferentUser_When_BulkForceCompletingTasks()
      throws Exception {
    Task claimedTask = createTaskClaimedByUser_1_1().buildAndStore(taskService);
    List<String> taskIdList = List.of(claimedTask.getId());
    final Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    Task beforeComplete = taskService.getTask(claimedTask.getId());
    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);
    Task afterComplete = taskService.getTask(claimedTask.getId());

    assertThat(results.containsErrors()).isFalse();
    assertTaskIsComplete(beforeBulkComplete, afterComplete);
    assertThat(afterComplete.getClaimed()).isEqualTo(beforeComplete.getClaimed());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ClaimTaskWhenNotClaimed_When_BulkForceCompletingTasks() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService);
    List<String> taskIdList = List.of(task.getId());
    Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();
    Task completedTask = taskService.getTask(task.getId());
    assertTaskIsComplete(beforeBulkComplete, completedTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_OnlyClaimTasksWhichAreNotClaimed_When_BulkForceCompletingTasks() throws Exception {
    Task task1 = createTaskClaimedByUser_1_1().owner("other").buildAndStore(taskService);
    Task task2 = createDefaultTask().buildAndStore(taskService);
    List<String> taskIdList = List.of(task1.getId(), task2.getId());
    final Instant beforeBulkComplete = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    BulkOperationResults<String, TaskanaException> results =
        taskService.forceCompleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();

    Task completedTask1 = taskService.getTask(task1.getId());

    assertTaskIsComplete(beforeBulkComplete, completedTask1);
    // do not update claimed timestamp for already claimed task
    assertThat(completedTask1.getClaimed()).isBefore(beforeBulkComplete);

    Task completedTask2 = taskService.getTask(task2.getId());
    assertTaskIsComplete(beforeBulkComplete, completedTask2);
  }

  private void assertTaskIsComplete(Instant before, Task completedTask) {
    assertThat(completedTask).isNotNull();
    assertThat(completedTask.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(completedTask.getOwner()).isEqualTo(currentUserContext.getUserid());
    assertThat(completedTask.getCompleted())
        .isNotNull()
        .isEqualTo(completedTask.getModified())
        .isNotEqualTo(completedTask.getCreated())
        .isAfterOrEqualTo(before);
  }

  private TaskBuilder createTaskClaimedByUser_1_1() {
    return createDefaultTask()
        .owner("user-1-1")
        .created(Instant.parse("2018-01-29T15:55:00Z"))
        .state(TaskState.CLAIMED)
        .claimed(Instant.now());
  }

  private TaskBuilder createTaskInReviewByUser_1_1() {
    return createDefaultTask()
        .owner("user-1-1")
        .created(Instant.parse("2018-01-29T15:55:00Z"))
        .state(TaskState.IN_REVIEW)
        .claimed(Instant.now());
  }

  private TaskBuilder createDefaultTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference);
  }
}
