package acceptance.task.complete;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.time.Instant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.spi.task.api.ReviewRequiredProvider;
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
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = DefaultTestEntities.defaultTestObjectReference().build();
  }

  private TaskBuilder createTaskClaimedByUser(String user) {
    return createDefaultTask().owner(user).state(TaskState.CLAIMED).claimed(Instant.now());
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

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @WithServiceProvider(
      serviceProviderInterface = ReviewRequiredProvider.class,
      serviceProviders = AlwaysRequireReview.class)
  class ServiceProviderAlwaysRequiringReview {

    @TaskanaInject TaskService taskService;

    @WithAccessId(user = "user-1-1")
    @Test
    void should_RequireReview_When_UserTriesToCompleteTask() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);

      Task processedTask = taskService.completeTask(task.getId());

      assertThat(processedTask.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
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
    @Test
    void should_CompleteTask_When_UserTriesToCompleteTask() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);

      Task processedTask = taskService.completeTask(task.getId());

      assertThat(processedTask.getState()).isEqualTo(TaskState.COMPLETED);
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
    @Test
    void should_RequestReview_When_UserTriesToCompleteTask() throws Exception {
      Task task = createTaskClaimedByUser("user-1-1").buildAndStore(taskService);

      Task processedTask = taskService.completeTask(task.getId());

      assertThat(processedTask.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    }
  }
}
