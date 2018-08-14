package acceptance.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
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
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

import java.util.ArrayList;
import java.util.List;

/**
 * Acceptance test for all "jobs tasks runner" scenarios.
 */
@RunWith(JAASRunner.class)
public class TaskCleanupJobAccTest extends AbstractAccTest {

    TaskService taskService;

    @Before
    public void before() {
        taskService = taskanaEngine.getTaskService();
    }

    @WithAccessId(userName = "admin")
    @Test
    public void shouldCleanCompletedTasksUntilDate() throws Exception {
        long totalTasksCount = taskService.createTaskQuery().count();
        assertEquals(72, totalTasksCount);

        taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(false);

        TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
        job.run();

        totalTasksCount = taskService.createTaskQuery().count();
        assertEquals(66, totalTasksCount);
    }

    @WithAccessId(userName = "admin")
    @Test
    public void shouldCleanCompletedTasksUntilDateWithSameParentBussiness() throws Exception {
        long totalTasksCount = taskService.createTaskQuery().count();
        assertEquals(67, totalTasksCount);

        taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

        List<TaskSummary> tasks = taskService.createTaskQuery().parentBusinessProcessIdIn("DOC_0000000000000000006").list();
        List<String> ids = new ArrayList<>();
        tasks.forEach(item -> {
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
    public void shouldNotCleanCompleteTasksAfterDefinedDay() throws Exception {
        Task createdTask = createAndCompleteTask();

        TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
        job.run();

        Task completedCreatedTask = taskService.getTask(createdTask.getId());
        assertNotNull(completedCreatedTask);
    }

    private Task createAndCompleteTask() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);
        taskService.claim(createdTask.getId());
        taskService.completeTask(createdTask.getId());
        return createdTask;
    }

}
