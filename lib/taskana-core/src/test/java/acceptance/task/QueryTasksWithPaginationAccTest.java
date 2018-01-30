package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query tasks by workbasket with pagination" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksWithPaginationAccTest extends AbstractAccTest {

    public QueryTasksWithPaginationAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testGetFirstPageOfTaskQuery()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("GPK_KSC")
            .list(0, 10);
        assertThat(results.size(), equalTo(10));
        assertThat(results.get(0).getTaskId(), equalTo("TKI:000000000000000000000000000000000003"));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testSecondPageOfTaskQuery()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("GPK_KSC")
            .list(10, 10);
        assertThat(results.size(), equalTo(10));
        assertThat(results.get(0).getTaskId(), equalTo("TKI:000000000000000000000000000000000013"));
    }

    @Ignore
    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testCountOfTaskQuery()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        // TaskService taskService = taskanaEngine.getTaskService();
        // long count = taskService.createTaskQuery()
        // .workbasketKeyIn("GPK_KSC")
        // .count();
        // assertThat(long, equalTo(21));
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
