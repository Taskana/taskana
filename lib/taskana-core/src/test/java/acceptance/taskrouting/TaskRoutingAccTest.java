package acceptance.taskrouting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import helper.AbstractAccTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskRoutingAccTest extends AbstractAccTest {

  @WithAccessId(user = "admin")
  @Test
  void testCreateTaskWithoutWorkbasketAndVoidNewTaskMethod() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();

    Task newTask = taskService.newTask();
    newTask.setClassificationKey("L10303");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    final Task taskToCreate = newTask;
    assertThatThrownBy(() -> taskService.createTask(taskToCreate))
        .isInstanceOf(InvalidArgumentException.class);
    ((TaskImpl) taskToCreate).setDomain("DOMAIN_C");
    assertThatThrownBy(() -> taskService.createTask(taskToCreate))
        .isInstanceOf(InvalidArgumentException.class);
    ((TaskImpl) taskToCreate).setDomain("DOMAIN_B");
    Task createdTask = taskService.createTask(taskToCreate);
    assertThat(createdTask.getWorkbasketSummary().getId())
        .isEqualTo("WBI:100000000000000000000000000000000011");
  }

  @WithAccessId(user = "admin")
  @Test
  void testCreateTaskWithNullWorkbasket() throws Exception {
    TaskImpl createdTaskA = createTask("DOMAIN_A", "L12010");
    assertThat(createdTaskA.getWorkbasketSummary().getId())
        .isEqualTo("WBI:100000000000000000000000000000000001");
    TaskImpl createdTaskB = createTask("DOMAIN_B", "T21001");
    assertThat(createdTaskB.getWorkbasketSummary().getId())
        .isEqualTo("WBI:100000000000000000000000000000000011");
    assertThatThrownBy(() -> createTask(null, "L12010"))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testCreateTaskWithNullRouting() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask(null, "DOMAIN_A");
    newTask.setClassificationKey("L12010");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setCustomAttribute(TaskCustomField.CUSTOM_7, "noRouting");
    assertThatThrownBy(() -> taskService.createTask(newTask))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testCreateTaskWithRoutingToMultipleWorkbaskets() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask(null, "DOMAIN_B");
    newTask.setClassificationKey("L12010");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setCustomAttribute(TaskCustomField.CUSTOM_7, "multipleWorkbaskets");
    assertThatThrownBy(() -> taskService.createTask(newTask))
        .isInstanceOf(InvalidArgumentException.class);
  }

  private TaskImpl createTask(String domain, String classificationKey) throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();

    Task newTask = taskService.newTask(null, domain);
    newTask.setClassificationKey(classificationKey);

    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    return (TaskImpl) taskService.createTask(newTask);
  }
}
