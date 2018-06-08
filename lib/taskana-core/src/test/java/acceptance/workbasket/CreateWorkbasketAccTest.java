package acceptance.workbasket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "create workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class CreateWorkbasketAccTest extends AbstractAccTest {

    public CreateWorkbasketAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"businessadmin"})
    @Test
    public void testCreateWorkbasket()
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException, WorkbasketAlreadyExistException, DomainNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

        Workbasket workbasket = workbasketService.newWorkbasket("NT1234", "DOMAIN_A");
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setOrgLevel1("company");
        workbasket = workbasketService.createWorkbasket(workbasket);
        WorkbasketAccessItem wbai = workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user_1_2");
        wbai.setPermRead(true);
        workbasketService.createWorkbasketAccessItem(wbai);

        int after = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();
        assertEquals(before + 1, after);
        Workbasket createdWorkbasket = workbasketService.getWorkbasket("NT1234", "DOMAIN_A");
        assertNotNull(createdWorkbasket);
        assertNotNull(createdWorkbasket.getId());
        assertEquals(workbasket, createdWorkbasket);
        Workbasket createdWorkbasket2 = workbasketService.getWorkbasket(createdWorkbasket.getId());
        assertNotNull(createdWorkbasket);
        assertEquals(createdWorkbasket, createdWorkbasket2);
    }

    @WithAccessId(
        userName = "dummy")
    @Test(expected = NotAuthorizedException.class)
    public void testCreateWorkbasketNotAuthorized()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketAlreadyExistException,
        DomainNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.newWorkbasket("key3", "DOMAIN_A");
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setOrgLevel1("company");
        workbasketService.createWorkbasket(workbasket);

        fail("NotAuthorizedException should have been thrown");
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"businessadmin"})
    @Test(expected = DomainNotFoundException.class)
    public void testCreateWorkbasketWithInvalidDomain()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketAlreadyExistException,
        DomainNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.newWorkbasket("key3", "UNKNOWN_DOMAIN");
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setOrgLevel1("company");
        workbasketService.createWorkbasket(workbasket);

        fail("DomainNotFoundException should have been thrown");
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testCreateWorkbasketWithMissingRequiredField()
        throws NotAuthorizedException, WorkbasketAlreadyExistException,
        DomainNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.newWorkbasket(null, "novatec");
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setOrgLevel1("company");
        try { // missing key
            workbasketService.createWorkbasket(workbasket);
            fail("InvalidWorkbasketException was expected");
        } catch (InvalidWorkbasketException e) {
        }

        workbasket = workbasketService.newWorkbasket("key", "novatec");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setOrgLevel1("company");
        try {  // missing name
            workbasketService.createWorkbasket(workbasket);
            fail("InvalidWorkbasketException was expected");
        } catch (InvalidWorkbasketException e) {
        }

        workbasket = workbasketService.newWorkbasket("key", "novatec");
        workbasket.setName("Megabasket");
        workbasket.setOrgLevel1("company");
        try {  // missing type
            workbasketService.createWorkbasket(workbasket);
            fail("InvalidWorkbasketException was expected");
        } catch (InvalidWorkbasketException e) {
        }

        workbasket = workbasketService.newWorkbasket("key", null);
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setOrgLevel1("company");
        try {  // missing domain
            workbasketService.createWorkbasket(workbasket);
            fail("InvalidWorkbasketException was expected");
        } catch (InvalidWorkbasketException e) {
        }
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"businessadmin"})
    @Test(expected = WorkbasketAlreadyExistException.class)
    public void testThrowsExceptionIfWorkbasketWithCaseInsensitiveSameKeyDomainIsCreated()
        throws NotAuthorizedException, InvalidWorkbasketException, WorkbasketAlreadyExistException,
        DomainNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.newWorkbasket("X123456", "DOMAIN_A");
        workbasket.setName("Personal Workbasket for UID X123456");
        workbasket.setType(WorkbasketType.PERSONAL);
        workbasket = workbasketService.createWorkbasket(workbasket);

        Workbasket duplicateWorkbasketWithSmallX = workbasketService.newWorkbasket("x123456", "DOMAIN_A");
        duplicateWorkbasketWithSmallX.setName("Personal Workbasket for UID X123456");
        duplicateWorkbasketWithSmallX.setType(WorkbasketType.PERSONAL);
        duplicateWorkbasketWithSmallX = workbasketService.createWorkbasket(duplicateWorkbasketWithSmallX);
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"businessadmin"})
    @Test
    public void testWorkbasketAccessItemSetName()
        throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException, WorkbasketAlreadyExistException, DomainNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

        Workbasket workbasket = workbasketService.newWorkbasket("WBAIT1234", "DOMAIN_A");
        workbasket.setName("MyNewBasket");
        workbasket.setType(WorkbasketType.PERSONAL);
        workbasket.setOrgLevel1("company");
        workbasket = workbasketService.createWorkbasket(workbasket);
        WorkbasketAccessItem wbai = workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user_1_2");
        wbai.setPermRead(true);
        wbai.setAccessName("Karl Napf");
        workbasketService.createWorkbasketAccessItem(wbai);

        Workbasket createdWorkbasket = workbasketService.getWorkbasket("WBAIT1234", "DOMAIN_A");
        assertNotNull(createdWorkbasket);
        assertNotNull(createdWorkbasket.getId());

        List<WorkbasketAccessItem> accessItems = workbasketService.getWorkbasketAccessItems(createdWorkbasket.getId());
        WorkbasketAccessItem item = accessItems.stream().filter(t -> wbai.getId().equals(t.getId())).findFirst().orElse(
            null);
        assertEquals("Karl Napf", item.getAccessName());

    }

}
