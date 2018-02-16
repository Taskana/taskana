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
import pro.taskana.KeyDomain;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaRuntimeException;
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
    public void testGetFirstPageOfTaskQueryWithOffset()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(0, 10);
        assertThat(results.size(), equalTo(10));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testSecondPageOfTaskQueryWithOffset()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(10, 10);
        assertThat(results.size(), equalTo(10));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testListOffsetAndLimitOutOfBounds()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        // both will be 0, working
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(-1, -3);
        assertThat(results.size(), equalTo(0));

        // limit will be 0
        results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(1, -3);
        assertThat(results.size(), equalTo(0));

        // offset will be 0
        results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(-1, 3);
        assertThat(results.size(), equalTo(3));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testPaginationWithPages()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        // Getting full page
        int pageNumber = 1;
        int pageSize = 4;
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(4));

        // Getting full page
        pageNumber = 3;
        pageSize = 1;
        results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(1));

        // Getting last results on 1 big page
        pageNumber = 0;
        pageSize = 100;
        results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(22));

        // Getting last results on multiple pages
        pageNumber = 2;
        pageSize = 10;
        results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(2));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testPaginationNullAndNegativeLimitsIgnoring()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        // 0 limit/size = 0 results
        int pageNumber = 1;
        int pageSize = 0;
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative size will be 0 = 0 results
        pageNumber = 1;
        pageSize = -1;
        results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative page = first page
        pageNumber = -1;
        pageSize = 10;
        results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(10));
    }

    /**
     * Testcase only for DB2 users, because H2 doesn´t throw a Exception when the offset is set to high.<br>
     * Using DB2 should throw a unchecked RuntimeException for a offset which is out of bounds.
     *
     * @throws SQLException
     * @throws NotAuthorizedException
     * @throws InvalidArgumentException
     */
    @Ignore
    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test(expected = TaskanaRuntimeException.class)
    public void testPaginationThrowingExceptionWhenPageOutOfBounds()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        // entrypoint set outside result amount
        int pageNumber = 5;
        int pageSize = 10;
        taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testCountOfTaskQuery()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        long count = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .count();
        assertThat(count, equalTo(22L));
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
