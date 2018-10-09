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
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
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
    public void shouldCleanWorkbasketMarkedForDeletion() throws Exception {
        long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(25, totalWorkbasketCount);
        List<WorkbasketSummary> workbaskets = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC", "sort001")
            .orderByKey(
                BaseQuery.SortDirection.ASCENDING)
            .list();
        long numTasksNotCompletedInWorkbasket = getNumberTaskNotCompleted(workbaskets.get(1).getId());
        assertEquals(numTasksNotCompletedInWorkbasket, 0);
        workbasketService.deleteWorkbasket(workbaskets.get(1).getId());

        WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
        job.run();

        totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(24, totalWorkbasketCount);
    }

    @WithAccessId(userName = "admin")
    @Test
    public void shouldCleanWorkbasketMarkedForDeletionWithCompletedTasks() throws TaskanaException {
        long totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(25, totalWorkbasketCount);
        List<WorkbasketSummary> workbaskets = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC", "sort001")
            .orderByKey(
                BaseQuery.SortDirection.ASCENDING)
            .list();
        long numTasksNotCompletedInWorkbasket = getNumberTaskNotCompleted(workbaskets.get(0).getId());
        assertNotEquals(numTasksNotCompletedInWorkbasket, 0);

        numTasksNotCompletedInWorkbasket = getNumberTaskNotCompleted(workbaskets.get(1).getId());
        assertEquals(numTasksNotCompletedInWorkbasket, 0);
        try {
            workbasketService.deleteWorkbasket(workbaskets.get(1).getId());
            workbasketService.deleteWorkbasket(workbaskets.get(0).getId());
        } catch (NotAuthorizedException | WorkbasketNotFoundException | WorkbasketInUseException | InvalidArgumentException e) {
            // workbaskets.get(0) contains non-completed tasks and can´t be marked for deletion.
        }

        WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
        job.run();

        totalWorkbasketCount = workbasketService.createWorkbasketQuery().count();
        assertEquals(24, totalWorkbasketCount);
    }

    private long getNumberTaskNotCompleted(String workbasketId) {
        return taskService
            .createTaskQuery()
            .workbasketIdIn(workbasketId)
            .stateNotIn(
                TaskState.COMPLETED)
            .count();
    }
}
