package acceptance.workbasket.query;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "query classifications with pagination" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryWorkbasketsWithPaginationAccTest extends AbstractAccTest {

  QueryWorkbasketsWithPaginationAccTest() {
    super();
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testGetFirstPageOfWorkbasketQueryWithOffset() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list(0, 5);
    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testGetSecondPageOfWorkbasketQueryWithOffset() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list(5, 5);
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void testListOffsetAndLimitOutOfBounds() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    // both will be 0, working
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list(-1, -3);
    assertThat(results).isEmpty();

    // limit will be 0
    results = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list(1, -3);
    assertThat(results).isEmpty();

    // offset will be 0
    results = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list(-1, 3);
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testPaginationWithPages() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    // Getting full page
    int pageNumber = 2;
    int pageSize = 4;
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(4);

    // Getting full page
    pageNumber = 4;
    pageSize = 1;
    results =
        workbasketService
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(1);

    // Getting last results on 1 big page
    pageNumber = 1;
    pageSize = 100;
    results =
        workbasketService
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(9);

    // Getting last results on multiple pages
    pageNumber = 2;
    pageSize = 5;
    results =
        workbasketService
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testPaginationNullAndNegativeLimitsIgnoring() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    // 0 limit/size = 0 results
    int pageNumber = 2;
    int pageSize = 0;
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).isEmpty();

    // Negative size will be 0 = 0 results
    pageNumber = 2;
    pageSize = -1;
    results =
        workbasketService
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).isEmpty();

    // Negative page = first page
    pageNumber = -1;
    pageSize = 10;
    results =
        workbasketService
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testCountOfWorkbasketQuery() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    long count = workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").count();
    assertThat(count).isEqualTo(9L);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testWorkbasketQueryDomA() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketSummary> result =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_A").list();
    assertThat(result).hasSize(9);
  }
}
