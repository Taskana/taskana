package acceptance.workbasket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.workbasket.api.Workbasket;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketSummary;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance test for all "get workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class DistributionTargetsAccTest extends AbstractAccTest {

  DistributionTargetsAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"teamlead_1"})
  @Test
  void testGetDistributionTargetsSucceedsById()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketSummary workbasketSummary =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC").single();

    List<WorkbasketSummary> retrievedDistributionTargets =
        workbasketService.getDistributionTargets(workbasketSummary.getId());

    assertEquals(4, retrievedDistributionTargets.size());
    List<String> expectedTargetIds =
        new ArrayList<>(
            Arrays.asList(
                "WBI:100000000000000000000000000000000002",
                "WBI:100000000000000000000000000000000003",
                "WBI:100000000000000000000000000000000004",
                "WBI:100000000000000000000000000000000005"));

    for (WorkbasketSummary wbSummary : retrievedDistributionTargets) {
      assertTrue(expectedTargetIds.contains(wbSummary.getId()));
    }
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"teamlead_1"})
  @Test
  void testGetDistributionTargetsSucceeds()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketSummary workbasketSummary =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC").single();

    List<WorkbasketSummary> retrievedDistributionTargets =
        workbasketService.getDistributionTargets(
            workbasketSummary.getKey(), workbasketSummary.getDomain());

    assertEquals(4, retrievedDistributionTargets.size());
    List<String> expectedTargetIds =
        new ArrayList<>(
            Arrays.asList(
                "WBI:100000000000000000000000000000000002",
                "WBI:100000000000000000000000000000000003",
                "WBI:100000000000000000000000000000000004",
                "WBI:100000000000000000000000000000000005"));

    for (WorkbasketSummary wbSummary : retrievedDistributionTargets) {
      assertTrue(expectedTargetIds.contains(wbSummary.getId()));
    }
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"teamlead_1", "group_1", "group_2", "businessadmin"})
  @Test
  void testDistributionTargetCallsWithNonExistingWorkbaskets()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String existingWb = "WBI:100000000000000000000000000000000001";
    String nonExistingWb = "WBI:100000000000000000000000000000000xx1";

    Assertions.assertThrows(
        WorkbasketNotFoundException.class,
        () -> workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000xx1"));

    Assertions.assertThrows(
        WorkbasketNotFoundException.class,
        () ->
            workbasketService.setDistributionTargets(
                existingWb, new ArrayList<>(Arrays.asList(nonExistingWb))));

    Assertions.assertThrows(
        WorkbasketNotFoundException.class,
        () -> workbasketService.addDistributionTarget(existingWb, nonExistingWb));

    int beforeCount = workbasketService.getDistributionTargets(existingWb).size();
    workbasketService.removeDistributionTarget(existingWb, nonExistingWb);
    int afterCount = workbasketService.getDistributionTargets(existingWb).size();

    assertEquals(afterCount, beforeCount);
  }

  @WithAccessId(
      userName = "user_3_1",
      groupNames = {"group_1"})
  @Test
  void testDistributionTargetCallsFailWithNotAuthorizedException() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String existingWb = "WBI:100000000000000000000000000000000001";

    Assertions.assertThrows(
        NotAuthorizedException.class, () -> workbasketService.getDistributionTargets(existingWb));

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            workbasketService.setDistributionTargets(
                existingWb, Arrays.asList("WBI:100000000000000000000000000000000002")));

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            workbasketService.addDistributionTarget(
                existingWb, "WBI:100000000000000000000000000000000002"));

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            workbasketService.removeDistributionTarget(
                existingWb, "WBI:100000000000000000000000000000000002"));
  }

  @WithAccessId(
      userName = "user_2_2",
      groupNames = {"group_1", "group_2", "businessadmin"})
  @Test
  void testAddAndRemoveDistributionTargets()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasket.getId());
    assertEquals(4, distributionTargets.size());

    // add a new distribution target
    Workbasket newTarget = workbasketService.getWorkbasket("GPK_B_KSC_2", "DOMAIN_B");
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
      groupNames = {"businessadmin"})
  @Test
  void testAddAndRemoveDistributionTargetsOnWorkbasketWithoutReadPermission()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_B_KSC_2", "DOMAIN_B");

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasket.getId());
    assertEquals(0, distributionTargets.size());

    // add a new distribution target
    Workbasket newTarget = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
    workbasketService.addDistributionTarget(workbasket.getId(), newTarget.getId());

    distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
    assertEquals(1, distributionTargets.size());

    // remove the new target
    workbasketService.removeDistributionTarget(workbasket.getId(), newTarget.getId());
    distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
    assertEquals(0, distributionTargets.size());
  }

  @WithAccessId(
      userName = "user_2_2",
      groupNames = {"group_1", "group_2"})
  @Test
  void testAddDistributionTargetsFailsNotAuthorized()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasket.getId());
    assertEquals(4, distributionTargets.size());

    // add a new distribution target
    Workbasket newTarget = workbasketService.getWorkbasket("GPK_B_KSC_2", "DOMAIN_B");

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> workbasketService.addDistributionTarget(workbasket.getId(), newTarget.getId()));
  }

  @WithAccessId(
      userName = "user_2_2",
      groupNames = {"group_1", "group_2", "businessadmin"})
  @Test
  void testSetDistributionTargets()
      throws NotAuthorizedException, WorkbasketNotFoundException, SQLException, IOException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket sourceWorkbasket = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");

    List<WorkbasketSummary> initialDistributionTargets =
        workbasketService.getDistributionTargets(sourceWorkbasket.getId());
    assertEquals(4, initialDistributionTargets.size());

    List<WorkbasketSummary> newDistributionTargets =
        workbasketService.createWorkbasketQuery().keyIn("USER_1_1", "GPK_B_KSC_1").list();
    assertEquals(2, newDistributionTargets.size());

    List<String> newDistributionTargetIds =
        newDistributionTargets.stream().map(WorkbasketSummary::getId).collect(Collectors.toList());

    workbasketService.setDistributionTargets(sourceWorkbasket.getId(), newDistributionTargetIds);
    List<WorkbasketSummary> changedTargets =
        workbasketService.getDistributionTargets(sourceWorkbasket.getId());
    assertEquals(2, changedTargets.size());

    // reset DB to original state
    resetDb(false);
  }

  @WithAccessId(
      userName = "user_2_2",
      groupNames = {"group_1", "group_2"})
  @Test
  void testGetDistributionSourcesById() throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    List<WorkbasketSummary> distributionSources =
        workbasketService.getDistributionSources("WBI:100000000000000000000000000000000004");

    assertEquals(2, distributionSources.size());
    List<String> expectedIds =
        new ArrayList<>(
            Arrays.asList(
                "WBI:100000000000000000000000000000000001",
                "WBI:100000000000000000000000000000000002"));

    for (WorkbasketSummary foundSummary : distributionSources) {
      assertTrue(expectedIds.contains(foundSummary.getId()));
    }
  }

  @WithAccessId(
      userName = "user_2_2",
      groupNames = {"group_1", "group_2"})
  @Test
  void testGetDistributionSourcesByKeyDomain()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    List<WorkbasketSummary> distributionSources =
        workbasketService.getDistributionSources("TEAMLEAD_1", "DOMAIN_A");

    assertEquals(2, distributionSources.size());
    List<String> expectedIds =
        new ArrayList<>(
            Arrays.asList(
                "WBI:100000000000000000000000000000000001",
                "WBI:100000000000000000000000000000000002"));

    for (WorkbasketSummary foundSummary : distributionSources) {
      assertTrue(expectedIds.contains(foundSummary.getId()));
    }
  }

  @WithAccessId(
      userName = "henry",
      groupNames = {"undefinedgroup"})
  @Test
  void testQueryDistributionSourcesThrowsNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> workbasketService.getDistributionSources("WBI:100000000000000000000000000000000004"));
  }

  @WithAccessId(
      userName = "user_2_2",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryDistributionSourcesThrowsWorkbasketNotFound() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Assertions.assertThrows(
        WorkbasketNotFoundException.class,
        () -> workbasketService.getDistributionSources("WBI:10dasgibtsdochnicht00000000000000004"));
  }
}
