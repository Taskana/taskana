package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JaasExtension.class)
class TaskCleanupJobAccTest extends AbstractAccTest {

  TaskService taskService;

  @BeforeEach
  void before() throws SQLException {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(false);
    taskService = taskanaEngine.getTaskService();
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldCleanCompletedTasksUntilDate() throws Exception {

    createAndCompleteTask();
    long totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(84);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(false);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(68);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldCleanCompletedTasksUntilDateWithSameParentBussiness() throws Exception {
    long totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(83);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

    List<TaskSummary> tasks =
        taskService.createTaskQuery().parentBusinessProcessIdIn("DOC_0000000000000000006").list();
    List<String> ids = new ArrayList<>();
    tasks.forEach(
        item -> {
          if (item.getCompleted() == null) {
            ids.add(item.getId());
          }
        });
    taskService.deleteTasks(ids);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskService.createTaskQuery().count();
    assertThat(totalTasksCount).isEqualTo(66);
  }

  @WithAccessId(user = "admin")
  @Test
  void shouldNotCleanCompleteTasksAfterDefinedDay() throws Exception {
    Task createdTask = createAndCompleteTask();

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    Task completedCreatedTask = taskService.getTask(createdTask.getId());
    assertThat(completedCreatedTask).isNotNull();
  }

  private Task createAndCompleteTask()
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);
    taskService.claim(createdTask.getId());
    taskService.completeTask(createdTask.getId());
    return createdTask;
  }
}
