package acceptance.workbasket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "get workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class DistributionTargetsAccTest extends AbstractAccTest {

    public DistributionTargetsAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"teamlead_1"})
    @Test
    public void testGetDistributionTargetsSucceeds() throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        WorkbasketSummary workbasketSummary = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC")
            .single();

        List<WorkbasketSummary> retrievedDistributionTargets = workbasketService
            .getDistributionTargets(workbasketSummary.getId());

        assertEquals(4, retrievedDistributionTargets.size());
        List<String> expectedTargetIds = new ArrayList<>(
            Arrays.asList("WBI:100000000000000000000000000000000002", "WBI:100000000000000000000000000000000003",
                "WBI:100000000000000000000000000000000004", "WBI:100000000000000000000000000000000005"));

        for (WorkbasketSummary wbSummary : retrievedDistributionTargets) {
            assertTrue(expectedTargetIds.contains(wbSummary.getId()));
        }

    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"teamlead_1", "group_1", "group_2"})
    @Test
    public void testDistributionTargetCallsWithNonExistingWorkbaskets()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        String existingWb = "WBI:100000000000000000000000000000000001";
        String nonExistingWb = "WBI:100000000000000000000000000000000xx1";

        try {
            workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000xx1");
            assertTrue("This line of code should not be reached", false);
        } catch (WorkbasketNotFoundException ex) {
            // nothing to do
        }

        try {
            List<String> distributionTargets = new ArrayList<>(
                Arrays.asList(nonExistingWb));
            workbasketService.setDistributionTargets(existingWb, distributionTargets);
            assertTrue("This line of code should not be reached", false);
        } catch (WorkbasketNotFoundException ex) {
            // nothing to do
        }

        try {
            workbasketService.addDistributionTarget(existingWb, nonExistingWb);
            assertTrue("This line of code should not be reached", false);
        } catch (WorkbasketNotFoundException ex) {
            // nothing to do
        }

        int beforeCount = workbasketService.getDistributionTargets(existingWb).size();
        workbasketService.removeDistributionTarget(existingWb, nonExistingWb);
        int afterCount = workbasketService.getDistributionTargets(existingWb).size();

        assertEquals(afterCount, beforeCount);

    }

    @WithAccessId(
        userName = "user_3_1")
    @Test
    public void testDistributionTargetCallsFailWithNotAuthorizedException()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        String existingWb = "WBI:100000000000000000000000000000000001";

        try {
            workbasketService.getDistributionTargets(existingWb);
            assertTrue("This line of code should not be reached", false);
        } catch (NotAuthorizedException ex) {
            // nothing to do
        }

        try {
            List<String> distributionTargets = new ArrayList<>(
                Arrays.asList("WBI:100000000000000000000000000000000002"));
            workbasketService.setDistributionTargets(existingWb, distributionTargets);
            assertTrue("This line of code should not be reached", false);
        } catch (NotAuthorizedException ex) {
            // nothing to do
        }

        try {
            workbasketService.addDistributionTarget(existingWb,
                "WBI:100000000000000000000000000000000002");
            assertTrue("This line of code should not be reached", false);
        } catch (NotAuthorizedException ex) {
            // nothing to do
        }

        try {
            workbasketService.removeDistributionTarget(existingWb,
                "WBI:100000000000000000000000000000000002");
            assertTrue("This line of code should not be reached", false);
        } catch (NotAuthorizedException ex) {
            // nothing to do
        }

    }

    @WithAccessId(
        userName = "user_2_2",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testAddAndRemoveDistributionTargets()
        throws NotAuthorizedException, WorkbasketNotFoundException, InvalidWorkbasketException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Workbasket workbasket = workbasketService.getWorkbasketByKey("GPK_KSC_1");

        List<WorkbasketSummary> distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
        assertEquals(4, distributionTargets.size());

        // add a new distribution target
        Workbasket newTarget = workbasketService.getWorkbasketByKey("GPK_B_KSC_2");
        workbasketService.addDistributionTarget(workbasket.getId(), newTarget.getId());

        distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
        assertEquals(5, distributionTargets.size());

        // remove the new target
        workbasketService.removeDistributionTarget(workbasket.getId(), newTarget.getId());
        distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
        assertEquals(4, distributionTargets.size());

        // remove the new target again Question: should this throw an exception?
        workbasketService.removeDistributionTarget(workbasket.getId(), newTarget.getId());
        distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
        assertEquals(4, distributionTargets.size());

    }

    @WithAccessId(
        userName = "user_2_2",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testSetDistributionTargets()
        throws NotAuthorizedException, WorkbasketNotFoundException, InvalidWorkbasketException, SQLException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        Workbasket sourceWorkbasket = workbasketService.getWorkbasketByKey("GPK_KSC_1");

        List<WorkbasketSummary> initialDistributionTargets = workbasketService
            .getDistributionTargets(sourceWorkbasket.getId());
        assertEquals(4, initialDistributionTargets.size());

        List<WorkbasketSummary> newDistributionTargets = workbasketService.createWorkbasketQuery()
            .keyIn("USER_1_1", "GPK_B_KSC_1")
            .list();
        assertEquals(2, newDistributionTargets.size());

        List<String> newDistributionTargetIds = newDistributionTargets.stream().map(t -> t.getId()).collect(
            Collectors.toList());

        workbasketService.setDistributionTargets(sourceWorkbasket.getId(), newDistributionTargetIds);
        List<WorkbasketSummary> changedTargets = workbasketService.getDistributionTargets(sourceWorkbasket.getId());
        assertEquals(2, changedTargets.size());

        // reset DB to original state
        resetDb(false);

    }

    @Ignore
    @WithAccessId(
        userName = "user_2_2",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryWorkbasketsWhereGivenWorkbasketIsDistributionTarget()
        throws NotAuthorizedException, WorkbasketNotFoundException, InvalidWorkbasketException, SQLException {
        // WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        //
        // List<WorkbasketSummary> distributionSources = workbasketService
        // .getDistributionSources("WBI:100000000000000000000000000000000004");
        //
        // assertEquals(2, distributionSources.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

}
