package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "query workbasket by permission" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryWorkbasketByPermissionAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;
  private static SortDirection desc = SortDirection.DESCENDING;

  QueryWorkbasketByPermissionAccTest() {
    super();
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAllTransferTargetsForUser()
      throws NotAuthorizedException, InvalidArgumentException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1")
            .list();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getKey()).isEqualTo("USER_1_1");
  }

  @WithAccessId(userName = "dummy")
  @Test
  void testQueryAllTransferTargetsForUserNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketQuery()
              .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1")
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAllTransferTargetsForUserAndGroup()
      throws NotAuthorizedException, InvalidArgumentException, SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .list();
    assertThat(results).hasSize(6);
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAllTransferTargetsForUserAndGroupSortedByNameAscending()
      throws NotAuthorizedException, InvalidArgumentException, SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .orderByName(asc)
            .list();
    assertThat(results).hasSize(6);
    assertThat(results.get(0).getKey()).isEqualTo("GPK_KSC_1");
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAllTransferTargetsForUserAndGroupSortedByNameDescending()
      throws NotAuthorizedException, InvalidArgumentException, SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user_1_1", "group_1")
            .orderByName(desc)
            .orderByKey(asc)
            .list();
    assertThat(results).hasSize(6);
    assertThat(results.get(0).getKey()).isEqualTo("USER_2_2");
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAllTransferSourcesForUserAndGroup()
      throws NotAuthorizedException, InvalidArgumentException, SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.DISTRIBUTE, "user_1_1", "group_1")
            .list();
    assertThat(results).hasSize(2);
    List<String> keys = new ArrayList<>(Arrays.asList("GPK_KSC_1", "USER_1_1"));
    for (WorkbasketSummary wb : results) {
      assertThat(keys.contains(wb.getKey())).isTrue();
    }
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testQueryAllTransferTargetsForUserAndGroupFromSubject() throws SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.APPEND)
            .list();
    assertThat(results).hasSize(6);
  }

  @WithAccessId(userName = "user_1_1")
  @Test
  void testQueryAllAvailableWorkbasketForOpeningForUserAndGroupFromSubject() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.READ)
            .list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"businessadmin"})
  @Test
  void testConsiderBusinessAdminPermissionsWhileQueryingWorkbaskets() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.OPEN)
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(userName = "admin")
  @Test
  void testSkipAuthorizationCheckForAdminWhileQueryingWorkbaskets() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .callerHasPermission(WorkbasketPermission.OPEN)
            .list();
    assertThat(results).hasSize(25);
  }
}
