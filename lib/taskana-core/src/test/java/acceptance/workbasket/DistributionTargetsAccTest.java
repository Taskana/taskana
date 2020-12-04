package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import helper.AbstractAccTest;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "get workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class DistributionTargetsAccTest extends AbstractAccTest {

  DistributionTargetsAccTest() {
    super();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testGetDistributionTargetsSucceedsById() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketSummary workbasketSummary =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC").single();
    List<String> expectedTargetIds =
        List.of(
            "WBI:100000000000000000000000000000000002",
            "WBI:100000000000000000000000000000000003",
            "WBI:100000000000000000000000000000000004",
            "WBI:100000000000000000000000000000000005");

    List<WorkbasketSummary> retrievedDistributionTargets =
        workbasketService.getDistributionTargets(workbasketSummary.getId());

    assertThat(retrievedDistributionTargets)
        .extracting(WorkbasketSummary::getId)
        .containsExactlyInAnyOrderElementsOf(expectedTargetIds);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testGetDistributionTargetsSucceeds() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketSummary workbasketSummary =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC").single();
    List<String> expectedTargetIds =
        List.of(
            "WBI:100000000000000000000000000000000002",
            "WBI:100000000000000000000000000000000003",
            "WBI:100000000000000000000000000000000004",
            "WBI:100000000000000000000000000000000005");

    List<WorkbasketSummary> retrievedDistributionTargets =
        workbasketService.getDistributionTargets(
            workbasketSummary.getKey(), workbasketSummary.getDomain());

    assertThat(retrievedDistributionTargets)
        .extracting(WorkbasketSummary::getId)
        .containsExactlyInAnyOrderElementsOf(expectedTargetIds);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testDistributionTargetCallsWithNonExistingWorkbaskets() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String existingWb = "WBI:100000000000000000000000000000000001";
    String nonExistingWb = "WBI:100000000000000000000000000000000xx1";

    ThrowingCallable call =
        () -> {
          workbasketService.getDistributionTargets("WBI:100000000000000000000000000000000xx1");
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);

    call =
        () -> {
          workbasketService.setDistributionTargets(existingWb, List.of(nonExistingWb));
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);

    call = () -> workbasketService.addDistributionTarget(existingWb, nonExistingWb);
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);

    int beforeCount = workbasketService.getDistributionTargets(existingWb).size();
    workbasketService.removeDistributionTarget(existingWb, nonExistingWb);
    int afterCount = workbasketService.getDistributionTargets(existingWb).size();

    assertThat(beforeCount).isEqualTo(afterCount);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ReturnDistributionTargets_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String existingWb = "WBI:100000000000000000000000000000000001";

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(existingWb);
    assertThat(distributionTargets).hasSize(4);
  }

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdminAndMakesDistTargetCalls() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String existingWb = "WBI:100000000000000000000000000000000001";

    ThrowingCallable call =
        () -> {
          workbasketService.setDistributionTargets(
              existingWb, List.of("WBI:100000000000000000000000000000000002"));
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);

    call =
        () -> {
          workbasketService.addDistributionTarget(
              existingWb, "WBI:100000000000000000000000000000000002");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);

    call =
        () -> {
          workbasketService.removeDistributionTarget(
              existingWb, "WBI:100000000000000000000000000000000002");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserTriesToGetDistributionTargetsAndRoleIsNotAdministrative() {

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String existingWb = "WBI:100000000000000000000000000000000001";

    ThrowingCallable getDistributionTargetsCall =
        () -> {
          workbasketService.getDistributionTargets(existingWb);
        };
    assertThatThrownBy(getDistributionTargetsCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testAddAndRemoveDistributionTargets() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).hasSize(4);

    // add a new distribution target
    Workbasket newTarget = workbasketService.getWorkbasket("GPK_B_KSC_2", "DOMAIN_B");
    workbasketService.addDistributionTarget(workbasket.getId(), newTarget.getId());

    distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).hasSize(5);

    // remove the new target
    workbasketService.removeDistributionTarget(workbasket.getId(), newTarget.getId());
    distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).hasSize(4);

    // remove the new target again Question: should this throw an exception?
    workbasketService.removeDistributionTarget(workbasket.getId(), newTarget.getId());
    distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).hasSize(4);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testAddAndRemoveDistributionTargetsOnWorkbasketWithoutReadPermission() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_B_KSC_2", "DOMAIN_B");

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).isEmpty();

    // add a new distribution target
    Workbasket newTarget = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");
    workbasketService.addDistributionTarget(workbasket.getId(), newTarget.getId());

    distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).hasSize(1);

    // remove the new target
    workbasketService.removeDistributionTarget(workbasket.getId(), newTarget.getId());
    distributionTargets = workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).isEmpty();
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void testAddDistributionTargetsFailsNotAuthorized() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).hasSize(4);

    // add a new distribution target
    Workbasket newTarget = workbasketService.getWorkbasket("USER-1-2", "DOMAIN_A");

    ThrowingCallable call =
        () -> {
          workbasketService.addDistributionTarget(workbasket.getId(), newTarget.getId());
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testSetDistributionTargets() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket sourceWorkbasket = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");

    List<WorkbasketSummary> initialDistributionTargets =
        workbasketService.getDistributionTargets(sourceWorkbasket.getId());
    assertThat(initialDistributionTargets).hasSize(4);

    List<WorkbasketSummary> newDistributionTargets =
        workbasketService.createWorkbasketQuery().keyIn("USER-1-1", "GPK_B_KSC_1").list();
    assertThat(newDistributionTargets).hasSize(2);

    List<String> newDistributionTargetIds =
        newDistributionTargets.stream().map(WorkbasketSummary::getId).collect(Collectors.toList());

    workbasketService.setDistributionTargets(sourceWorkbasket.getId(), newDistributionTargetIds);
    List<WorkbasketSummary> changedTargets =
        workbasketService.getDistributionTargets(sourceWorkbasket.getId());
    assertThat(changedTargets).hasSize(2);

    // reset DB to original state
    resetDb(false);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testGetDistributionSourcesById() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    List<WorkbasketSummary> distributionSources =
        workbasketService.getDistributionSources("WBI:100000000000000000000000000000000004");

    assertThat(distributionSources)
        .extracting(WorkbasketSummary::getId)
        .containsExactlyInAnyOrder(
            "WBI:100000000000000000000000000000000001", "WBI:100000000000000000000000000000000002");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void testGetDistributionSourcesByKeyDomain() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    List<WorkbasketSummary> distributionSources =
        workbasketService.getDistributionSources("TEAMLEAD-1", "DOMAIN_A");

    assertThat(distributionSources)
        .extracting(WorkbasketSummary::getId)
        .containsExactlyInAnyOrder(
            "WBI:100000000000000000000000000000000001", "WBI:100000000000000000000000000000000002");
  }

  @WithAccessId(user = "unknownuser")
  @Test
  void testQueryDistributionSourcesThrowsNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> workbasketService.getDistributionSources("WBI:100000000000000000000000000000000004");
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-2-2")
  @Test
  void testQueryDistributionSourcesThrowsWorkbasketNotFound() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> workbasketService.getDistributionSources("WBI:10dasgibtsdochnicht00000000000000004");
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }
}
