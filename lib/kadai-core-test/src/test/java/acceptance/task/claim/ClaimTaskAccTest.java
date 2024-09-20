package acceptance.task.claim;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.internal.util.Triplet;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.InvalidOwnerException;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.UserBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;

@KadaiIntegrationTest
class ClaimTaskAccTest implements KadaiConfigurationModifier {
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject UserService userService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;
  WorkbasketSummary wbWithoutEditTasks;
  WorkbasketSummary wbWithoutReadTasks;
  WorkbasketSummary wbWithoutRead;

  @Override
  public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
    return builder.addAdditionalUserInfo(true);
  }

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    wbWithoutEditTasks = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    wbWithoutReadTasks = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    wbWithoutRead = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(wbWithoutEditTasks.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(wbWithoutReadTasks.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(wbWithoutRead.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = defaultTestObjectReference().build();

    UserBuilder.newUser()
        .id("user-1-2")
        .firstName("Max")
        .lastName("Mustermann")
        .longName("Long name of user-1-2")
        .buildAndStore(userService);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_TaskIsReady() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task claimedTask = taskService.claim(task.getId());

    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(claimedTask.getModified())
        .isNotEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_TaskIsReadyForReview() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task claimedTask = taskService.claim(task.getId());

    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getState()).isEqualTo(TaskState.IN_REVIEW);
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(claimedTask.getModified())
        .isNotEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskIsAlreadyClaimedByAnotherUser() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .claimed(Instant.now())
            .owner("user-1-1")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.claim(task.getId());

    InvalidOwnerException e = catchThrowableOfType(InvalidOwnerException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getTaskId()).isEqualTo(task.getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskIsAlreadyInReviewByAnotherUser() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .state(TaskState.IN_REVIEW)
            .claimed(Instant.now())
            .owner("user-1-1")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.claim(task.getId());

    InvalidOwnerException e = catchThrowableOfType(InvalidOwnerException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getTaskId()).isEqualTo(task.getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_AlreadyClaimedByCaller() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .claimed(Instant.now())
            .owner("user-1-2")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    assertThatCode(() -> taskService.claim(task.getId())).doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_AlreadyInReviewByCaller() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .state(TaskState.IN_REVIEW)
            .claimed(Instant.now())
            .owner("user-1-2")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    assertThatCode(() -> taskService.claim(task.getId())).doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ForceClaimTask_When_TaskIsAlreadyClaimedByAnotherUser() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .claimed(Instant.now())
            .owner("user-1-1")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task claimedTask = taskService.forceClaim(task.getId());

    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(claimedTask.getModified())
        .isNotEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ForceClaimTask_When_InReviewByAnotherUser() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .state(TaskState.IN_REVIEW)
            .claimed(Instant.now())
            .owner("user-1-1")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task claimedTask = taskService.forceClaim(task.getId());

    assertThat(claimedTask).isNotNull();
    assertThat(claimedTask.getState()).isEqualTo(TaskState.IN_REVIEW);
    assertThat(claimedTask.getClaimed()).isNotNull();
    assertThat(claimedTask.getModified())
        .isNotEqualTo(claimedTask.getCreated())
        .isEqualTo(claimedTask.getClaimed());
    assertThat(claimedTask.isRead()).isTrue();
    assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_ForceClaimingTaskWithMissingPermission()
      throws Exception {
    List<Triplet<String, WorkbasketSummary, WorkbasketPermission>> list =
        List.of(
            Triplet.of("With Missing Read Permission", wbWithoutRead, WorkbasketPermission.READ),
            Triplet.of(
                "With Missing ReadTasks Permission",
                wbWithoutReadTasks,
                WorkbasketPermission.READTASKS),
            Triplet.of(
                "With Missing EditTasks Permission",
                wbWithoutEditTasks,
                WorkbasketPermission.EDITTASKS));
    ThrowingConsumer<Triplet<String, WorkbasketSummary, WorkbasketPermission>> testClaimTask =
        t -> {
          String anyUserName = "TestUser28";
          Task task =
              TaskBuilder.newTask()
                  .classificationSummary(defaultClassificationSummary)
                  .workbasketSummary(t.getMiddle())
                  .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
                  .state(TaskState.CLAIMED)
                  .owner(anyUserName)
                  .buildAndStore(taskService, "admin");

          ThrowingCallable call = () -> taskService.forceClaim(task.getId());

          NotAuthorizedOnWorkbasketException e =
              catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

          if (t.getRight() != WorkbasketPermission.EDITTASKS) {
            assertThat(e.getRequiredPermissions())
                .containsExactlyInAnyOrder(
                    WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
          } else {
            assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.EDITTASKS);
          }
          assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
          assertThat(e.getWorkbasketId()).isEqualTo(t.getMiddle().getId());
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, testClaimTask);
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_ThrowNotAuthorizedException_When_UserHasNoReadPermissionAndTaskIsReadyForReview()
      throws Exception {
    Task task =
        TaskBuilder.newTask()
            .state(TaskState.READY_FOR_REVIEW)
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "user-1-2");

    ThrowingCallable call = () -> taskService.claim(task.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo("user-taskrouter");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getRequiredPermissions())
        .containsExactlyInAnyOrder(WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CancelClaimTask_When_TaskIsClaimed() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .claimed(Instant.now())
            .owner("user-1-2")
            .ownerLongName("Long Name")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task unclaimedTask = taskService.cancelClaim(claimedTask.getId());

    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
    assertThat(unclaimedTask.getOwnerLongName()).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_KeepOwnerAndOwnerLongName_When_CancelClaimWithKeepOwner() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .claimed(Instant.now())
            .owner("user-1-2")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task unclaimedTask = taskService.cancelClaim(claimedTask.getId(), true);

    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isEqualTo("user-1-2");
    assertThat(unclaimedTask.getOwnerLongName()).isEqualTo("Long name of user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_KeepOwnerAndOwnerLongName_When_ForceCancelClaimWithKeepOwner() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .claimed(Instant.now())
            .owner("user-1-2")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task unclaimedTask = taskService.forceCancelClaim(claimedTask.getId(), true);

    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isEqualTo("user-1-2");
    assertThat(unclaimedTask.getOwnerLongName()).isEqualTo("Long name of user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CancelClaimTask_When_TaskIsInReview() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .state(TaskState.IN_REVIEW)
            .claimed(Instant.now())
            .owner("user-1-2")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task unclaimedTask = taskService.cancelClaim(claimedTask.getId());

    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_CancelClaimingATaskClaimedByAnotherUser() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .claimed(Instant.now())
            .owner("user-1-1")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.cancelClaim(claimedTask.getId());

    InvalidOwnerException e = catchThrowableOfType(InvalidOwnerException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getTaskId()).isEqualTo(claimedTask.getId());
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_CancelClaimingATaskInReviewByAnotherUser() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .state(TaskState.IN_REVIEW)
            .claimed(Instant.now())
            .owner("user-1-2")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.cancelClaim(claimedTask.getId());

    InvalidOwnerException e = catchThrowableOfType(InvalidOwnerException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo("taskadmin");
    assertThat(e.getTaskId()).isEqualTo(claimedTask.getId());
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_CancelClaimingTaskWithMissingPermission()
      throws Exception {
    List<Triplet<String, WorkbasketSummary, WorkbasketPermission>> list =
        List.of(
            Triplet.of("With Missing Read Permission", wbWithoutRead, WorkbasketPermission.READ),
            Triplet.of(
                "With Missing ReadTasks Permission",
                wbWithoutReadTasks,
                WorkbasketPermission.READTASKS),
            Triplet.of(
                "With Missing EditTasks Permission",
                wbWithoutEditTasks,
                WorkbasketPermission.EDITTASKS));
    ThrowingConsumer<Triplet<String, WorkbasketSummary, WorkbasketPermission>> testCancelClaimTask =
        t -> {
          Task task =
              TaskBuilder.newTask()
                  .classificationSummary(defaultClassificationSummary)
                  .workbasketSummary(t.getMiddle())
                  .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
                  .state(TaskState.CLAIMED)
                  .owner("user-1-2")
                  .buildAndStore(taskService, "admin");

          task.setNote("Test Note");
          ThrowingCallable call = () -> taskService.cancelClaim(task.getId());

          NotAuthorizedOnWorkbasketException e =
              catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

          if (t.getRight() != WorkbasketPermission.EDITTASKS) {
            assertThat(e.getRequiredPermissions())
                .containsExactlyInAnyOrder(
                    WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
          } else {
            assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.EDITTASKS);
          }
          assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
          assertThat(e.getWorkbasketId()).isEqualTo(t.getMiddle().getId());
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, testCancelClaimTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ForceCancelClaim_When_TaskClaimedByAnotherUser() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .state(TaskState.CLAIMED)
            .claimed(Instant.now())
            .owner("user-1-1")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task unclaimedTask = taskService.forceCancelClaim(claimedTask.getId());

    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ForceCancelClaimTask_When_InReviewByAnotherUser() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .state(TaskState.IN_REVIEW)
            .claimed(Instant.now())
            .owner("user-1-1")
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Task unclaimedTask = taskService.forceCancelClaim(claimedTask.getId());

    assertThat(unclaimedTask).isNotNull();
    assertThat(unclaimedTask.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(unclaimedTask.getClaimed()).isNull();
    assertThat(unclaimedTask.isRead()).isTrue();
    assertThat(unclaimedTask.getOwner()).isNull();
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_ForceCancelClaimingTaskWithMissingPermission()
      throws Exception {
    List<Triplet<String, WorkbasketSummary, WorkbasketPermission>> list =
        List.of(
            Triplet.of("With Missing Read Permission", wbWithoutRead, WorkbasketPermission.READ),
            Triplet.of(
                "With Missing ReadTasks Permission",
                wbWithoutReadTasks,
                WorkbasketPermission.READTASKS),
            Triplet.of(
                "With Missing EditTasks Permission",
                wbWithoutEditTasks,
                WorkbasketPermission.EDITTASKS));
    ThrowingConsumer<Triplet<String, WorkbasketSummary, WorkbasketPermission>> testCancelClaimTask =
        t -> {
          Task task =
              TaskBuilder.newTask()
                  .classificationSummary(defaultClassificationSummary)
                  .workbasketSummary(t.getMiddle())
                  .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
                  .state(TaskState.CLAIMED)
                  .owner("user-1-2")
                  .buildAndStore(taskService, "admin");

          task.setNote("Test Note");
          ThrowingCallable call = () -> taskService.forceCancelClaim(task.getId());

          NotAuthorizedOnWorkbasketException e =
              catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

          if (t.getRight() != WorkbasketPermission.EDITTASKS) {
            assertThat(e.getRequiredPermissions())
                .containsExactlyInAnyOrder(
                    WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
          } else {
            assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.EDITTASKS);
          }
          assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
          assertThat(e.getWorkbasketId()).isEqualTo(t.getMiddle().getId());
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, testCancelClaimTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_OwnerOfReadyForReviewTaskIsSet() throws Exception {
    String anyUserName = "TestUser28";
    Task taskReadyForReview =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY_FOR_REVIEW)
            .owner(anyUserName)
            .buildAndStore(taskService);

    Task taskClaimed = taskService.claim(taskReadyForReview.getId());

    assertThat(taskClaimed.getState()).isEqualTo(TaskState.IN_REVIEW);
    assertThat(taskClaimed.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ClaimTask_When_OwnerOfReadyTaskIsSet() throws Exception {
    String anyUserName = "TestUser28";
    Task taskReadyForReview =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .owner(anyUserName)
            .buildAndStore(taskService);

    Task taskClaimed = taskService.claim(taskReadyForReview.getId());

    assertThat(taskClaimed.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(taskClaimed.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_ClaimingTaskWithMissingPermission()
      throws Exception {
    List<Triplet<String, WorkbasketSummary, WorkbasketPermission>> list =
        List.of(
            Triplet.of("With Missing Read Permission", wbWithoutRead, WorkbasketPermission.READ),
            Triplet.of(
                "With Missing ReadTasks Permission",
                wbWithoutReadTasks,
                WorkbasketPermission.READTASKS),
            Triplet.of(
                "With Missing EditTasks Permission",
                wbWithoutEditTasks,
                WorkbasketPermission.EDITTASKS));
    ThrowingConsumer<Triplet<String, WorkbasketSummary, WorkbasketPermission>> testClaimTask =
        t -> {
          String anyUserName = "TestUser28";
          Task task =
              TaskBuilder.newTask()
                  .classificationSummary(defaultClassificationSummary)
                  .workbasketSummary(t.getMiddle())
                  .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
                  .state(TaskState.READY)
                  .owner(anyUserName)
                  .buildAndStore(taskService, "admin");

          ThrowingCallable call = () -> taskService.claim(task.getId());

          NotAuthorizedOnWorkbasketException e =
              catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

          if (t.getRight() != WorkbasketPermission.EDITTASKS) {
            assertThat(e.getRequiredPermissions())
                .containsExactlyInAnyOrder(
                    WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
          } else {
            assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.EDITTASKS);
          }
          assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
          assertThat(e.getWorkbasketId()).isEqualTo(t.getMiddle().getId());
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, testClaimTask);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithAdditionalUserInfoEnabled implements KadaiConfigurationModifier {

    @KadaiInject TaskService taskService;

    @Override
    public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
      return builder.addAdditionalUserInfo(true);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongName() throws Exception {

      Task task =
          TaskBuilder.newTask()
              .classificationSummary(defaultClassificationSummary)
              .workbasketSummary(defaultWorkbasketSummary)
              .primaryObjRef(defaultObjectReference)
              .buildAndStore(taskService);

      Task claimedTask = taskService.claim(task.getId());

      assertThat(claimedTask).isNotNull();
      assertThat(claimedTask.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(claimedTask.getClaimed()).isNotNull();
      assertThat(claimedTask.getModified())
          .isNotEqualTo(claimedTask.getCreated())
          .isEqualTo(claimedTask.getClaimed());
      assertThat(claimedTask.isRead()).isTrue();
      assertThat(claimedTask.getOwner()).isEqualTo("user-1-2");
      assertThat(claimedTask.getOwnerLongName()).isEqualTo("Long name of user-1-2");
    }
  }
}
