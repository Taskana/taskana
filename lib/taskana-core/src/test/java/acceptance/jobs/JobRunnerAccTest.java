package acceptance.jobs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.TimeInterval;
import pro.taskana.jobs.JobRunner;
import pro.taskana.jobs.TaskCleanupJob;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "jobs tasks runner" scenarios.
 */
@RunWith(JAASRunner.class)
public class JobRunnerAccTest extends AbstractAccTest {

    TaskService taskService;

    @Before
    public void before() {
        taskService = taskanaEngine.getTaskService();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void shouldCleanCompletedTasksUntilDate() throws Exception {

        JobRunner runner = new JobRunner(taskanaEngine);
        Instant completeUntilDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .minusDays(14)
            .toInstant();

        List<TaskSummary> tasksCompletedUntilDateBefore = getTaskCompletedUntilDate(completeUntilDate);
        TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, completeUntilDate);
        runner.runJob(job);
        List<TaskSummary> tasksCompletedUntilDateAfter = getTaskCompletedUntilDate(completeUntilDate);

        assertTrue(tasksCompletedUntilDateBefore.size() > 0);
        assertTrue(tasksCompletedUntilDateAfter.size() == 0);

    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void shouldNotCleanCompleteTasksAfterDefinedDay()
        throws Exception {

        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);
        taskService.claim(createdTask.getId());
        taskService.completeTask(createdTask.getId());

        JobRunner runner = new JobRunner(taskanaEngine);
        Instant completeUntilDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .minusDays(14)
            .toInstant();

        List<TaskSummary> tasksCompletedUntilDateBefore = getTaskCompletedUntilDate(completeUntilDate);
        TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, completeUntilDate);
        runner.runJob(job);

        Task completedCreatedTask = taskService.getTask(createdTask.getId());
        assertNotNull(completedCreatedTask);

    }

    private List<TaskSummary> getTaskCompletedUntilDate(Instant date) {
        return taskService.createTaskQuery()
            .completedWithin(new TimeInterval(null, date))
            .list();
    }

}
