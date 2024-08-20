package acceptance.workbasket.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for workbasket queries and authorization. */
@ExtendWith(JaasExtension.class)
class WorkbasketQueryAccTest extends AbstractAccTest {

  @Test
  void testQueryWorkbasketByUnauthenticated() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).isEmpty();
    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketQuery()
              .nameLike("%")
              .accessIdsHavePermissions(
                  List.of(WorkbasketPermission.TRANSFER),
                  "teamlead-1",
                  GROUP_1_DN,
                  GROUP_2_DN,
                  PERM_1)
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "unknownuser")
  @Test
  void testQueryWorkbasketByUnknownUser() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).isEmpty();
    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketQuery()
              .nameLike("%")
              .accessIdsHavePermissions(
                  List.of(WorkbasketPermission.TRANSFER),
                  "teamlead-1",
                  GROUP_1_DN,
                  GROUP_2_DN,
                  PERM_1)
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryWorkbasketByBusinessAdmin() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).hasSize(26);

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.TRANSFER),
                "teamlead-1",
                GROUP_1_DN,
                GROUP_2_DN,
                PERM_1)
            .list();

    assertThat(results).hasSize(13);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryWorkbasketByAdmin() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).hasSize(26);

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.TRANSFER),
                "teamlead-1",
                GROUP_1_DN,
                GROUP_2_DN,
                PERM_1)
            .list();

    assertThat(results).hasSize(13);
  }
}
