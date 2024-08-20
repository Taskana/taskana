package acceptance.task.complete;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.spi.task.api.ReviewRequiredProvider;
import io.kadai.spi.task.api.TaskEndstatePreprocessor;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.models.ObjectReference;
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
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;

@KadaiIntegrationTest
class CompleteTaskWithSpiAccTest {

  @KadaiInject TaskService taskService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @SuppressWarnings("JUnitMalformedDeclaration")
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
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = DefaultTestEntities.defaultTestObjectReference().build();
  }

  private TaskBuilder createTaskClaimedByUser(String user) {
    return createDefaultTask().owner(user).state(TaskState.CLAIMED).claimed(Instant.now());
  }

  private TaskBuilder createTaskInReviewByUser(String user) {
    return createDefaultTask().owner(user).state(TaskState.IN_REVIEW).claimed(Instant.now());
  }

  private TaskBuilder createDefaultTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference);
  }

  static class AlwaysRequireReview implements ReviewRequiredProvider {
    @Override
    public boolean reviewRequired(Task task) {
      return true;
    }
  }

  static class NeverRequireReview implements ReviewRequiredProvider {
    @Override
    public boolean reviewRequired(Task task) {
      return false;
    }
  }

  static class SetCustomAttributeToEndstate implements TaskEndstatePreprocessor {
    @Override
    public Task processTaskBeforeEndstate(Task task) {
      String endstate = task.getState().toString();
      task.getCustomAttributeMap()
          .put(
              "camunda:attribute1",
              "{\"valueInfo\":{\"objectTypeName\":\"java.lang.String\"},"
                  + "\"type\":\"String\",\"value\":\""
                  + endstate
                  + "\"}");
      return task;
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = ReviewRequiredProvider.class,
      serviceProviders = AlwaysRequireReview.class)
  class ServiceProviderAlwaysRequiringReview {

    @KadaiInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @TestFactory
    Stream<DynamicTest> should_RequireReview_When_UserTriesToCompleteTask() throws Exception {
      List<Task> tasks =
          List.of(
              createTaskClaimedByUser("user-1-1").buildAndStore(taskService),
              createTaskInReviewByUser("user-1-1").buildAndStore(taskService));
      ThrowingConsumer<Task> test =
          task -> {
            Task processedTask = taskService.completeTask(task.getId());
            assertThat(processedTask.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
          };
      return DynamicTest.stream(
          tasks.iterator(), t -> "Try to complete " + t.getState().name() + " Task", test);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = ReviewRequiredProvider.class,
      serviceProviders = NeverRequireReview.class)
  class ServiceProviderNeverRequiringReview {

    @KadaiInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @TestFactory
    Stream<DynamicTest> should_CompleteTask_When_UserTriesToCompleteTask() throws Exception {
      List<Task> tasks =
          List.of(
              createTaskClaimedByUser("user-1-1").buildAndStore(taskService),
              createTaskInReviewByUser("user-1-1").buildAndStore(taskService));
      ThrowingConsumer<Task> test =
          task -> {
            Task processedTask = taskService.completeTask(task.getId());

            assertThat(processedTask.getState()).isEqualTo(TaskState.COMPLETED);
          };

      return DynamicTest.stream(
          tasks.iterator(), t -> "Try to complete " + t.getState().name() + " Task", test);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = ReviewRequiredProvider.class,
      serviceProviders = {AlwaysRequireReview.class, NeverRequireReview.class})
  class ServiceProvidersAlwaysAndNeverRequiringReview {

    @KadaiInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @TestFactory
    Stream<DynamicTest> should_RequestReview_When_UserTriesToCompleteTask() throws Exception {
      List<Task> tasks =
          List.of(
              createTaskClaimedByUser("user-1-1").buildAndStore(taskService),
              createTaskInReviewByUser("user-1-1").buildAndStore(taskService));
      ThrowingConsumer<Task> test =
          task -> {
            Task processedTask = taskService.completeTask(task.getId());

            assertThat(processedTask.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
          };
      return DynamicTest.stream(
          tasks.iterator(), t -> "Try to complete " + t.getState().name() + " Task", test);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = TaskEndstatePreprocessor.class,
      serviceProviders = SetCustomAttributeToEndstate.class)
  class ServiceProviderSetsCustomAttributeToCompleted {

    @KadaiInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @Test
    void should_SetCustomAttribute_When_UserCompletesTask() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);
      Task processedTask = taskService.completeTask(task.getId());
      assertThat(processedTask.getState()).isEqualTo(TaskState.COMPLETED);
      assertThat(processedTask.getCustomAttributeMap())
          .containsEntry(
              "camunda:attribute1",
              "{\"valueInfo\":{\"objectTypeName\":\"java.lang.String\"},"
                  + "\"type\":\"String\",\"value\":\"COMPLETED\"}");
    }
  }
}
