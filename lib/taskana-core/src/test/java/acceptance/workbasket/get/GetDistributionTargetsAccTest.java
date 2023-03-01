/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package acceptance.workbasket.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "get workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class GetDistributionTargetsAccTest extends AbstractAccTest {

  GetDistributionTargetsAccTest() {
    super();
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ReturnDistributionTargets_When_QueriedById() throws Exception {
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
  void should_ReturnDistributionTargets_When_QueriedByKeyDomain() throws Exception {
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
  void should_ThrowException_When_QueriedWithInvalidWorkbasket() throws Exception {
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
          workbasketService.getDistributionTargets("NOT_EXUTSING", "NOT_EXUTSING");
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
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
    assertThatThrownBy(call).isInstanceOf(MismatchedRoleException.class);
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
    assertThatThrownBy(getDistributionTargetsCall)
        .isInstanceOf(MismatchedWorkbasketPermissionException.class);
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
  void testGetDistributionSourcesThrowsNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> workbasketService.getDistributionSources("WBI:100000000000000000000000000000000004");
    assertThatThrownBy(call).isInstanceOf(MismatchedWorkbasketPermissionException.class);
  }

  @WithAccessId(user = "user-2-2")
  @Test
  void testGetDistributionSourcesThrowsWorkbasketNotFound() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> workbasketService.getDistributionSources("WBI:10dasgibtsdochnicht00000000000000004");
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }
}
