package acceptance.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketSummary;

/** Acceptance test for workbasket queries and authorization. */
@ExtendWith(JaasExtension.class)
class WorkbasketQueryAccTest extends AbstractAccTest {

  WorkbasketQueryAccTest() {
    super();
  }

  @Test
  void testQueryWorkbasketByUnauthenticated() throws InvalidArgumentException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertEquals(0L, results.size());
    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            workbasketService
                .createWorkbasketQuery()
                .nameLike("%")
                .accessIdsHavePermission(
                    WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
                .list());
  }

  @WithAccessId(userName = "unknown")
  @Test
  void testQueryWorkbasketByUnknownUser() throws InvalidArgumentException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertEquals(0L, results.size());

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            workbasketService
                .createWorkbasketQuery()
                .nameLike("%")
                .accessIdsHavePermission(
                    WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
                .list());
  }

  @WithAccessId(userName = "unknown", groupNames = "businessadmin")
  @Test
  void testQueryWorkbasketByBusinessAdmin()
      throws NotAuthorizedException, InvalidArgumentException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertEquals(25L, results.size());

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermission(
                WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
            .list();

    assertEquals(13L, results.size());
  }

  @WithAccessId(userName = "unknown", groupNames = "admin")
  @Test
  void testQueryWorkbasketByAdmin() throws NotAuthorizedException, InvalidArgumentException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").list();
    assertEquals(25L, results.size());

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermission(
                WorkbasketPermission.TRANSFER, "teamlead_1", "group_1", "group_2")
            .list();

    assertEquals(13L, results.size());
  }
}
