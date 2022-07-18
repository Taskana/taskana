package acceptance.workbasket.query;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.workbasket.api.AccessItemQueryColumnName.ACCESS_ID;
import static pro.taskana.workbasket.api.AccessItemQueryColumnName.WORKBASKET_ID;
import static pro.taskana.workbasket.api.AccessItemQueryColumnName.WORKBASKET_KEY;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketAccessItemQuery;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

@ExtendWith(JaasExtension.class)
class QueryWorkbasketAccessItemsAccTest extends AbstractAccTest {

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryWorkbasketAccessItemValuesForColumnName() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<String> columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(WORKBASKET_ID, null);
    assertThat(columnValueList).hasSize(24);

    columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(ACCESS_ID, null);
    assertThat(columnValueList).hasSize(10);

    columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(WORKBASKET_KEY, null);
    assertThat(columnValueList).hasSize(24);

    long countEntries = workbasketService.createWorkbasketAccessItemQuery().count();
    assertThat(countEntries).isGreaterThan(columnValueList.size()); // DISTINCT
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIds() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user-1-1", GROUP_1_DN)
            .list();
    assertThat(results).hasSize(8);
  }

  @WithAccessId(user = "unknownuser")
  @Test
  void testQueryAccessItemsForAccessIdsNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

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
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketAccessItemQuery query =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user-1-1", GROUP_1_DN)
            .orderByAccessId(SortDirection.DESCENDING)
            .orderByWorkbasketId(SortDirection.DESCENDING);
    List<WorkbasketAccessItem> results = query.list();
    long count = query.count();
    assertThat(results).hasSize(8).size().isEqualTo(count);
    assertThat(results.get(0).getId()).isEqualTo("WAI:100000000000000000000000000000000003");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIdsAndWorkbasketKey() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user-1-1", GROUP_1_DN)
            .workbasketIdIn(
                "WBI:100000000000000000000000000000000006",
                "WBI:100000000000000000000000000000000002")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIdsWorkbasketKeyLike() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService.createWorkbasketAccessItemQuery().workbasketKeyLike("GPK_KSC%").list();
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsForAccessIdsWorkbasketKeyLikeAndOrderAsc() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
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
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAccessItemsByWorkbasketKeyOrderedDescending() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .orderByWorkbasketId(SortDirection.DESCENDING)
            .orderByAccessId(SortDirection.ASCENDING)
            .list();
    assertThat(results).hasSize(3);
    assertThat(results.get(0).getId()).isEqualTo("WAI:100000000000000000000000000000000009");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryForIdIn() throws Exception {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String[] expectedIds = {
      "WAI:100000000000000000000000000000000001",
      "WAI:100000000000000000000000000000000015",
      "WAI:100000000000000000000000000000000007"
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
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
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
