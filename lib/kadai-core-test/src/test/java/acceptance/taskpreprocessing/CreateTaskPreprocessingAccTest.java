package acceptance.taskpreprocessing;

import static acceptance.taskpreprocessing.CreateTaskPreprocessingAccTest.TestCreateTaskPreprocessorProvider.SPI_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import acceptance.taskpreprocessing.CreateTaskPreprocessingAccTest.TestCreateTaskPreprocessorProvider;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.spi.task.api.CreateTaskPreprocessor;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Task;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Acceptance test for "task preprocessing" scenario. */
@KadaiIntegrationTest
@WithServiceProvider(
    serviceProviderInterface = CreateTaskPreprocessor.class,
    serviceProviders = TestCreateTaskPreprocessorProvider.class)
class CreateTaskPreprocessingAccTest {

  @KadaiInject TaskService taskService;

  WorkbasketSummary workbasketSummary;
  ClassificationSummary classificationSummary;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    classificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);

    workbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .accessId("user-1-1")
        .workbasketId(workbasketSummary.getId())
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ProcessTaskBeforeCreation_When_CreateTaskPreprocessorEnabled() throws Exception {
    Task newTaskToCreate = taskService.newTask(workbasketSummary.getId());
    newTaskToCreate.setClassificationKey(classificationSummary.getKey());
    newTaskToCreate.setPrimaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());

    Task createdTask = taskService.createTask(newTaskToCreate);

    assertThat(createdTask.getCustomField(TaskCustomField.CUSTOM_1)).isEqualTo(SPI_VALUE);
  }

  static class TestCreateTaskPreprocessorProvider implements CreateTaskPreprocessor {

    static final String SPI_VALUE = "preprocessedCustomField";

    @Override
    public void processTaskBeforeCreation(Task taskToProcess) {
      taskToProcess.setCustomField(TaskCustomField.CUSTOM_1, SPI_VALUE);
    }
  }
}
