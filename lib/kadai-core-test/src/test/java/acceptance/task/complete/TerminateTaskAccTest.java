package acceptance.task.complete;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import acceptance.task.complete.CompleteTaskWithSpiAccTest.SetCustomAttributeToEndstate;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.internal.util.Triplet;
import io.kadai.spi.task.api.TaskEndstatePreprocessor;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.InvalidTaskStateException;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
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

@KadaiIntegrationTest
class TerminateTaskAccTest {
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject CurrentUserContext currentUserContext;

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

    NotAuthorizedException e = catchThrowableOfType(NotAuthorizedException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo(currentUserContext.getUserid());
    assertThat(e.getRoles()).containsExactlyInAnyOrder(KadaiRole.ADMIN, KadaiRole.TASK_ADMIN);
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

          InvalidTaskStateException e = catchThrowableOfType(InvalidTaskStateException.class, call);
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

    @KadaiInject TaskService taskService;

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
