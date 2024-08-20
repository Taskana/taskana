package acceptance.workbasket.query;

import static io.kadai.workbasket.api.AccessItemQueryColumnName.ACCESS_ID;
import static io.kadai.workbasket.api.AccessItemQueryColumnName.WORKBASKET_ID;
import static io.kadai.workbasket.api.AccessItemQueryColumnName.WORKBASKET_KEY;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketAccessItemQuery;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "query access items for workbaskets" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryWorkbasketAccessItemsAccTest extends AbstractAccTest {

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryWorkbasketAccessItemValuesForColumnName() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<String> columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(WORKBASKET_ID, null);
    assertThat(columnValueList).hasSize(24);

    columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(ACCESS_ID, null);
    assertThat(columnValueList).hasSize(11);

    columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(WORKBASKET_KEY, null);
    assertThat(columnValueList).hasSize(24);

    long countEntries = workbasketService.createWorkbasketAccessItemQuery().count();
    assertThat(countEntries).isGreaterThan(columnValueList.size()); // DISTINCT
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIds() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user-1-1", GROUP_1_DN, PERM_1)
            .list();
    assertThat(results).hasSize(11);
  }

  @WithAccessId(user = "unknownuser")
  @Test
  void testQueryAccessItemsForAccessIdsNotAuthorized() {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    ThrowingCallable call =
        () -> {
          workbasketService
              .createWorkbasketAccessItemQuery()
              .accessIdIn("user-1-1", "group-1")
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIdsOrderedDescending() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    WorkbasketAccessItemQuery query =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user-1-1", GROUP_1_DN, PERM_1)
            .orderByAccessId(SortDirection.DESCENDING)
            .orderByWorkbasketId(SortDirection.DESCENDING);
    List<WorkbasketAccessItem> results = query.list();
    long count = query.count();
    assertThat(results).hasSize(11).size().isEqualTo(count);
    assertThat(results.get(0).getId()).isEqualTo("WAI:100000000000000000000000000000000003");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIdsAndWorkbasketKey() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user-1-1", GROUP_1_DN, PERM_1)
            .workbasketIdIn(
                "WBI:100000000000000000000000000000000006",
                "WBI:100000000000000000000000000000000002",
                "WBI:100000000000000000000000000000000005")
            .list();
    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIdsWorkbasketKeyLike() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService.createWorkbasketAccessItemQuery().workbasketKeyLike("GPK_KSC%").list();
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIdsWorkbasketKeyLikeAndOrderAsc() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .workbasketKeyLike("GPK_KSC%")
            .orderByWorkbasketKey(SortDirection.ASCENDING)
            .list();
    assertThat(results).hasSize(4);
    assertThat(results.get(0).getWorkbasketKey()).isEqualTo("GPK_KSC");
    assertThat(results.get(3).getWorkbasketKey()).isEqualTo("GPK_KSC_2");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsByWorkbasketKey() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .list();
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsByWorkbasketKeyOrderedDescending() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .orderByWorkbasketId(SortDirection.DESCENDING)
            .orderByAccessId(SortDirection.ASCENDING)
            .list();
    assertThat(results).hasSize(4);
    assertThat(results.get(0).getId()).isEqualTo("WAI:100000000000000000000000000000000009");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryForIdIn() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    String[] expectedIds = {
      "WAI:100000000000000000000000000000000001",
      "WAI:100000000000000000000000000000000015",
      "WAI:100000000000000000000000000000000006"
    };
    List<WorkbasketAccessItem> results =
        workbasketService.createWorkbasketAccessItemQuery().idIn(expectedIds).list();
    assertThat(results)
        .extracting(WorkbasketAccessItem::getId)
        .containsExactlyInAnyOrder(expectedIds);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryForOrderById() throws Exception {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .orderById(SortDirection.ASCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketAccessItem::getId)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }
}
