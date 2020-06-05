package acceptance.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for workbasket queries and authorization. */
@ExtendWith(JaasExtension.class)
class WorkbasketQueryAccTest extends AbstractAccTest {

  WorkbasketQueryAccTest() {
    super();
  }

  @Test
  void testQueryWorkbasketByUnauthenticated() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).isEmpty();
    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketQuery()
              .nameLike("%")
              .accessIdsHavePermission(
                  WorkbasketPermission.TRANSFER, "teamlead-1", "group-1", "group-2")
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "unknown")
  @Test
  void testQueryWorkbasketByUnknownUser() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).isEmpty();
    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketQuery()
              .nameLike("%")
              .accessIdsHavePermission(
                  WorkbasketPermission.TRANSFER, "teamlead-1", "group-1", "group-2")
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "unknown", groups = "businessadmin")
  @Test
  void testQueryWorkbasketByBusinessAdmin()
      throws NotAuthorizedException, InvalidArgumentException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).hasSize(25);

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermission(
                WorkbasketPermission.TRANSFER, "teamlead-1", "group-1", "group-2")
            .list();

    assertThat(results).hasSize(13);
  }

  @WithAccessId(user = "unknown", groups = "admin")
  @Test
  void testQueryWorkbasketByAdmin() throws NotAuthorizedException, InvalidArgumentException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertThat(results).hasSize(25);

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermission(
                WorkbasketPermission.TRANSFER, "teamlead-1", "group-1", "group-2")
            .list();

    assertThat(results).hasSize(13);
  }
}
