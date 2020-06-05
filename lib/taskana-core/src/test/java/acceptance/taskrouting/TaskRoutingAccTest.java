package acceptance.taskrouting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskRoutingAccTest extends AbstractAccTest {

  @WithAccessId(user = "admin", groups = "group_1")
  @Test
  void testCreateTaskWithoutWorkbasketAndVoidNewTaskMethod()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {
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
    assertThat("WBI:100000000000000000000000000000000011")
        .isEqualTo(createdTask.getWorkbasketSummary().getId());
  }

  @WithAccessId(user = "admin", groups = "group_1")
  @Test
  void testCreateTaskWithNullWorkbasket()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {
    TaskImpl createdTaskA = createTask("DOMAIN_A", "L12010");
    assertThat("WBI:100000000000000000000000000000000001")
        .isEqualTo(createdTaskA.getWorkbasketSummary().getId());
    TaskImpl createdTaskB = createTask("DOMAIN_B", "T21001");
    assertThat("WBI:100000000000000000000000000000000011")
        .isEqualTo(createdTaskB.getWorkbasketSummary().getId());
    assertThatThrownBy(() -> createTask(null, "L12010"))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "admin", groups = "group_1")
  @Test
  void testCreateTaskWithNullRouting() throws InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask(null, "DOMAIN_A");
    newTask.setClassificationKey("L12010");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setCustomAttribute("7", "noRouting");
    assertThatThrownBy(() -> taskService.createTask(newTask))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "admin", groups = "group_1")
  @Test
  void testCreateTaskWithRoutingToMultipleWorkbaskets() throws InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask(null, "DOMAIN_B");
    newTask.setClassificationKey("L12010");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setCustomAttribute("7", "multipleWorkbaskets");
    assertThatThrownBy(() -> taskService.createTask(newTask))
        .isInstanceOf(InvalidArgumentException.class);
  }

  private TaskImpl createTask(String domain, String classificationKey)
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {
    TaskService taskService = taskanaEngine.getTaskService();

    Task newTask = taskService.newTask(null, domain);
    newTask.setClassificationKey(classificationKey);

    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    TaskImpl createdTask = (TaskImpl) taskService.createTask(newTask);
    return createdTask;
  }
}
