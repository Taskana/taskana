package acceptance.workbasket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "get workbasket" scenarios.
 */
@ExtendWith(JAASExtension.class)
class GetWorkbasketAccTest extends AbstractAccTest {

    GetWorkbasketAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testGetWorkbasketById()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.getWorkbasket("WBI:100000000000000000000000000000000007");

        assertEquals("DOMAIN_A", workbasket.getDomain());
        assertEquals("PPK User 2 KSC 1", workbasket.getDescription());
        assertEquals("PPK User 2 KSC 1", workbasket.getName());
        assertEquals("USER_1_2", workbasket.getKey());
        assertEquals(WorkbasketType.PERSONAL, workbasket.getType());
        assertEquals("Peter Maier", workbasket.getOwner());
        assertEquals("versicherung", workbasket.getOrgLevel1());
        assertEquals("abteilung", workbasket.getOrgLevel2());
        assertEquals("projekt", workbasket.getOrgLevel3());
        assertEquals("team", workbasket.getOrgLevel4());
        assertEquals("custom1", workbasket.getCustom1());
        assertEquals("custom2", workbasket.getCustom2());
        assertEquals("custom3", workbasket.getCustom3());
        assertEquals("custom4", workbasket.getCustom4());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testGetWorkbasketByKeyAndDomain()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.getWorkbasket("USER_1_2", "DOMAIN_A");

        assertEquals("WBI:100000000000000000000000000000000007", workbasket.getId());
        assertEquals("PPK User 2 KSC 1", workbasket.getDescription());
        assertEquals("PPK User 2 KSC 1", workbasket.getName());
        assertEquals(WorkbasketType.PERSONAL, workbasket.getType());
        assertEquals("Peter Maier", workbasket.getOwner());
        assertEquals("versicherung", workbasket.getOrgLevel1());
        assertEquals("abteilung", workbasket.getOrgLevel2());
        assertEquals("projekt", workbasket.getOrgLevel3());
        assertEquals("team", workbasket.getOrgLevel4());
        assertEquals("custom1", workbasket.getCustom1());
        assertEquals("custom2", workbasket.getCustom2());
        assertEquals("custom3", workbasket.getCustom3());
        assertEquals("custom4", workbasket.getCustom4());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testGetWorkbasketPermissions() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketPermission> permissions = workbasketService
            .getPermissionsForWorkbasket("WBI:100000000000000000000000000000000007");

        assertEquals(4, permissions.size());
        assertTrue(permissions.contains(WorkbasketPermission.READ));
        assertTrue(permissions.contains(WorkbasketPermission.OPEN));
        assertTrue(permissions.contains(WorkbasketPermission.TRANSFER));
        assertTrue(permissions.contains(WorkbasketPermission.APPEND));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testGetWorkbasketPermissionsForInvalidWorkbasketId() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketPermission> permissions = workbasketService
            .getPermissionsForWorkbasket("WBI:invalid");

        assertEquals(0, permissions.size());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testGetWorkbasketAsSummary()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        WorkbasketSummary workbasketSummary = workbasketService
            .getWorkbasket("WBI:100000000000000000000000000000000007").asSummary();

        assertEquals("DOMAIN_A", workbasketSummary.getDomain());
        assertEquals("PPK User 2 KSC 1", workbasketSummary.getDescription());
        assertEquals("PPK User 2 KSC 1", workbasketSummary.getName());
        assertEquals("USER_1_2", workbasketSummary.getKey());
        assertEquals(WorkbasketType.PERSONAL, workbasketSummary.getType());
        assertEquals("Peter Maier", workbasketSummary.getOwner());
        assertEquals("versicherung", workbasketSummary.getOrgLevel1());
        assertEquals("abteilung", workbasketSummary.getOrgLevel2());
        assertEquals("projekt", workbasketSummary.getOrgLevel3());
        assertEquals("team", workbasketSummary.getOrgLevel4());
        assertEquals("custom1", workbasketSummary.getCustom1());
        assertEquals("custom2", workbasketSummary.getCustom2());
        assertEquals("custom3", workbasketSummary.getCustom3());
        assertEquals("custom4", workbasketSummary.getCustom4());
        assertEquals(false, workbasketSummary.isMarkedForDeletion());
    }

    @Test
    void testThrowsExceptionIfIdIsInvalid() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Assertions.assertThrows(WorkbasketNotFoundException.class, () ->
            workbasketService.getWorkbasket("INVALID_ID"));
    }

    @Test
    void testThrowsExceptionIfKeyOrDomainIsInvalid() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Assertions.assertThrows(WorkbasketNotFoundException.class, () ->
            workbasketService.getWorkbasket("INVALID_KEY", "INVALID_DOMAIN"));
    }

    @Test
    void testGetByIdNotAuthorized() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Assertions.assertThrows(NotAuthorizedException.class, () ->
            workbasketService.getWorkbasket("WBI:100000000000000000000000000000000001"));
    }

    @Test
    void testGetByKeyDomainNotAuthorized() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Assertions.assertThrows(NotAuthorizedException.class, () ->
            workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A"));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    void testGetWorkbasketByIdNotExisting() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Assertions.assertThrows(WorkbasketNotFoundException.class, () ->
            workbasketService.getWorkbasket("NOT EXISTING ID"));
    }

}
