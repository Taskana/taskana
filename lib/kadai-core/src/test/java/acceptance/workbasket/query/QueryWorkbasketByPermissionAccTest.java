package acceptance.workbasket.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "query workbasket by permission" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryWorkbasketByPermissionAccTest extends AbstractAccTest {

  private static final WorkbasketService WORKBASKET_SERVICE = kadaiEngine.getWorkbasketService();

  // region accessIdsHavePermission

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllTransferTargetsForUser_When_QueryingForSinglePermission() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(List.of(WorkbasketPermission.APPEND), "user-1-1")
            .list();

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getKey()).isEqualTo("USER-1-1");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllTransferTargetsForUser_When_QueryingForMultiplePermissions() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.APPEND, WorkbasketPermission.DISTRIBUTE), "teamlead-1")
            .list();

    assertThat(results)
        .extracting(WorkbasketSummary::getKey)
        .hasSize(2)
        .containsExactlyInAnyOrder("GPK_KSC", "TEAMLEAD-1");
  }

  @WithAccessId(user = "unknownuser")
  @Test
  void should_ThrowNotAuthorizedException_When_QueryingWithUnknownUser() {

    ThrowingCallable call =
        () ->
            WORKBASKET_SERVICE
                .createWorkbasketQuery()
                .accessIdsHavePermissions(List.of(WorkbasketPermission.APPEND), "user-1-1")
                .list();

    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllTransferTargetsForUserGroupPermission_When_QueryingForSinglePermission()
      throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.APPEND), "user-1-1", GROUP_1_DN, PERM_1)
            .list();

    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllTransferTargetsForUserGroupPermission_When_QueryingForMultiplePermissions()
      throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.APPEND, WorkbasketPermission.OPEN),
                "user-1-1",
                GROUP_1_DN,
                PERM_1)
            .list();

    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllWorkbasketsForUserGroupPermission_When_QueryingForReadTasksPermissions()
      throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.READTASKS), "user-1-1", GROUP_1_DN, PERM_1)
            .list();

    assertThat(results).hasSize(8);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllWorkbasketsForUserGroupPermission_When_QueryingForEditTasksPermissions()
      throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.EDITTASKS), "user-1-1", GROUP_1_DN, PERM_1)
            .list();

    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllTransferTargetsForUserGroupPermission_When_QueryingForSortedByNameAscending()
      throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.APPEND), "user-1-1", GROUP_1_DN, PERM_1)
            .orderByName(SortDirection.ASCENDING)
            .list();

    assertThat(results).hasSize(7);
    assertThat(results.get(0).getKey()).isEqualTo("GPK_KSC_1");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllTransferTargetsForUserGroupPermission_When_QueryingForSortedByNameDescending()
      throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.APPEND), "user-1-1", GROUP_1_DN, PERM_1)
            .orderByName(SortDirection.DESCENDING)
            .orderByKey(SortDirection.ASCENDING)
            .list();

    assertThat(results).hasSize(7);
    assertThat(results.get(0).getKey()).isEqualTo("USER-2-2");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllTransferSourcesForUserGroupPermission_When_QueryingForSinglePermission()
      throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.DISTRIBUTE), "user-1-1", GROUP_1_DN, PERM_1)
            .list();

    assertThat(results)
        .extracting(WorkbasketSummary::getKey)
        .containsExactlyInAnyOrder("GPK_KSC_1", "USER-1-1", "TEAMLEAD-2");
  }

  // endregion

  // region callerHasPermission

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GetAllTransferTargetsForSubjectUser_When_QueryingForSinglePermission() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.READ)
            .list();

    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GetAllWorkbasketsForSubjectUser_When_QueryingForReadTasksPermission() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.READTASKS)
            .list();

    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GetAllWorkbasketsForSubjectUser_When_QueryingForEditTasksPermission() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.EDITTASKS)
            .list();

    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_GetAllTransferTargetsForSubjectUser_When_QueryingForMultiplePermission() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.READ, WorkbasketPermission.OPEN)
            .list();

    assertThat(results).hasSize(3);
  }

  @WithAccessId(
      user = "user-1-1",
      groups = {GROUP_1_DN, PERM_1})
  @Test
  void should_GetAllTransferTargetsForSubjectUserGroupPerm_When_QueryingForSinglePermission() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.APPEND)
            .list();

    assertThat(results).hasSize(7);
  }

  @WithAccessId(
      user = "user-1-1",
      groups = {GROUP_1_DN, PERM_1})
  @Test
  void should_GetAllTransferTargetsForSubjectUserGroupPerm_When_QueryingForMultiplePermissions() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.APPEND, WorkbasketPermission.OPEN)
            .list();

    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ConsiderBusinessAdminPermissions_When_QueryingWorkbaskets() {
    List<WorkbasketSummary> filteredWorkbaskets =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.OPEN)
            .list();

    assertThat(filteredWorkbaskets).isEmpty();
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_SkipAuthorizationCheckForAdmin_When_QueryingWorkbaskets() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .callerHasPermissions(WorkbasketPermission.OPEN)
            .list();

    assertThat(results).hasSize(26);
  }

  // endregion

}
