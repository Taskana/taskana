package acceptance.workbasket;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidRequestException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query workbasket by permission" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketByPermissionAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryWorkbasketByPermissionAccTest() {
        super();
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAllTransferTargetsForUser()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1")
            .list();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("USER_1_1", results.get(0).getKey());
    }

    @WithAccessId(
        userName = "dummy")
    @Test(expected = NotAuthorizedException.class)
    public void testQueryAllTransferTargetsForUserNotAuthorized()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1")
            .list();
        fail("NotAuthorizedException was expected");
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAllTransferTargetsForUserAndGroup()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .list();
        Assert.assertEquals(6, results.size());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAllTransferTargetsForUserAndGroupSortedByNameAscending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException,
        InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .orderByName(asc)
            .list();
        Assert.assertEquals(6, results.size());
        Assert.assertEquals("GPK_KSC_1", results.get(0).getKey());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAllTransferTargetsForUserAndGroupSortedByNameDescending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException,
        InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .orderByName(desc)
            .orderByKey(asc)
            .list();
        Assert.assertEquals(6, results.size());
        Assert.assertEquals("USER_2_2", results.get(0).getKey());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testQueryAllTransferSourcesForUserAndGroup()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException,
        InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.DISTRIBUTE, "user_1_1", "group_1")
            .list();
        Assert.assertEquals(2, results.size());
        List<String> keys = new ArrayList<>(Arrays.asList("GPK_KSC_1", "USER_1_1"));
        for (WorkbasketSummary wb : results) {
            Assert.assertTrue(keys.contains(wb.getKey()));
        }
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryAllTransferTargetsForUserAndGroupFromSubject()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.APPEND)
            .list();
        Assert.assertEquals(6, results.size());
    }

    @WithAccessId(userName = "user_1_1")
    @Test
    public void testQueryAllAvailableWorkbasketForOpeningForUserAndGroupFromSubject()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.READ)
            .list();
        Assert.assertEquals(1, results.size());
    }

}
