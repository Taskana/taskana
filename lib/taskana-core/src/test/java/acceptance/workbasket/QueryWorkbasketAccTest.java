package acceptance.workbasket;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.api.BaseQuery.SortDirection.ASCENDING;
import static pro.taskana.common.api.BaseQuery.SortDirection.DESCENDING;
import static pro.taskana.workbasket.api.WorkbasketQueryColumnName.NAME;

import acceptance.AbstractAccTest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "query workbasket by permission" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryWorkbasketAccTest extends AbstractAccTest {

  private static WorkbasketService workbasketService;

  @BeforeAll
  static void setup() {
    workbasketService = taskanaEngine.getWorkbasketService();
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void testQueryAllForUserMultipleTimes() {
    WorkbasketQuery query = workbasketService.createWorkbasketQuery();
    int count = (int) query.count();
    assertThat(count).isEqualTo(4);
    List<WorkbasketSummary> workbaskets = query.list();
    assertThat(workbaskets).isNotNull();
    assertThat(workbaskets).hasSize(count);
    workbaskets = query.list();
    assertThat(workbaskets).isNotNull();
    assertThat(workbaskets).hasSize(count);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testQueryAllForBusinessAdminMultipleTimes() {
    WorkbasketQuery query = workbasketService.createWorkbasketQuery();
    int count = (int) query.count();
    assertThat(count).isEqualTo(25);
    List<WorkbasketSummary> workbaskets = query.list();
    assertThat(workbaskets).isNotNull();
    assertThat(workbaskets).hasSize(count);
    workbaskets = query.list();
    assertThat(workbaskets).isNotNull();
    assertThat(workbaskets).hasSize(count);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryAllForAdminMultipleTimes() {
    WorkbasketQuery query = workbasketService.createWorkbasketQuery();
    int count = (int) query.count();
    assertThat(count).isEqualTo(25);
    List<WorkbasketSummary> workbaskets = query.list();
    assertThat(workbaskets).isNotNull();
    assertThat(workbaskets).hasSize(count);
    workbaskets = query.list();
    assertThat(workbaskets).isNotNull();
    assertThat(workbaskets).hasSize(count);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketValuesForColumnName() {
    List<String> columnValueList = workbasketService.createWorkbasketQuery().listValues(NAME, null);
    assertThat(columnValueList).isNotNull();
    assertThat(columnValueList).hasSize(10);

    columnValueList =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%korb%")
            .orderByName(ASCENDING)
            .listValues(NAME, DESCENDING); // will override
    assertThat(columnValueList).isNotNull();
    assertThat(columnValueList).hasSize(4);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByDomain() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().domainIn("DOMAIN_B").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByDomainAndType() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .typeIn(WorkbasketType.PERSONAL)
            .list();
    assertThat(results).hasSize(6);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByName() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameIn("Gruppenpostkorb KSC").list();
    assertThat(results)
        .hasSize(1)
        .first()
        .extracting(WorkbasketSummary::getKey)
        .isEqualTo("GPK_KSC");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByNameStartsWith() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%Gruppenpostkorb KSC%").list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByNameContains() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%Teamlead%", "%Gruppenpostkorb KSC%")
            .list();
    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByNameContainsCaseInsensitive() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%TEAMLEAD%").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByDescription() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .descriptionLike("%ppk%", "%gruppen%")
            .orderByType(DESCENDING)
            .orderByDescription(ASCENDING)
            .list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByOwnerLike() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .ownerLike("%an%", "%te%")
            .orderByOwner(ASCENDING)
            .list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByKey() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByMultipleKeys() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC_1", "GPK_KSC").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByMultipleKeysWithUnknownKey() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().keyIn("GPK_KSC_1", "GPK_Ksc", "GPK_KSC_3").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByKeyContains() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().keyLike("%KSC%").list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByKeyContainsIgnoreCase() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().keyLike("%kSc%").list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByKeyOrNameContainsIgnoreCase() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().keyOrNameLike("%kSc%").list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByNameStartsWithSortedByNameAscending() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .orderByName(ASCENDING)
            .list();
    assertThat(results)
        .hasSize(3)
        .extracting(WorkbasketSummary::getName)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "user-b-1")
  @Test
  void testQueryWorkbasketByNameStartsWithSortedByNameDescending() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("basxet%")
            .orderByName(DESCENDING)
            .list();
    assertThat(results)
        .hasSize(10)
        .extracting(WorkbasketSummary::getName)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "user-b-1")
  @Test
  void testQueryWorkbasketByNameStartsWithSortedByKeyAscending() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("basxet%").orderByKey(ASCENDING).list();
    assertThat(results)
        .hasSize(10)
        .extracting(WorkbasketSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "user-b-1")
  @Test
  void testQueryWorkbasketByNameStartsWithSortedByKeyDescending() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("basxet%").orderByKey(DESCENDING).list();
    assertThat(results)
        .hasSize(10)
        .extracting(WorkbasketSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByCreated() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().createdWithin(toDaysInterval()).list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByModified() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().modifiedWithin(toDaysInterval()).list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryWorkbasketByAdmin() throws Exception {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().nameLike("%").orderByName(DESCENDING).list();
    assertThat(results)
        .hasSize(25)
        .extracting(WorkbasketSummary::getName)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());

    results =
        workbasketService
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermission(
                WorkbasketPermission.TRANSFER, "teamlead-1", GROUP_1_DN, GROUP_2_DN)
            .orderByName(DESCENDING)
            .list();

    assertThat(results).hasSize(13);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void testQueryWorkbasketByDomainLike() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .domainLike("DOMAIN_%")
            .orderByDomain(ASCENDING)
            .list();

    List<String> expectedIds =
        Arrays.asList(
            "WBI:100000000000000000000000000000000001",
            "WBI:100000000000000000000000000000000002",
            "WBI:100000000000000000000000000000000004",
            "WBI:100000000000000000000000000000000005",
            "WBI:100000000000000000000000000000000006",
            "WBI:100000000000000000000000000000000007",
            "WBI:100000000000000000000000000000000008",
            "WBI:100000000000000000000000000000000009",
            "WBI:100000000000000000000000000000000010",
            "WBI:100000000000000000000000000000000012");
    assertThat(results)
        .extracting(WorkbasketSummary::getId)
        .containsExactlyInAnyOrderElementsOf(expectedIds);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryWorkbasketByOwnerInOrderByDomainDesc() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .ownerIn("owner0815")
            .orderByDomain(DESCENDING)
            .list();

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getId()).isEqualTo("WBI:100000000000000000000000000000000015");
    assertThat(results.get(1).getId()).isEqualTo("WBI:100000000000000000000000000000000001");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void testQueryForCustom1In() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_1, "ABCQVW")
            .list();

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo("WBI:100000000000000000000000000000000001");
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom1Like() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_1, "custo%")
            .list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom2In() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_2, "cust2", "custom2")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom2Like() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_2, "cusTo%")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom3In() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_3, "custom3")
            .list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom3Like() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_3, "cu%")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom4In() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_4, "custom4", "team")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForCustom4Like() {
    List<WorkbasketSummary> results =
        workbasketService
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_4, "%u%")
            .list();
    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrgLevl1In() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orgLevel1In("orgl1", "").list();
    assertThat(results).hasSize(24);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrgLevel1Like() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orgLevel1Like("%1").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrgLevel2In() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orgLevel2In("abteilung").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrgLevel2Like() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orgLevel2Like("ab%").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrgLevel3In() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orgLevel3In("orgl3").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrgLevel3Like() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orgLevel3Like("org%").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrgLevel4In() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orgLevel4In("team", "orgl4").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrgLevel4Like() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orgLevel4Like("%").list();
    assertThat(results).hasSize(25);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByOrgLevel1Desc() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orderByOrgLevel1(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketSummary::getOrgLevel1)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByOrgLevel2Asc() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orderByOrgLevel2(ASCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketSummary::getOrgLevel2)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByOrgLevel3Desc() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orderByOrgLevel3(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketSummary::getOrgLevel3)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryForOrderByOrgLevel4Asc() {
    List<WorkbasketSummary> results =
        workbasketService.createWorkbasketQuery().orderByOrgLevel4(ASCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketSummary::getOrgLevel4)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_SortQueryAsc_When_OrderingByCustomAttribute() {
    Iterator<WorkbasketCustomField> iterator = Arrays.stream(WorkbasketCustomField.values())
                                                   .iterator();

    ThrowingConsumer<WorkbasketCustomField> test = customField -> {
      List<WorkbasketSummary> results =
          workbasketService
              .createWorkbasketQuery()
              .orderByCustomAttribute(customField, ASCENDING)
              .list();

      assertThat(results)
          .hasSizeGreaterThan(2)
          .extracting(w -> w.getCustomAttribute(customField))
          .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
    };
    return DynamicTest.stream(iterator, c -> "for " + c,
        test);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_SortQueryDesc_When_OrderingByCustomAttribute() {
    Iterator<WorkbasketCustomField> iterator = Arrays.stream(WorkbasketCustomField.values())
                                                   .iterator();

    ThrowingConsumer<WorkbasketCustomField> test = customField -> {
      List<WorkbasketSummary> results =
          workbasketService
              .createWorkbasketQuery()
              .orderByCustomAttribute(customField, DESCENDING)
              .list();

      assertThat(results)
          .hasSizeGreaterThan(2)
          .extracting(w -> w.getCustomAttribute(customField))
          .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
    };
    return DynamicTest.stream(iterator, c -> "for " + c,
        test);
  }
}
