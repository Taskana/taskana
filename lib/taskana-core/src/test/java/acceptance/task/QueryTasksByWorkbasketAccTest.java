package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
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
 * Acceptance test for all "query tasks by workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksByWorkbasketAccTest extends AbstractAccTest {

    public QueryTasksByWorkbasketAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfNoOpenerPermissionOnQueriedWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("USER_2_1")
            .list();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfNoOpenerPermissionOnAtLeastOneQueriedWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("USER_1_1", "USER_2_1")
            .list();
    }

    @WithAccessId(userName = "user_1_1")
    @Test
    public void testQueryAllTasksForDomains() throws NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .domainIn("DOMAIN_B", "", "DOMAIN_A")
            .list();
        assertThat(results.size(), equalTo(17));

        results = taskService.createTaskQuery()
            .domainIn("DOMAIN_A")
            .workbasketKeyIn("USER_1_1")
            .list();
        assertThat(results.size(), equalTo(2));
    }

    @WithAccessId(userName = "user_1_1")
    @Test
    public void testQueryTasksForWorkbasket() throws NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .domainIn("DOMAIN_A")
            .workbasketKeyIn("USER_1_1")
            .list();
        assertThat(results.size(), equalTo(2));
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
