package acceptance.task.complete;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

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
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.spi.task.api.ReviewRequiredProvider;
import pro.taskana.spi.task.api.TaskEndstatePreprocessor;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.ObjectReference;
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
class CompleteTaskWithSpiAccTest {

  @TaskanaInject TaskService taskService;

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

    @TaskanaInject TaskService taskService;

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

    @TaskanaInject TaskService taskService;

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

    @TaskanaInject TaskService taskService;

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

    @TaskanaInject TaskService taskService;

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
