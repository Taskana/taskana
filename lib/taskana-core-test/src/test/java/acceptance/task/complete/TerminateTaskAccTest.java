package acceptance.task.complete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import acceptance.task.complete.CompleteTaskWithSpiAccTest.SetCustomAttributeToEndstate;
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
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.util.Triplet;
import pro.taskana.spi.task.api.TaskEndstatePreprocessor;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class TerminateTaskAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject CurrentUserContext currentUserContext;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;

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
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TerminateTask_When_TaskStateIsReady() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.READY)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    Task readTask = taskService.terminateTask(task.getId());

    assertThat(readTask.getState()).isEqualTo(TaskState.TERMINATED);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TerminateTask_When_TaskStateIsClaimed() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CLAIMED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    Task terminatedTask = taskService.terminateTask(task.getId());
    assertThat(terminatedTask.getState()).isEqualTo(TaskState.TERMINATED);
  }

  @WithAccessId(user = "user-1-2")
  @WithAccessId(user = "user-taskrouter")
  @TestTemplate
  void should_ThrowException_When_UserIsNotInAdministrativeRole() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.READY)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService, "admin");

    ThrowingCallable call = () -> taskService.terminateTask(task.getId());

    NotAuthorizedException e = catchThrowableOfType(call, NotAuthorizedException.class);
    assertThat(e.getCurrentUserId()).isEqualTo(currentUserContext.getUserid());
    assertThat(e.getRoles()).containsExactlyInAnyOrder(TaskanaRole.ADMIN, TaskanaRole.TASK_ADMIN);
  }

  @WithAccessId(user = "taskadmin")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_TerminateTaskInEndState() throws Exception {
    Task taskCancelled =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.CANCELLED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task taskTerminated =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.TERMINATED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    Task taskCompleted =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .state(TaskState.COMPLETED)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    List<Triplet<String, Task, TaskState>> testValues =
        List.of(
            Triplet.of("When Task State is Cancelled", taskCancelled, TaskState.CANCELLED),
            Triplet.of("When Task State is Terminated", taskTerminated, TaskState.TERMINATED),
            Triplet.of("When Task State is Completed", taskCompleted, TaskState.COMPLETED));
    ThrowingConsumer<Triplet<String, Task, TaskState>> test =
        t -> {
          ThrowingCallable call = () -> taskService.terminateTask(t.getMiddle().getId());

          InvalidTaskStateException e = catchThrowableOfType(call, InvalidTaskStateException.class);
          assertThat(e.getRequiredTaskStates())
              .containsExactlyInAnyOrder(
                  TaskState.READY,
                  TaskState.IN_REVIEW,
                  TaskState.CLAIMED,
                  TaskState.READY_FOR_REVIEW);
          assertThat(e.getTaskState()).isEqualTo(t.getRight());
          assertThat(e.getTaskId()).isEqualTo(t.getMiddle().getId());
        };

    return DynamicTest.stream(testValues.iterator(), Triplet::getLeft, test);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = TaskEndstatePreprocessor.class,
      serviceProviders = SetCustomAttributeToEndstate.class)
  class ServiceProviderSetsCustomAttributeToTerminated {

    @TaskanaInject TaskService taskService;

    @WithAccessId(user = "admin")
    @Test
    void should_SetCustomAttribute_When_UserTerminatesTask() throws Exception {
      Task task =
          TaskBuilder.newTask()
              .classificationSummary(defaultClassificationSummary)
              .workbasketSummary(defaultWorkbasketSummary)
              .state(TaskState.READY)
              .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
              .buildAndStore(taskService);
      Task processedTask = taskService.terminateTask(task.getId());
      assertThat(processedTask.getState()).isEqualTo(TaskState.TERMINATED);
      assertThat(processedTask.getCustomAttributeMap())
          .containsEntry(
              "camunda:attribute1",
              "{\"valueInfo\":{\"objectTypeName\":\"java.lang.String\"},"
                  + "\"type\":\"String\",\"value\":\"TERMINATED\"}");
    }
  }
}
