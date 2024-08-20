package acceptance.workbasket.query;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "query classifications with pagination" scenarios. */
@ExtendWith(JaasExtension.class)
class WorkbasketQueryWithOrderedPaginationAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;
  private static SortDirection desc = SortDirection.DESCENDING;

  @WithAccessId(
      user = "teamlead-1",
      groups = {GROUP_1_DN, GROUP_2_DN})
  @Test
  void testGetFirstPageOfTaskQueryWithOffset() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").orderByKey(asc).list(0, 5);
    assertThat(results).hasSize(5);
    assertThat(results.get(0).getKey()).isEqualTo("GPK_KSC");
    assertThat(results.get(4).getKey()).isEqualTo("TEAMLEAD-2");

    results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").orderByKey(desc).list(0, 5);
    assertThat(results).hasSize(5);
    assertThat(results.get(0).getKey()).isEqualTo("USER-2-2");
    assertThat(results.get(4).getKey()).isEqualTo("TPK_VIP");
  }

  @WithAccessId(
      user = "teamlead-1",
      groups = {GROUP_1_DN, GROUP_2_DN})
  @Test
  void testGetSecondPageOfTaskQueryWithOffset() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").orderByKey(asc).list(5, 5);
    assertThat(results).hasSize(5);
    assertThat(results.get(0).getKey()).isEqualTo("TPK_VIP");
    assertThat(results.get(4).getKey()).isEqualTo("USER-2-2");

    results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").orderByKey(desc).list(5, 5);
    assertThat(results).hasSize(5);
    assertThat(results.get(0).getKey()).isEqualTo("TEAMLEAD-2");
    assertThat(results.get(4).getKey()).isEqualTo("GPK_KSC");
  }
}
