package acceptance.workbasket;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.WorkbasketType;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "get workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class CreateWorkbasketAccTest extends AbstractAccTest {

    public CreateWorkbasketAccTest() {
        super();
    }

    @Test
    public void testCreateWorkbasket()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
        InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        int before = workbasketService.getWorkbaskets().size();
        Workbasket workbasket = workbasketService.newWorkbasket("key");
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workbasket.setOrgLevel1("company");
        workbasketService.createWorkbasket(workbasket);
        Assert.assertEquals(before + 1, workbasketService.getWorkbaskets().size());
    }

    @Test
    public void testCreateWorkbasketWithMissingRequiredField()
        throws WorkbasketNotFoundException, NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket workbasket = workbasketService.newWorkbasket(null);
        workbasket.setName("Megabasket");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workbasket.setOrgLevel1("company");
        try { // missing key
            workbasketService.createWorkbasket(workbasket);
            fail("InvalidWorkbasketException was expected");
        } catch (InvalidWorkbasketException e) {
        }

        workbasket = workbasketService.newWorkbasket("key");
        workbasket.setType(WorkbasketType.GROUP);
        workbasket.setDomain("novatec");
        workbasket.setOrgLevel1("company");
        try {  // missing name
            workbasketService.createWorkbasket(workbasket);
            fail("InvalidWorkbasketException was expected");
        } catch (InvalidWorkbasketException e) {
        }

        workbasket = workbasketService.newWorkbasket("key");
        workbasket.setName("Megabasket");
        workbasket.setDomain("novatec");
        workbasket.setOrgLevel1("company");
        try {  // missing type
            workbasketService.createWorkbasket(workbasket);
            fail("InvalidWorkbasketException was expected");
        } catch (InvalidWorkbasketException e) {
        }

        workbasket = workbasketService.newWorkbasket("key");
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
