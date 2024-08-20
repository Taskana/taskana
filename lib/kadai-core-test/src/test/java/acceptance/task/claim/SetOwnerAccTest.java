package acceptance.task.claim;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.BulkOperationResults;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.InvalidTaskStateException;
import io.kadai.task.api.exceptions.TaskNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.assertj.core.api.Condition;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@KadaiIntegrationTest
class SetOwnerAccTest {
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_SetOwner_When_TaskIsReadyForReview() throws Exception {
    Task taskReadyForReview =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY_FOR_REVIEW)
            .buildAndStore(taskService);
    String anyUserName = "TestUser28";

    Task modifiedTaskReady = setOwner(taskReadyForReview, anyUserName);

    assertThat(modifiedTaskReady.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(modifiedTaskReady.getOwner()).isEqualTo(anyUserName);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_SetOwner_When_TaskIsReady() throws Exception {
    Task taskReady =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService);
    String anyUserName = "TestUser27";

    Task modifiedTaskReady = setOwner(taskReady, anyUserName);

    assertThat(modifiedTaskReady.getState()).isEqualTo(TaskState.READY);
    assertThat(modifiedTaskReady.getOwner()).isEqualTo(anyUserName);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_SetOwnerViaUpdateTaskIsNotAuthorizedOnWorkbasket()
      throws Exception {
    Task taskReadyForReview =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY_FOR_REVIEW)
            .buildAndStore(taskService, "admin");
    String anyUserName = "TestUser3";

    ThrowingCallable call2 = () -> setOwner(taskReadyForReview, anyUserName);

    NotAuthorizedOnWorkbasketException e2 =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call2);
    assertThat(e2.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e2.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e2.getRequiredPermissions())
        .containsExactly(WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_SetOwnerOfClaimedTaskFails() throws Exception {
    Task taskClaimed =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.CLAIMED)
            .owner("user-1-1")
            .buildAndStore(taskService, "admin");
    String anyUserName = "TestUser007";

    ThrowingCallable call = () -> setOwner(taskClaimed, anyUserName);

    InvalidTaskStateException e = catchThrowableOfType(InvalidTaskStateException.class, call);
    assertThat(e.getTaskId()).isEqualTo(taskClaimed.getId());
    assertThat(e.getTaskState()).isEqualTo(TaskState.CLAIMED);
    assertThat(e.getRequiredTaskStates())
        .containsExactlyInAnyOrder(TaskState.READY, TaskState.READY_FOR_REVIEW);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_SetOwnerViaSetOwnerOfTasksIsNotAuthorizedOnWorkbasket()
      throws Exception {
    Task taskReady =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");

    String anyUserName = "TestUser3";
    taskService.setOwnerOfTasks(anyUserName, List.of(taskReady.getId()));

    BulkOperationResults<String, KadaiException> results =
        taskService.setOwnerOfTasks(anyUserName, List.of(taskReady.getId()));
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorForId(taskReady.getId()))
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_SetMOwnerOfMultipleTasks_When_TasksAreReadyAndReadyForReview() throws Exception {
    Task taskReadyForReview =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY_FOR_REVIEW)
            .buildAndStore(taskService, "admin");
    Task taskReady =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");
    List<String> taskIds = List.of(taskReadyForReview.getId(), taskReady.getId());

    BulkOperationResults<String, KadaiException> results =
        taskService.setOwnerOfTasks("someUser", taskIds);

    assertThat(results.containsErrors()).isFalse();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_SetOwnerOfTasks_When_TasksAreDuplicated() throws Exception {
    Task taskReadyForReview =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY_FOR_REVIEW)
            .buildAndStore(taskService, "admin");
    Task taskReady =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");
    List<String> taskIds =
        List.of(
            taskReady.getId(),
            taskReadyForReview.getId(),
            taskReady.getId(),
            taskReadyForReview.getId());

    BulkOperationResults<String, KadaiException> results =
        taskService.setOwnerOfTasks("someUser", taskIds);

    assertThat(results.containsErrors()).isFalse();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_SetOwnerOfTasks_When_TasksAreDuplicatedAndTaskNotExisting() throws Exception {
    Task taskReadyForReview =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY_FOR_REVIEW)
            .buildAndStore(taskService, "admin");
    Task taskReady =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");
    List<String> taskIds =
        List.of(
            taskReady.getId(),
            "TKI:000000000000000000000000000047110059",
            taskReadyForReview.getId(),
            taskReady.getId());

    BulkOperationResults<String, KadaiException> results =
        taskService.setOwnerOfTasks("someUser", taskIds);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(1);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000047110059"))
        .isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-b-2")
  @Test
  void should_ReturnExceptions_When_SettingOwnerOfMultipleTasksWhileNotAuthorized()
      throws Exception {
    Task taskReadyForReview =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY_FOR_REVIEW)
            .buildAndStore(taskService, "admin");
    Task taskReady =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");
    List<String> taskIds =
        List.of(taskReady.getId(), taskReadyForReview.getId(), taskReady.getId());

    BulkOperationResults<String, KadaiException> results =
        taskService.setOwnerOfTasks("someUser", taskIds);

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(2);
    assertThat(results.getErrorForId(taskReady.getId()))
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class);
    assertThat(results.getErrorForId(taskReadyForReview.getId()))
        .isInstanceOf(NotAuthorizedOnWorkbasketException.class);
  }

  @WithAccessId(user = "user-b-2")
  @Test
  void should_SetOwnerOfMultipleTask_When_TaskListIsEmpty() {
    List<String> taskIds = new ArrayList<>();

    BulkOperationResults<String, KadaiException> results =
        taskService.setOwnerOfTasks("someUser", taskIds);

    assertThat(results.containsErrors()).isFalse();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_SetOwnerOfMultipleTasksAndThrowVariousExceptions() throws Exception {
    WorkbasketSummary defaultWorkbasketSummary2 =
        defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService, "admin");
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary2.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService, "admin");
    Task task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary2)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");
    Task task2 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary2)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.COMPLETED)
            .buildAndStore(taskService, "admin");
    Task task3 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.COMPLETED)
            .buildAndStore(taskService, "admin");
    Task task4 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");

    BulkOperationResults<String, KadaiException> results =
        taskService.setOwnerOfTasks(
            "theWorkaholic", List.of(task1.getId(), task2.getId(), task3.getId(), task4.getId()));

    assertThat(results.containsErrors()).isTrue();

    Condition<Object> invalidTaskStateException =
        new Condition<>(
            c -> c.getClass() == InvalidTaskStateException.class, "InvalidStateException");
    Condition<Object> mismatchedWorkbasketPermissionException =
        new Condition<>(
            c -> c.getClass() == NotAuthorizedOnWorkbasketException.class,
            "MismatchedWorkbasketPermissionException");

    assertThat(results.getErrorMap())
        .hasSize(3)
        .extractingFromEntries(Entry::getValue)
        .hasOnlyElementsOfTypes(
            InvalidTaskStateException.class, NotAuthorizedOnWorkbasketException.class)
        .areExactly(1, invalidTaskStateException)
        .areExactly(2, mismatchedWorkbasketPermissionException);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetOwnerOfMultipleTasksAndThrowVariousExceptionsAsAdmin() throws Exception {
    WorkbasketSummary defaultWorkbasketSummary2 =
        defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService, "admin");
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary2.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService, "admin");
    Task task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary2)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");
    Task task2 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary2)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.COMPLETED)
            .buildAndStore(taskService, "admin");
    Task task3 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.COMPLETED)
            .buildAndStore(taskService, "admin");
    Task task4 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService, "admin");

    BulkOperationResults<String, KadaiException> results =
        taskService.setOwnerOfTasks(
            "theWorkaholic", List.of(task1.getId(), task2.getId(), task3.getId(), task4.getId()));

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap())
        .hasSize(2)
        .extractingFromEntries(Entry::getValue)
        .hasOnlyElementsOfType(InvalidTaskStateException.class);
  }

  private Task setOwner(Task task, String anyUserName) throws Exception {
    task.setOwner(anyUserName);
    task = taskService.updateTask(task);
    return task;
  }
}
