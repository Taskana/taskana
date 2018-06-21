package acceptance.task;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BulkOperationResults;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.JobTaskRunner;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "jobs tasks runner" scenarios.
 */
@RunWith(JAASRunner.class)
public class JobTaskRunnerAccTest extends AbstractAccTest {

    TaskService taskService;

    @Before
    public void before() {
        taskService = taskanaEngine.getTaskService();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void shouldCleanCompletedTasksUntilDate() {

        JobTaskRunner runner = new JobTaskRunner(taskanaEngine);
        Instant completeUntilDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .minusDays(14)
            .toInstant();

        List<TaskSummary> tasksCompletedUntilDateBefore = getTaskCompletedUntilDate(completeUntilDate);
        BulkOperationResults<String, Exception> results = runner.runCleanCompletedTasks(completeUntilDate);
        List<TaskSummary> tasksCompletedUntilDateAfter = getTaskCompletedUntilDate(completeUntilDate);

        assertFalse(results.containsErrors());
        assertTrue(tasksCompletedUntilDateBefore.size() > 0);
        assertTrue(tasksCompletedUntilDateAfter.size() == 0);

    }
    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void shouldNotCleanCompleteTasksAfterDefinedDay()
        throws TaskNotFoundException, NotAuthorizedException, InvalidStateException, InvalidOwnerException,
        TaskAlreadyExistException, InvalidArgumentException, WorkbasketNotFoundException,
        ClassificationNotFoundException {

        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);
        taskService.claim(createdTask.getId());
        taskService.completeTask(createdTask.getId());

        JobTaskRunner runner = new JobTaskRunner(taskanaEngine);
        Instant completeUntilDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .minusDays(14)
            .toInstant();
        runner.runCleanCompletedTasks(completeUntilDate);
        Task completedCreatedTask = taskService.getTask(createdTask.getId());
        assertNotNull(completedCreatedTask);

    }

    private List<TaskSummary> getTaskCompletedUntilDate(Instant date) {
        return taskService.createTaskQuery()
            .completedWithin(new TimeInterval(null, date))
            .list();
    }

}
