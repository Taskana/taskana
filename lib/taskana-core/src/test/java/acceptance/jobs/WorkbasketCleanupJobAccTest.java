package acceptance.jobs;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery;
import pro.taskana.TaskService;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.jobs.WorkbasketCleanupJob;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

import java.util.List;

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
    public void shouldCleanWorkbasketMarkedForDeletion() throws Exception {
        long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(25, totalWorkbasketCount);
        List<WorkbasketSummary> workbaskets = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC", "sort001")
            .orderByKey(
                BaseQuery.SortDirection.ASCENDING)
            .list();
        assertEquals(taskService.allTasksCompletedByWorkbasketId(workbaskets.get(1).getId()), true);
        workbasketService.markWorkbasketForDeletion(workbaskets.get(1).getId());

        WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
        job.run();

        totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(24, totalWorkbasketCount);
    }

    @WithAccessId(userName = "admin")
    @Test
    public void shouldCleanWorkbasketMarkedForDeletionWithCompletedTasks() throws Exception {
        long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(25, totalWorkbasketCount);
        List<WorkbasketSummary> workbaskets = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC", "sort001")
            .orderByKey(
                BaseQuery.SortDirection.ASCENDING)
            .list();
        assertEquals(taskService.allTasksCompletedByWorkbasketId(workbaskets.get(0).getId()), false);
        assertEquals(taskService.allTasksCompletedByWorkbasketId(workbaskets.get(1).getId()), true);
        workbasketService.markWorkbasketForDeletion(workbaskets.get(0).getId());
        workbasketService.markWorkbasketForDeletion(workbaskets.get(1).getId());

        WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
        job.run();

        totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(24, totalWorkbasketCount);
    }
}
