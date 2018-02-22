package acceptance.workbasket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.WorkbasketType;
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
        groupNames = {"group_1"})
    @Test
    public void testCreateWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        int before = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();

        Workbasket workbasket = workbasketService.newWorkbasket("NT1234", "DOMAIN_A");
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setOrgLevel1("company");
        workbasket = workbasketService.createWorkbasket(workbasket);
        WorkbasketAccessItem wbai = workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user_1_2");
        wbai.setPermRead(true);
        workbasketService.createWorkbasketAuthorization(wbai);

        int after = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list().size();
        assertEquals(before + 1, after);
        Workbasket createdWorkbasket = workbasketService.getWorkbasket("NT1234", "DOMAIN_A");
        assertNotNull(createdWorkbasket);
        assertNotNull(createdWorkbasket.getId());
        Workbasket createdWorkbasket2 = workbasketService.getWorkbasket(createdWorkbasket.getId());
        assertNotNull(createdWorkbasket);
        assertEquals(createdWorkbasket, createdWorkbasket2);
    }

    @Test
    public void testCreateWorkbasketWithMissingRequiredField()
        throws WorkbasketNotFoundException, NotAuthorizedException {
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
}
