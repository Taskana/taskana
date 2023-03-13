package acceptance.task.update;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.ClassificationBuilder;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class UpdateManualPriorityAccTest {

  private static final int CLASSIFICATION_PRIORITY = 9;

  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;

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
    JobRunner runner = new JobRunner(taskanaEngine);
    // run the TaskRefreshJob that was scheduled by the ClassificationChangedJob.
    runner.runJobs();
    Thread.sleep(10); // otherwise the next runJobs call intermittently doesn't find the Job created
    // by the previous step (it searches with DueDate < CurrentTime)
    runner.runJobs();
  }
}
