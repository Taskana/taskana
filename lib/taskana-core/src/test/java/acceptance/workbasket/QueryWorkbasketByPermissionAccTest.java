package acceptance.workbasket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query workbasket by permission" scenarios.
 */
@ExtendWith(JAASExtension.class)
class QueryWorkbasketByPermissionAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    QueryWorkbasketByPermissionAccTest() {
        super();
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    void testQueryAllTransferTargetsForUser()
        throws NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1")
            .list();
        assertEquals(1, results.size());
        assertEquals("USER_1_1", results.get(0).getKey());
    }

    @WithAccessId(
        userName = "dummy")
    @Test
    void testQueryAllTransferTargetsForUserNotAuthorized() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Assertions.assertThrows(NotAuthorizedException.class, () ->
            workbasketService.createWorkbasketQuery()
                .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1")
                .list());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    void testQueryAllTransferTargetsForUserAndGroup()
        throws NotAuthorizedException, InvalidArgumentException, SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .list();
        assertEquals(6, results.size());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    void testQueryAllTransferTargetsForUserAndGroupSortedByNameAscending()
        throws NotAuthorizedException, InvalidArgumentException, SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .orderByName(asc)
            .list();
        assertEquals(6, results.size());
        assertEquals("GPK_KSC_1", results.get(0).getKey());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    void testQueryAllTransferTargetsForUserAndGroupSortedByNameDescending()
        throws NotAuthorizedException, InvalidArgumentException, SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .orderByName(desc)
            .orderByKey(asc)
            .list();
        assertEquals(6, results.size());
        assertEquals("USER_2_2", results.get(0).getKey());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    void testQueryAllTransferSourcesForUserAndGroup()
        throws NotAuthorizedException, InvalidArgumentException, SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.DISTRIBUTE, "user_1_1", "group_1")
            .list();
        assertEquals(2, results.size());
        List<String> keys = new ArrayList<>(Arrays.asList("GPK_KSC_1", "USER_1_1"));
        for (WorkbasketSummary wb : results) {
            assertTrue(keys.contains(wb.getKey()));
        }
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testQueryAllTransferTargetsForUserAndGroupFromSubject()
        throws SystemException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.APPEND)
            .list();
        assertEquals(6, results.size());
    }

    @WithAccessId(userName = "user_1_1")
    @Test
    void testQueryAllAvailableWorkbasketForOpeningForUserAndGroupFromSubject() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.READ)
            .list();
        assertEquals(1, results.size());
    }

    @WithAccessId(userName = "teamlead_1", groupNames = {"businessadmin"})
    @Test
    void testConsiderBusinessAdminPermissionsWhileQueryingWorkbaskets() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.OPEN)
            .list();
        assertEquals(3, results.size());
    }

    @WithAccessId(userName = "admin")
    @Test
    void testSkipAuthorizationCheckForAdminWhileQueryingWorkbaskets() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.OPEN)
            .list();
        assertEquals(25, results.size());
    }

}
