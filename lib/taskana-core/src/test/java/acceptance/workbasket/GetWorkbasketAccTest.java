package acceptance.workbasket;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "get workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class GetWorkbasketAccTest extends AbstractAccTest {

    public GetWorkbasketAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testGetWorkbasket()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.getWorkbasket("WBI:100000000000000000000000000000000007");

        Assert.assertEquals("DOMAIN_A", workbasket.getDomain());
        Assert.assertEquals("PPK User 2 KSC 1", workbasket.getDescription());
        Assert.assertEquals("PPK User 2 KSC 1", workbasket.getName());
        Assert.assertEquals("USER_1_2", workbasket.getKey());
        Assert.assertEquals(WorkbasketType.PERSONAL, workbasket.getType());
        Assert.assertEquals("Peter Maier", workbasket.getOwner());
        Assert.assertEquals("Versicherung", workbasket.getOrgLevel1());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testGetWorkbasketPermissions() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketPermission> permissions = workbasketService
            .getPermissionsForWorkbasket("WBI:100000000000000000000000000000000007");

        Assert.assertEquals(4, permissions.size());
        Assert.assertTrue(permissions.contains(WorkbasketPermission.READ));
        Assert.assertTrue(permissions.contains(WorkbasketPermission.OPEN));
        Assert.assertTrue(permissions.contains(WorkbasketPermission.TRANSFER));
        Assert.assertTrue(permissions.contains(WorkbasketPermission.APPEND));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testThrowsExceptionIfIdIsInvalid()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        workbasketService.getWorkbasket("INVALID_ID");
    }

    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfNotAuthorized()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000001");
    }

}
