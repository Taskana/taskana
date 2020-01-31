package acceptance.workbasket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketSummary;

/** Acceptance test for all "query classifications with pagination" scenarios. */
@ExtendWith(JaasExtension.class)
class WorkbasketQueryWithOrderedPaginationAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;
  private static SortDirection desc = SortDirection.DESCENDING;

  @Test
  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  void testGetFirstPageOfTaskQueryWithOffset() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").orderByKey(asc).list(0, 5);
    assertThat(results.size(), equalTo(5));
    assertThat(results.get(0).getKey(), equalTo("GPK_KSC"));
    assertThat(results.get(4).getKey(), equalTo("TEAMLEAD_2"));

    results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").orderByKey(desc).list(0, 5);
    assertThat(results.size(), equalTo(5));
    assertThat(results.get(0).getKey(), equalTo("USER_2_2"));
    assertThat(results.get(4).getKey(), equalTo("TPK_VIP"));
  }

  @Test
  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  void testGetSecondPageOfTaskQueryWithOffset() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").orderByKey(asc).list(5, 5);
    assertThat(results.size(), equalTo(5));
    assertThat(results.get(0).getKey(), equalTo("TPK_VIP"));
    assertThat(results.get(4).getKey(), equalTo("USER_2_2"));

    results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").orderByKey(desc).list(5, 5);
    assertThat(results.size(), equalTo(5));
    assertThat(results.get(0).getKey(), equalTo("TEAMLEAD_2"));
    assertThat(results.get(4).getKey(), equalTo("GPK_KSC"));
  }
}
