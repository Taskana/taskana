package acceptance.task.complete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.internal.util.Triplet;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class CancelTaskAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject UserService userService;

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
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CancelReadyTask() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.READY)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    taskService.cancelTask(task.getId());

    Task readTask = taskService.getTask(task.getId());
    assertThat(readTask.getState()).isEqualTo(TaskState.CANCELLED);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_CancelTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Task taskToCancel =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CLAIMED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    Task cancelledTask = taskService.cancelTask(taskToCancel.getId());

    assertThat(cancelledTask.getState()).isEqualTo(TaskState.CANCELLED);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CancelClaimedTask() throws Exception {
    Task claimedTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CLAIMED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    Task cancelledTask = taskService.cancelTask(claimedTask.getId());

    assertThat(cancelledTask.getState()).isEqualTo(TaskState.CANCELLED);
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_ThrowException_When_UserNotAuthorized() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CLAIMED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");

    ThrowingCallable call = () -> taskService.cancelTask(task.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(call, NotAuthorizedOnWorkbasketException.class);
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
    assertThat(e.getCurrentUserId()).isEqualTo("user-taskrouter");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_CancellingATaskInEndState() throws Exception {
    Task completedTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.COMPLETED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task terminatedTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.TERMINATED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task cancelledTask =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CANCELLED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    List<Triplet<String, Task, TaskState>> list =
        List.of(
            Triplet.of("When Cancelling Completed Task", completedTask, TaskState.COMPLETED),
            Triplet.of("When Cancelling Terminated Task", terminatedTask, TaskState.TERMINATED),
            Triplet.of("When Cancelling Cancelled Task", cancelledTask, TaskState.CANCELLED));
    ThrowingConsumer<Triplet<String, Task, TaskState>> testCancelTask =
        t -> {
          ThrowingCallable call = () -> taskService.cancelTask(t.getMiddle().getId());

          InvalidTaskStateException e = catchThrowableOfType(call, InvalidTaskStateException.class);
          assertThat(e.getRequiredTaskStates())
              .containsExactlyInAnyOrder(
                  TaskState.READY,
                  TaskState.IN_REVIEW,
                  TaskState.READY_FOR_REVIEW,
                  TaskState.CLAIMED);
          assertThat(e.getTaskId()).isEqualTo(t.getMiddle().getId());
          assertThat(e.getTaskState()).isEqualTo(t.getRight());
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, testCancelTask);
  }
}
