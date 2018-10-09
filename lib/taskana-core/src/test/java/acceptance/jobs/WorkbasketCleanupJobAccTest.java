package acceptance.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.jobs.TaskCleanupJob;
import pro.taskana.jobs.WorkbasketCleanupJob;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "jobs workbasket runner" scenarios.
 */
@RunWith(JAASRunner.class)
public class WorkbasketCleanupJobAccTest extends AbstractAccTest {

    WorkbasketService workbasketService;
    TaskService taskService;

    @Before
    public void before() {
        workbasketService = taskanaEngine.getWorkbasketService();
        taskService = taskanaEngine.getTaskService();
    }

    @After
    public void after() throws Exception {
        resetDb(true);
    }

    @WithAccessId(userName = "admin")
    @Test
    public void shouldCleanWorkbasketMarkedForDeletionWithoutTasks() throws TaskanaException {
        long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(25, totalWorkbasketCount);
        List<WorkbasketSummary> workbaskets = workbasketService.createWorkbasketQuery()
            .keyIn("TEAMLEAD_1")
            .orderByKey(
                BaseQuery.SortDirection.ASCENDING)
            .list();

        assertEquals(getNumberTaskNotCompleted(workbaskets.get(0).getId()), 0);
        assertEquals(getNumberTaskCompleted(workbaskets.get(0).getId()), 1);

        // Workbasket with completed task will be marked for deletion.
        workbasketService.deleteWorkbasket(workbaskets.get(0).getId());

        // Run taskCleanupJob for deleting completing tasks before running workbasketCleanupJob
        TaskCleanupJob taskCleanupJob = new TaskCleanupJob(taskanaEngine, null, null);
        taskCleanupJob.run();

        assertEquals(getNumberTaskCompleted(workbaskets.get(0).getId()), 0);

        WorkbasketCleanupJob workbasketCleanupJob = new WorkbasketCleanupJob(taskanaEngine, null, null);
        workbasketCleanupJob.run();

        totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(24, totalWorkbasketCount);
    }

    @WithAccessId(userName = "admin")
    @Test
    public void shouldNotCleanWorkbasketMarkedForDeletionIfWorkbasketHasTasks() throws Exception {
        long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(25, totalWorkbasketCount);
        List<WorkbasketSummary> workbaskets = workbasketService.createWorkbasketQuery()
            .keyIn("TEAMLEAD_1")
            .orderByKey(
                BaseQuery.SortDirection.ASCENDING)
            .list();

        assertNotEquals(getNumberTaskCompleted(workbaskets.get(0).getId()), 0);

        // Workbasket with completed task will be marked for deletion.
        workbasketService.deleteWorkbasket(workbaskets.get(0).getId());

        WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
        job.run();

        totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(25, totalWorkbasketCount);
    }

    private long getNumberTaskNotCompleted(String workbasketId) {
        return taskService
            .createTaskQuery()
            .workbasketIdIn(workbasketId)
            .stateNotIn(
                TaskState.COMPLETED)
            .count();
    }

    private long getNumberTaskCompleted(String workbasketId) {
        return taskService
            .createTaskQuery()
            .workbasketIdIn(workbasketId)
            .stateIn(
                TaskState.COMPLETED)
            .count();
    }
}
