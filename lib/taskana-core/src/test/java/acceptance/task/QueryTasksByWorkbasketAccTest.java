package acceptance.task;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query tasks by workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksByWorkbasketAccTest extends AbstractAccTest {

    public QueryTasksByWorkbasketAccTest() {
        super();
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfNoOpenerPermissionOnQueriedWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        // TaskService taskService = taskanaEngine.getTaskService();
        // List<TaskSummary> results = taskService.createTaskQuery()
        // .workbasketKeyIn("USER_2_1")
        // .list();
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testThrowsExceptionIfNoOpenerPermissionOnAtLeastOneQueriedWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        // TaskService taskService = taskanaEngine.getTaskService();
        // List<TaskSummary> results = taskService.createTaskQuery()
        // .workbasketKeyIn("USER_1_1", "USER_2_1")
        // .list();
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
