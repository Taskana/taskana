package acceptance.taskpreprocessing;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.DefaultTestEntities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;
import testapi.WithServiceProvider;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.builder.WorkbasketAccessItemBuilder;

/** Acceptance test for "task preprocessing" scenario. */
@TaskanaIntegrationTest
@WithServiceProvider(
    serviceProviderInterface = CreateTaskPreprocessor.class,
    serviceProviders = CreateTaskPreprocessingAccTest.TestCreateTaskPreprocessorProvider.class)
class CreateTaskPreprocessingAccTest {

  @TaskanaInject TaskService taskService;

  WorkbasketSummary workbasketSummary;
  ClassificationSummary classificationSummary;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    classificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStore(classificationService)
            .asSummary();

    workbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService).asSummary();

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
  void should_processTaskBeforeCreation_When_CreateTaskPreprocessorEnabled() throws Exception {
    Task newTaskToCreate = taskService.newTask(workbasketSummary.getId());
    newTaskToCreate.setClassificationKey(classificationSummary.getKey());
    newTaskToCreate.setPrimaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());

    Task createdTask = taskService.createTask(newTaskToCreate);

    assertThat(createdTask.getCustomAttribute(TaskCustomField.CUSTOM_1))
        .isEqualTo("preprocessedCustomField");
  }

  public static class TestCreateTaskPreprocessorProvider implements CreateTaskPreprocessor {

    @Override
    public void processTaskBeforeCreation(Task taskToProcess) {
      taskToProcess.setCustomAttribute(TaskCustomField.CUSTOM_1, "preprocessedCustomField");
    }
  }
}
