package acceptance.task.requestchanges;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.internal.util.EnumUtil;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.InvalidOwnerException;
import io.kadai.task.api.exceptions.InvalidTaskStateException;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

@KadaiIntegrationTest
class RequestChangesAccTest {
  @KadaiInject TaskService taskService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

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
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = DefaultTestEntities.defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_RequestChanges_When_TaskIsClaimed() throws Exception {
    Instant now = Instant.now();
    Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService);

    Task result = taskService.requestChanges(task.getId());

    assertThat(result.getState()).isEqualTo(TaskState.READY);
    assertThat(result.getOwner()).isNull();
    assertThat(result.getModified()).isAfterOrEqualTo(now);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ForceRequestChanges_When_TaskIsInReviewByDifferentUser() throws Exception {
    Instant now = Instant.now();
    Task task = createTaskInReviewByUser("user-1-2").buildAndStore(taskService);

    Task result = taskService.forceRequestChanges(task.getId());

    assertThat(result.getState()).isEqualTo(TaskState.READY);
    assertThat(result.getOwner()).isNull();
    assertThat(result.getModified()).isAfterOrEqualTo(now);
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicTest> should_ForceRequestChanges_When_TaskIsNotInEndState() {
    List<TaskState> testCases = Arrays.asList(EnumUtil.allValuesExceptFor(TaskState.END_STATES));
    ThrowingConsumer<TaskState> test =
        state -> {
          Instant now = Instant.now();
          Task task = createDefaultTask().state(state).buildAndStore(taskService);
          Task result = taskService.forceRequestChanges(task.getId());

          assertThat(result.getState()).isEqualTo(TaskState.READY);
          assertThat(result.getOwner()).isNull();
          assertThat(result.getModified()).isAfterOrEqualTo(now);
        };
    return DynamicTest.stream(testCases.iterator(), TaskState::name, test);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_RequestingReviewAndTaskIsInReviewByDifferentOwner()
      throws Exception {
    Task task = createTaskInReviewByUser("user-1-2").buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.requestChanges(task.getId());

    InvalidOwnerException e = catchThrowableOfType(InvalidOwnerException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getTaskId()).isEqualTo(task.getId());
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_RequestingChangesAndTaskIsNotInReview() {
    List<TaskState> invalidStates = Arrays.asList(EnumUtil.allValuesExceptFor(TaskState.IN_REVIEW));

    ThrowingConsumer<TaskState> test =
        state -> {
          Task task = createDefaultTask().state(state).buildAndStore(taskService);
          ThrowingCallable call = () -> taskService.requestChanges(task.getId());

          InvalidTaskStateException e = catchThrowableOfType(InvalidTaskStateException.class, call);
          assertThat(e.getRequiredTaskStates()).containsExactly(TaskState.IN_REVIEW);
          assertThat(e.getTaskState()).isEqualTo(state);
          assertThat(e.getTaskId()).isEqualTo(task.getId());
        };
    return DynamicTest.stream(invalidStates.iterator(), TaskState::name, test);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_UserHasNoWorkbasketPermission() throws Exception {
    Task task = createTaskInReviewByUser("user-1-1").buildAndStore(taskService, "user-1-1");
    ThrowingCallable call = () -> taskService.requestChanges(task.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);
    assertThat(e.getRequiredPermissions())
        .containsExactly(WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getDomain()).isNull();
    assertThat(e.getWorkbasketKey()).isNull();
  }

  @WithAccessId(user = "user-1-1")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_ForceRequestChangesAndTaskIsInEndState() {
    List<TaskState> endStates = Arrays.asList(TaskState.END_STATES);

    ThrowingConsumer<TaskState> test =
        state -> {
          Task task = createDefaultTask().state(state).buildAndStore(taskService);
          ThrowingCallable call = () -> taskService.forceRequestChanges(task.getId());

          InvalidTaskStateException e = catchThrowableOfType(InvalidTaskStateException.class, call);
          assertThat(e.getRequiredTaskStates())
              .containsExactlyInAnyOrder(EnumUtil.allValuesExceptFor(TaskState.END_STATES));
          assertThat(e.getTaskState()).isEqualTo(state);
          assertThat(e.getTaskId()).isEqualTo(task.getId());
        };
    return DynamicTest.stream(endStates.iterator(), TaskState::name, test);
  }

  private TaskBuilder createTaskInReviewByUser(String owner) {
    return createDefaultTask().owner(owner).state(TaskState.IN_REVIEW);
  }

  private TaskBuilder createDefaultTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference);
  }
}
