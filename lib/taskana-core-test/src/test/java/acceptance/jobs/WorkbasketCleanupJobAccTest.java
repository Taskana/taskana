package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.jobs.WorkbasketCleanupJob;

// All tests are executed as admin, because the jobrunner needs admin rights.
@TaskanaIntegrationTest
class WorkbasketCleanupJobAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject TaskanaEngine taskanaEngine;

  ClassificationSummary classification;
  ObjectReference primaryObjRef;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    classification =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    primaryObjRef = DefaultTestEntities.defaultTestObjectReference().build();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CleanWorkbasketMarkedForDeletion_When_WorkbasketHasNoTasks() throws Exception {
    WorkbasketSummary wbSummary =
        DefaultTestEntities.defaultTestWorkbasket()
            .markedForDeletion(true)
            .buildAndStore(workbasketService);

    WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
    job.run();

    List<WorkbasketSummary> wbSummaries =
        workbasketService.createWorkbasketQuery().idIn(wbSummary.getId()).list();
    assertThat(wbSummaries).isEmpty();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotCleanWorkbasketMarkedForDeletion_When_WorkbasketHasTasks() throws Exception {
    Workbasket wb = DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService);
    TaskBuilder.newTask()
        .workbasketSummary(wb.asSummary())
        .classificationSummary(classification)
        .primaryObjRef(primaryObjRef)
        .state(TaskState.COMPLETED)
        .buildAndStore(taskService);

    // Workbasket with completed task will be marked for deletion.
    workbasketService.deleteWorkbasket(wb.getId());
    WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
    job.run();

    List<WorkbasketSummary> wbSummaries = workbasketService.createWorkbasketQuery().list();
    assertThat(wbSummaries).hasSize(1);
  }
}
