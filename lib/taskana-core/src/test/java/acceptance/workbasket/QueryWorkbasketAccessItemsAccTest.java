package acceptance.workbasket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pro.taskana.AccessItemQueryColumnName.ACCESS_ID;
import static pro.taskana.AccessItemQueryColumnName.WORKBASKET_ID;
import static pro.taskana.AccessItemQueryColumnName.WORKBASKET_KEY;

import acceptance.AbstractAccTest;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketAccessItemQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "query access items for workbaskets" scenarios. */
@ExtendWith(JAASExtension.class)
class QueryWorkbasketAccessItemsAccTest extends AbstractAccTest {

  QueryWorkbasketAccessItemsAccTest() {
    super();
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryWorkbasketAccessItemValuesForColumnName() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<String> columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(WORKBASKET_ID, null);
    assertNotNull(columnValueList);
    assertEquals(24, columnValueList.size());

    columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(ACCESS_ID, null);
    assertNotNull(columnValueList);
    assertEquals(9, columnValueList.size());

    columnValueList =
        workbasketService.createWorkbasketAccessItemQuery().listValues(WORKBASKET_KEY, null);
    assertNotNull(columnValueList);
    assertEquals(24, columnValueList.size());

    long countEntries = workbasketService.createWorkbasketAccessItemQuery().count();
    assertTrue(columnValueList.size() < countEntries); // DISTINCT
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAccessItemsForAccessIds() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .list();
    assertEquals(8L, results.size());
  }

  @WithAccessId(userName = "dummy")
  @Test
  void testQueryAccessItemsForAccessIdsNotAuthorized() {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            workbasketService
                .createWorkbasketAccessItemQuery()
                .accessIdIn("user_1_1", "group_1")
                .list());
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAccessItemsForAccessIdsOrderedDescending() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    WorkbasketAccessItemQuery query =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .orderByAccessId(SortDirection.DESCENDING)
            .orderByWorkbasketId(SortDirection.DESCENDING);
    List<WorkbasketAccessItem> results = query.list();
    long count = query.count();
    assertEquals(8L, results.size());
    assertEquals(results.size(), count);
    assertEquals("WAI:100000000000000000000000000000000003", results.get(0).getId());
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAccessItemsForAccessIdsAndWorkbasketKey() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .accessIdIn("user_1_1", "group_1")
            .workbasketIdIn(
                "WBI:100000000000000000000000000000000006",
                "WBI:100000000000000000000000000000000002")
            .list();
    assertEquals(3L, results.size());
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAccessItemsForAccessIdsWorkbasketKeyLike() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService.createWorkbasketAccessItemQuery().workbasketKeyLike("GPK_KSC%").list();
    assertEquals(4L, results.size());
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAccessItemsForAccessIdsWorkbasketKeyLikeAndOrderAsc()
      throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .workbasketKeyLike("GPK_KSC%")
            .orderByWorkbasketKey(SortDirection.ASCENDING)
            .list();
    assertEquals(4L, results.size());
    assertEquals("GPK_KSC", results.get(0).getWorkbasketKey());
    assertEquals("GPK_KSC_2", results.get(3).getWorkbasketKey());
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAccessItemsByWorkbasketKey() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .list();
    assertEquals(3L, results.size());
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  void testQueryAccessItemsByWorkbasketKeyOrderedDescending() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .workbasketIdIn("WBI:100000000000000000000000000000000006")
            .orderByWorkbasketId(SortDirection.DESCENDING)
            .orderByAccessId(SortDirection.ASCENDING)
            .list();
    assertEquals(3L, results.size());
    assertEquals("WAI:100000000000000000000000000000000009", results.get(0).getId());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testQueryForIdIn() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    String[] expectedIds = {
      "WAI:100000000000000000000000000000000001",
      "WAI:100000000000000000000000000000000015",
      "WAI:100000000000000000000000000000000007"
    };
    List<WorkbasketAccessItem> results =
        workbasketService.createWorkbasketAccessItemQuery().idIn(expectedIds).list();
    for (String id : Arrays.asList(expectedIds)) {
      assertTrue(results.stream().anyMatch(accessItem -> accessItem.getId().equals(id)));
    }
  }

  @WithAccessId(userName = "businessadmin")
  @Test
  void testQueryForOrderById() throws NotAuthorizedException {
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    List<WorkbasketAccessItem> results =
        workbasketService
            .createWorkbasketAccessItemQuery()
            .orderById(SortDirection.ASCENDING)
            .list();
    assertEquals("0000000000000000000000000000000000000900", results.get(0).getId());
    assertEquals(
        "WAI:100000000000000000000000000000000123", results.get(results.size() - 1).getId());
  }
}
