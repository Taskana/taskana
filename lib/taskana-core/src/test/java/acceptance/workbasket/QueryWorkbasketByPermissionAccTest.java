package acceptance.workbasket;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketSummary;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query workbasket by permission" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketByPermissionAccTest extends AbstractAccTest {

    public QueryWorkbasketByPermissionAccTest() {
        super();
    }

    @Test
    public void testQueryAllTransferTargetsForUser()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .access(WorkbasketAuthorization.APPEND, "user_1_1")
            .list();
        Assert.assertEquals(1L, results.size());
        Assert.assertEquals("USER_1_1", results.get(0).getKey());
    }

    @Test
    public void testQueryAllTransferTargetsForUserAndGroup()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .access(WorkbasketAuthorization.APPEND, "user_1_1", "group_1")
            .list();
        Assert.assertEquals(8L, results.size());
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryAllTransferTargetsForUserAndGroupFromSubject()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .access(WorkbasketAuthorization.APPEND)
            .list();
        Assert.assertEquals(7L, results.size());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryAllAvailableWorkbasketForOpeningForUserAndGroupFromSubject()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .access(WorkbasketAuthorization.OPEN)
            .list();
        Assert.assertEquals(5L, results.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
