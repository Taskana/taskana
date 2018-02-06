package acceptance.workbasket;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test which does test the deletion of a workbasket and all wanted failures.
 */
@RunWith(JAASRunner.class)
public class DeleteWorkbasketAccTest extends AbstractAccTest {

    private WorkbasketService workbasketService;

    public DeleteWorkbasketAccTest() {
        super();
    }

    @Before
    public void setUpMethod() {
        workbasketService = taskanaEngine.getWorkbasketService();
    }

    @WithAccessId(userName = "teamlead_2")
    @Test
    public void testDeleteWorkbasket()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException, InvalidArgumentException {
        Workbasket wb = workbasketService.getWorkbasketByKey("TEAMLEAD_2");
        workbasketService.deleteWorkbasket(wb.getId());

        try {
            workbasketService.getWorkbasketByKey("TEAMLEAD_2");
            fail("There should be no result for a deleted Workbasket.");
        } catch (WorkbasketNotFoundException e) {
            // Workbasket is deleted
        }
    }

    @WithAccessId(userName = "user_1_1", groupNames = {"teamlead_1", "group_1"})
    @Test
    public void testDeleteWorkbasketAlsoAsDistributionTarget()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException, InvalidArgumentException {
        Workbasket wb = workbasketService.getWorkbasketByKey("GPK_KSC_1");
        int distTargets = workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000001")
            .size();

        // WB deleted
        workbasketService.deleteWorkbasket(wb.getId());
        try {
            workbasketService.getWorkbasketByKey("GPK_KSC_1");
            fail("There should be no result for a deleted Workbasket.");
        } catch (WorkbasketNotFoundException e) {
            // Workbasket is deleted
        }

        int newDistTargets = workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000001")
            .size();
        assertThat(newDistTargets, equalTo(3));
        assertTrue(newDistTargets < distTargets);
    }

    @Test
    public void testDeleteWorkbasketWithNullOrEmptyParam()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException {
        // Test Null-Value
        try {
            workbasketService.deleteWorkbasket(null);
            fail("delete() should have thrown an InvalidArgumentException, when the param ID is null.");
        } catch (InvalidArgumentException e) {
            // Nothing to do here
        }

        // Test EMPTY-Value
        try {
            workbasketService.deleteWorkbasket("");
            fail("delete() should have thrown an InvalidArgumentException, when the param ID is EMPTY-String.");
        } catch (InvalidArgumentException e) {
            // Nothing to do here
        }
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testDeleteWorkbasketButNotExisting()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException, InvalidArgumentException {
        workbasketService.deleteWorkbasket("SOME NOT EXISTING ID");
    }

    @WithAccessId(userName = "user_1_1")
    @Test(expected = WorkbasketInUseException.class)
    public void testDeleteWorkbasketWhichIsUsed()
        throws WorkbasketNotFoundException, NotAuthorizedException, WorkbasketInUseException, InvalidArgumentException {
        Workbasket wb = workbasketService.getWorkbasketByKey("USER_1_1");   // all rights, DOMAIN_A with Tasks
        workbasketService.deleteWorkbasket(wb.getId());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
