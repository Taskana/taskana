package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
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

  @WithAccessId(user = "user_1_1", groups = "teamlead_1")
  @Test
  void testGetDistributionTargetsSucceedsById()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketSummary workbasketSummary =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC").single();
    List<String> expectedTargetIds =
        Arrays.asList(
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

  @WithAccessId(user = "user_1_1", groups = "teamlead_1")
  @Test
  void testGetDistributionTargetsSucceeds()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketSummary workbasketSummary =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC").single();
    List<String> expectedTargetIds =
        Arrays.asList(
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

  @WithAccessId(
      user = "user_1_1",
      groups = {"teamlead_1", "group_1", "group_2", "businessadmin"})
  @Test
  void testDistributionTargetCallsWithNonExistingWorkbaskets()
      throws NotAuthorizedException, WorkbasketNotFoundException {
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
          workbasketService.setDistributionTargets(
              existingWb, Collections.singletonList(nonExistingWb));
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);

    call = () -> workbasketService.addDistributionTarget(existingWb, nonExistingWb);
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);

    int beforeCount = workbasketService.getDistributionTargets(existingWb).size();
    workbasketService.removeDistributionTarget(existingWb, nonExistingWb);
    int afterCount = workbasketService.getDistributionTargets(existingWb).size();

    assertThat(beforeCount).isEqualTo(afterCount);
  }

  @WithAccessId(user = "user_3_1", groups = "group_1")
  @Test
  void testDistributionTargetCallsFailWithNotAuthorizedException() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String existingWb = "WBI:100000000000000000000000000000000001";

    ThrowingCallable call =
        () -> {
          workbasketService.getDistributionTargets(existingWb);
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);

    call =
        () -> {
          workbasketService.setDistributionTargets(
              existingWb, Collections.singletonList("WBI:100000000000000000000000000000000002"));
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

  @WithAccessId(
      user = "user_2_2",
      groups = {"group_1", "group_2", "businessadmin"})
  @Test
  void testAddAndRemoveDistributionTargets()
      throws NotAuthorizedException, WorkbasketNotFoundException {
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

  @WithAccessId(user = "user_2_2", groups = "businessadmin")
  @Test
  void testAddAndRemoveDistributionTargetsOnWorkbasketWithoutReadPermission()
      throws NotAuthorizedException, WorkbasketNotFoundException {
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

  @WithAccessId(
      user = "user_2_2",
      groups = {"group_1", "group_2"})
  @Test
  void testAddDistributionTargetsFailsNotAuthorized()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");

    List<WorkbasketSummary> distributionTargets =
        workbasketService.getDistributionTargets(workbasket.getId());
    assertThat(distributionTargets).hasSize(4);

    // add a new distribution target
    Workbasket newTarget = workbasketService.getWorkbasket("GPK_B_KSC_2", "DOMAIN_B");

    ThrowingCallable call =
        () -> {
          workbasketService.addDistributionTarget(workbasket.getId(), newTarget.getId());
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      user = "user_2_2",
      groups = {"group_1", "group_2", "businessadmin"})
  @Test
  void testSetDistributionTargets()
      throws NotAuthorizedException, WorkbasketNotFoundException, SQLException, IOException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Workbasket sourceWorkbasket = workbasketService.getWorkbasket("GPK_KSC_1", "DOMAIN_A");

    List<WorkbasketSummary> initialDistributionTargets =
        workbasketService.getDistributionTargets(sourceWorkbasket.getId());
    assertThat(initialDistributionTargets).hasSize(4);

    List<WorkbasketSummary> newDistributionTargets =
        workbasketService.createWorkbasketQuery().keyIn("USER_1_1", "GPK_B_KSC_1").list();
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

  @WithAccessId(
      user = "user_2_2",
      groups = {"group_1", "group_2"})
  @Test
  void testGetDistributionSourcesById() throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    List<WorkbasketSummary> distributionSources =
        workbasketService.getDistributionSources("WBI:100000000000000000000000000000000004");

    assertThat(distributionSources).hasSize(2);
    List<String> expectedIds =
        new ArrayList<>(
            Arrays.asList(
                "WBI:100000000000000000000000000000000001",
                "WBI:100000000000000000000000000000000002"));

    for (WorkbasketSummary foundSummary : distributionSources) {
      assertThat(expectedIds.contains(foundSummary.getId())).isTrue();
    }
  }

  @WithAccessId(
      user = "user_2_2",
      groups = {"group_1", "group_2"})
  @Test
  void testGetDistributionSourcesByKeyDomain()
      throws NotAuthorizedException, WorkbasketNotFoundException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    List<WorkbasketSummary> distributionSources =
        workbasketService.getDistributionSources("TEAMLEAD_1", "DOMAIN_A");

    assertThat(distributionSources).hasSize(2);
    List<String> expectedIds =
        new ArrayList<>(
            Arrays.asList(
                "WBI:100000000000000000000000000000000001",
                "WBI:100000000000000000000000000000000002"));

    for (WorkbasketSummary foundSummary : distributionSources) {
      assertThat(expectedIds.contains(foundSummary.getId())).isTrue();
    }
  }

  @WithAccessId(user = "henry", groups = "undefinedgroup")
  @Test
  void testQueryDistributionSourcesThrowsNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService.getDistributionSources("WBI:100000000000000000000000000000000004");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_UserIsTaskAdminAndNotAuthorizedToGetWorkbasketDistTargets() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable retrieveWorkbasketDistributionTargetsCall =
        () -> {
          List<WorkbasketSummary> ws =
              workbasketService.getDistributionSources("WBI:100000000000000000000000000000000004");
        };
    assertThatThrownBy(retrieveWorkbasketDistributionTargetsCall)
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_UserIsTaskAdminAndNotAuthorizedToSetWorkbasketDistTargets() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService.setDistributionTargets(
              "WBI:100000000000000000000000000000000004",
              Arrays.asList("WBI:100000000000000000000000000000000002"));
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_UserIsTaskAdminAndNotAuthorizedToAddWorkbasketDistTarget() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService.addDistributionTarget(
              "WBI:100000000000000000000000000000000004",
              "WBI:100000000000000000000000000000000002");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ThrowException_When_UserIsTaskAdminAndNotAuthorizedToRemoveWorkbasketDistTarget() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService.removeDistributionTarget(
              "WBI:100000000000000000000000000000000004",
              "WBI:100000000000000000000000000000000002");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      user = "user_2_2",
      groups = {"group_1", "group_2"})
  @Test
  void testQueryDistributionSourcesThrowsWorkbasketNotFound() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService.getDistributionSources("WBI:10dasgibtsdochnicht00000000000000004");
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }
}
