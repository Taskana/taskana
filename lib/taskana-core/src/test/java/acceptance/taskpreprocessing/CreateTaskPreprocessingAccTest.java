package acceptance.taskpreprocessing;

import static org.assertj.core.api.Assertions.assertThat;

import helper.AbstractAccTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;

/** Acceptance test for "task preprocessing" scenario. */
@ExtendWith(JaasExtension.class)
class CreateTaskPreprocessingAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();

  @WithAccessId(user = "admin")
  @Test
  void should_processTaskBeforeCreation_When_CreateTaskPreprocessorEnabled() throws Exception {

    TaskImpl newTaskToCreate = (TaskImpl) taskService.newTask();

    newTaskToCreate.setClassificationKey("L10303");

    newTaskToCreate.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    newTaskToCreate.setWorkbasketKey("GPK_KSC");

    newTaskToCreate.setDomain("DOMAIN_A");

    Task createdTask = taskService.createTask(newTaskToCreate);

    assertThat(createdTask.getCustomAttribute(TaskCustomField.CUSTOM_1))
        .isEqualTo("preprocessedCustomField");
  }
}
