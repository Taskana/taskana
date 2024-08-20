package acceptance.task.update;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.jobs.JobRunner;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.ClassificationBuilder;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@KadaiIntegrationTest
class UpdateManualPriorityAccTest {

  private static final int CLASSIFICATION_PRIORITY = 9;

  @KadaiInject KadaiEngine kadaiEngine;
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification()
            .priority(CLASSIFICATION_PRIORITY)
            .buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_SetPriorityToManualPriority_When_PositiveManualPriority() throws Exception {
    Task task = taskService.newTask(defaultWorkbasketSummary.getId());
    task.setClassificationKey(defaultClassificationSummary.getKey());
    task.setPrimaryObjRef(defaultObjectReference);
    task.setManualPriority(123);
    Task result = taskService.createTask(task);

    assertThat(result.getPriority()).isEqualTo(result.getManualPriority()).isEqualTo(123);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_SetPriorityAccordingToClassification_When_NegativeManualPriority() throws Exception {
    Task task = taskService.newTask(defaultWorkbasketSummary.getId());
    task.setClassificationKey(defaultClassificationSummary.getKey());
    task.setPrimaryObjRef(defaultObjectReference);
    task.setManualPriority(-5);
    Task result = taskService.createTask(task);

    assertThat(result.getPriority()).isEqualTo(CLASSIFICATION_PRIORITY);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotUpdatePriorityAccordingToClassification_When_PositiveManualPriority()
      throws Exception {
    Classification classification =
        ClassificationBuilder.newClassification()
            .key("Key")
            .domain(defaultWorkbasketSummary.getDomain())
            .buildAndStore(classificationService);
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(classification.asSummary())
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .manualPriority(123)
            .buildAndStore(taskService);

    classification.setPriority(1000);
    updateClassificationAndRunAssociatedJobs(classification);

    Task result = taskService.getTask(task.getId());
    assertThat(result.getPriority()).isEqualTo(result.getManualPriority()).isEqualTo(123);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_UpdatePriorityAccordingToClassification_When_UpdatedTaskHasNegativeManualPriority()
      throws Exception {
    Task task = createDefaultTask().manualPriority(123).buildAndStore(taskService);

    task.setManualPriority(-5);
    Task result = taskService.updateTask(task);

    assertThat(result.getPriority()).isEqualTo(CLASSIFICATION_PRIORITY);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotUpdatePriorityAccordingToClassification_When_UpdatedTaskHasZeroManualPriority()
      throws Exception {
    Task task = createDefaultTask().manualPriority(123).buildAndStore(taskService);

    task.setManualPriority(0);
    Task result = taskService.updateTask(task);

    assertThat(result.getPriority()).isZero();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateManualPriority_When_UpdatedTaskHasNewPositiveManualPriority() throws Exception {
    Task task = createDefaultTask().manualPriority(123).buildAndStore(taskService);

    task.setManualPriority(42);
    Task result = taskService.updateTask(task);

    assertThat(result.getPriority()).isEqualTo(result.getManualPriority()).isEqualTo(42);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateManualPriority_When_TaskWithoutManualPriorityHasManualPriorityAfterUpdate()
      throws Exception {
    Task task = createDefaultTask().manualPriority(-1).buildAndStore(taskService);

    task.setManualPriority(42);
    Task result = taskService.updateTask(task);

    assertThat(result.getPriority()).isEqualTo(result.getManualPriority()).isEqualTo(42);
  }

  private TaskBuilder createDefaultTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference);
  }

  private void updateClassificationAndRunAssociatedJobs(Classification classification)
      throws Exception {
    classificationService.updateClassification(classification);
    Thread.sleep(10);
    // run the ClassificationChangedJob
    JobRunner runner = new JobRunner(kadaiEngine);
    // run the TaskRefreshJob that was scheduled by the ClassificationChangedJob.
    runner.runJobs();
    Thread.sleep(10); // otherwise the next runJobs call intermittently doesn't find the Job created
    // by the previous step (it searches with DueDate < CurrentTime)
    runner.runJobs();
  }
}
