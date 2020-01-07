package acceptance.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.jobs.TaskCleanupJob;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "jobs tasks runner" scenarios. */
@ExtendWith(JAASExtension.class)
class TaskCleanupJobAccTest extends AbstractAccTest {

  TaskService taskService;

  @BeforeEach
  void before() throws SQLException {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(false);
    taskService = taskanaEngine.getTaskService();
  }

  @WithAccessId(userName = "admin")
  @Test
  void shouldCleanCompletedTasksUntilDate() throws Exception {

    createAndCompleteTask();
    long totalTasksCount = taskService.createTaskQuery().count();
    assertEquals(74, totalTasksCount);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(false);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskService.createTaskQuery().count();
    assertEquals(68, totalTasksCount);
  }

  @WithAccessId(userName = "admin")
  @Test
  void shouldCleanCompletedTasksUntilDateWithSameParentBussiness() throws Exception {
    long totalTasksCount = taskService.createTaskQuery().count();
    assertEquals(73, totalTasksCount);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

    List<TaskSummary> tasks =
        taskService.createTaskQuery().parentBusinessProcessIdIn("DOC_0000000000000000006").list();
    List<String> ids = new ArrayList<>();
    tasks.forEach(
        item -> {
          if (item.getCompleted() == null) {
            ids.add(item.getTaskId());
          }
        });
    taskService.deleteTasks(ids);

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    totalTasksCount = taskService.createTaskQuery().count();
    assertEquals(66, totalTasksCount);
  }

  @WithAccessId(userName = "admin")
  @Test
  void shouldNotCleanCompleteTasksAfterDefinedDay() throws Exception {
    Task createdTask = createAndCompleteTask();

    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.run();

    Task completedCreatedTask = taskService.getTask(createdTask.getId());
    assertNotNull(completedCreatedTask);
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
