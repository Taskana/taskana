package acceptance.workbasket;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.model.WorkbasketAuthorization;
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
            .accessIdsHavePersmission(WorkbasketAuthorization.APPEND, "user_1_1")
            .list();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("USER_1_1", results.get(1).getKey());
    }

    @Test
    public void testQueryAllTransferTargetsForUserAndGroup()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePersmission(WorkbasketAuthorization.APPEND, "user_1_1", "group_1")
            .list();
        Assert.assertEquals(9, results.size());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = { "group_1" })
    @Test
    public void testQueryAllTransferTargetsForUserAndGroupFromSubject()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .callerHasPermission(WorkbasketAuthorization.APPEND)
            .list();
        Assert.assertEquals(9, results.size());
    }

    @WithAccessId(userName = "user_1_1")
    @Test
    public void testQueryAllAvailableWorkbasketForOpeningForUserAndGroupFromSubject()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .callerHasPermission(WorkbasketAuthorization.READ)
            .list();
        Assert.assertEquals(2, results.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
