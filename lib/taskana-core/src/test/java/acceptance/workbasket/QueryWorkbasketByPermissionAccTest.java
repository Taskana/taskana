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
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
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

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testQueryAllTransferTargetsForUser()
      throws NotAuthorizedException, InvalidArgumentException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user-1-1")
            .list();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getKey()).isEqualTo("USER-1-1");
  }

  @WithAccessId(user = "dummy")
  @Test
  void testQueryAllTransferTargetsForUserNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketQuery()
              .accessIdsHavePermission(WorkbasketPermission.APPEND, "user-1-1")
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testQueryAllTransferTargetsForUserAndGroup()
      throws NotAuthorizedException, InvalidArgumentException, SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user-1-1", "group_1")
            .list();
    assertThat(results).hasSize(6);
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testQueryAllTransferTargetsForUserAndGroupSortedByNameAscending()
      throws NotAuthorizedException, InvalidArgumentException, SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user-1-1", "group_1")
            .orderByName(asc)
            .list();
    assertThat(results).hasSize(6);
    assertThat(results.get(0).getKey()).isEqualTo("GPK_KSC_1");
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testQueryAllTransferTargetsForUserAndGroupSortedByNameDescending()
      throws NotAuthorizedException, InvalidArgumentException, SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.APPEND, "user-1-1", "group_1")
            .orderByName(desc)
            .orderByKey(asc)
            .list();
    assertThat(results).hasSize(6);
    assertThat(results.get(0).getKey()).isEqualTo("USER-2-2");
  }

  @WithAccessId(user = "dummy", groups = "businessadmin")
  @Test
  void testQueryAllTransferSourcesForUserAndGroup()
      throws NotAuthorizedException, InvalidArgumentException, SystemException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .accessIdsHavePermission(WorkbasketPermission.DISTRIBUTE, "user-1-1", "group_1")
            .list();
    assertThat(results).hasSize(2);
    List<String> keys = new ArrayList<>(Arrays.asList("GPK_KSC_1", "USER-1-1"));
    for (WorkbasketSummary wb : results) {
      assertThat(keys.contains(wb.getKey())).isTrue();
    }
  }

  @WithAccessId(user = "user-1-1", groups = "group_1")
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

  @WithAccessId(user = "user-1-1")
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

  @WithAccessId(user = "teamlead-1", groups = "businessadmin")
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

  @WithAccessId(user = "admin")
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
