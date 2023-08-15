package acceptance.workbasket.query;

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

  private static final WorkbasketService WORKBASKET_SERVICE = taskanaEngine.getWorkbasketService();

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_QueryAllMultipleTimes_When_UserIsTeamlead1() {
    WorkbasketQuery query = WORKBASKET_SERVICE.createWorkbasketQuery();
    int count = (int) query.count();
    assertThat(count).isEqualTo(4);
    List<WorkbasketSummary> workbaskets = query.list();
    assertThat(workbaskets).hasSize(count);
    workbaskets = query.list();
    assertThat(workbaskets).hasSize(count);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_QueryAllMultipleTimes_When_UserIsBusinessAdmin() {
    WorkbasketQuery query = WORKBASKET_SERVICE.createWorkbasketQuery();
    int count = (int) query.count();
    assertThat(count).isEqualTo(26);
    List<WorkbasketSummary> workbaskets = query.list();
    assertThat(workbaskets).hasSize(count);
    workbaskets = query.list();
    assertThat(workbaskets).hasSize(count);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_QueryAllMultipleTimes_When_UserIsAdmin() {
    WorkbasketQuery query = WORKBASKET_SERVICE.createWorkbasketQuery();
    int count = (int) query.count();
    assertThat(count).isEqualTo(26);
    List<WorkbasketSummary> workbaskets = query.list();
    assertThat(workbaskets).hasSize(count);
    workbaskets = query.list();
    assertThat(workbaskets).hasSize(count);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_QueryWorkbasketValues_When_ColumnIsName() {
    List<String> columnValueList =
        WORKBASKET_SERVICE.createWorkbasketQuery().listValues(NAME, null);
    assertThat(columnValueList).hasSize(10);

    columnValueList =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .nameLike("%korb%")
            .orderByName(ASCENDING)
            .listValues(NAME, DESCENDING); // will override
    assertThat(columnValueList).hasSize(4);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForDomainIn() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().domainIn("DOMAIN_B").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForDomainInAndTypeIn() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .typeIn(WorkbasketType.PERSONAL)
            .list();
    assertThat(results).hasSize(6);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ReturnWorkbasketsMatchingName_When_QueriedWithNameIn() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().nameIn("Gruppenpostkorb KSC").list();
    assertThat(results)
        .hasSize(1)
        .first()
        .extracting(WorkbasketSummary::getKey)
        .isEqualTo("GPK_KSC");
  }

  @WithAccessId(user = "admin", groups = GROUP_1_DN)
  @Test
  void should_ReturnWorkbasketsMatchingName_When_QueriedWithNameLikeWithEszett() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().nameLike("%ß%").list();
    assertThat(results)
        .hasSize(1)
        .first()
        .extracting(WorkbasketSummary::getKey)
        .isEqualTo("MASSNAHMEN");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForNameStartsWith() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().nameLike("%Gruppenpostkorb KSC%").list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForNameContains() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .nameLike("%Teamlead%", "%Gruppenpostkorb KSC%")
            .list();
    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForNameContainsCaseInsensitive() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().nameLike("%TEAMLEAD%").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForDescriptionLike() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .descriptionLike("%ppk%", "%gruppen%")
            .orderByType(DESCENDING)
            .orderByDescription(ASCENDING)
            .list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForOwnerLike() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .ownerLike("%1-2%")
            .orderByOwner(ASCENDING)
            .list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_QuerySingleWb_When_UsingKeyIn() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().keyIn("GPK_KSC").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_QueryMultipleWbs_When_UsingKeyIn() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().keyIn("GPK_KSC_1", "GPK_KSC").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_QueryMultipleWbs_When_UsingKeyInWithUnknownKey() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .keyIn("GPK_KSC_1", "GPK_KSC", "GPK_KSC_3")
            .list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_QueryByKeyContains_When_UsingKeyIn() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().keyLike("%KSC%").list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_QueryByKeyContainsIgnoreCase_When_UsingKeyIn() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().keyLike("%kSc%").list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_QueryByKeyOrNameContainsIgnoreCase_When_UsingKeyIn() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().keyOrNameLike("%kSc%").list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_QueryByNameStartsWithSortedByNameAscending_When_UsingNameLike() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
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
  void should_QueryByNameStartsWithSortedByNameDescending_When_UsingNameLike() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
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
  void should_QueryByNameStartsWithSortedByKeyAscending_When_UsingNameLike() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().nameLike("basxet%").orderByKey(ASCENDING).list();
    assertThat(results)
        .hasSize(10)
        .extracting(WorkbasketSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "user-b-1")
  @Test
  void should_QueryByNameStartsWithSortedByKeyDescending_When_UsingNameLike() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .nameLike("basxet%")
            .orderByKey(DESCENDING)
            .list();
    assertThat(results)
        .hasSize(10)
        .extracting(WorkbasketSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForCreatedWithin() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().createdWithin(toDaysInterval()).list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForModifiedWithinW() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().modifiedWithin(toDaysInterval()).list();
    assertThat(results).hasSize(9);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_QueryWorkbaskets_When_UserIsAdmin() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().nameLike("%").orderByName(DESCENDING).list();
    assertThat(results)
        .hasSize(26)
        .extracting(WorkbasketSummary::getName)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());

    results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .nameLike("%")
            .accessIdsHavePermissions(
                List.of(WorkbasketPermission.TRANSFER), "teamlead-1", GROUP_1_DN, GROUP_2_DN)
            .orderByName(DESCENDING)
            .list();

    assertThat(results).hasSize(13);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ApplyFilter_When_QueryingForDomainLike() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .domainLike("DOMAIN_%")
            .orderByDomain(ASCENDING)
            .list();

    assertThat(results)
        .extracting(WorkbasketSummary::getId)
        .containsExactlyInAnyOrder(
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
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOwnerInOrderByDomainDesc() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .ownerIn("teamlead-1")
            .orderByDomain(DESCENDING)
            .list();

    assertThat(results)
        .extracting(WorkbasketSummary::getId)
        .containsExactly("WBI:100000000000000000000000000000000001");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ApplyFilter_When_QueryingForCustom1In() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_1, "ABCQVW")
            .list();

    assertThat(results)
        .extracting(WorkbasketSummary::getId)
        .containsExactly("WBI:100000000000000000000000000000000001");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom1Like() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_1, "custo%")
            .list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom2In() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_2, "cust2", "custom2")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom2Like() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_2, "cusTo%")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom3In() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_3, "custom3")
            .list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom3Like() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_3, "cu%")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom4In() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_4, "custom4", "team")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom4Like() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_4, "%u%")
            .list();
    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom5In() throws Exception {

    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_5, "custom5")
            .list();

    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom5Like() throws Exception {

    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_5, "custo%")
            .list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom6In() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_6, "custom6")
            .list();

    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom6Like() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_6, "cust%")
            .list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom7In() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_7, "custom7")
            .list();

    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom7Like() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_7, "cust%")
            .list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom8In() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_8, "custom8")
            .list();

    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForCustom8Like() throws Exception {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE
            .createWorkbasketQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_8, "cust%")
            .list();
    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOrgLevel1In() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orgLevel1In("orgl1", "").list();
    assertThat(results).hasSize(25);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOrgLevel1Like() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orgLevel1Like("%1").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOrgLevel2In() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orgLevel2In("abteilung").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOrgLevel2Like() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orgLevel2Like("ab%").list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOrgLevel3In() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orgLevel3In("orgl3").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOrgLevel3Like() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orgLevel3Like("org%").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOrgLevel4In() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orgLevel4In("team", "orgl4").list();
    assertThat(results).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyFilter_When_QueryingForOrgLevel4Like() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orgLevel4Like("%").list();
    assertThat(results).hasSize(26);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_OrderByOrgLevel1Desc_When_QueryingWorkbaskets() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orderByOrgLevel1(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketSummary::getOrgLevel1)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_OrderByOrgLevel2Asc_When_QueryingWorkbaskets() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orderByOrgLevel2(ASCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketSummary::getOrgLevel2)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_OrderByOrgLevel3Desc_When_QueryingWorkbaskets() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orderByOrgLevel3(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketSummary::getOrgLevel3)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_OrderByOrgLevel4Asc_When_QueryingWorkbaskets() {
    List<WorkbasketSummary> results =
        WORKBASKET_SERVICE.createWorkbasketQuery().orderByOrgLevel4(ASCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(WorkbasketSummary::getOrgLevel4)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_SortQueryAsc_When_OrderingByCustomAttribute() {
    Iterator<WorkbasketCustomField> iterator =
        Arrays.stream(WorkbasketCustomField.values()).iterator();

    ThrowingConsumer<WorkbasketCustomField> test =
        customField -> {
          List<WorkbasketSummary> results =
              WORKBASKET_SERVICE
                  .createWorkbasketQuery()
                  .orderByCustomAttribute(customField, ASCENDING)
                  .list();

          assertThat(results)
              .hasSizeGreaterThan(2)
              .extracting(w -> w.getCustomField(customField))
              .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
        };
    return DynamicTest.stream(iterator, c -> "for " + c, test);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_SortQueryDesc_When_OrderingByCustomAttribute() {
    Iterator<WorkbasketCustomField> iterator =
        Arrays.stream(WorkbasketCustomField.values()).iterator();

    ThrowingConsumer<WorkbasketCustomField> test =
        customField -> {
          List<WorkbasketSummary> results =
              WORKBASKET_SERVICE
                  .createWorkbasketQuery()
                  .orderByCustomAttribute(customField, DESCENDING)
                  .list();

          assertThat(results)
              .hasSizeGreaterThan(2)
              .extracting(w -> w.getCustomField(customField))
              .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
        };
    return DynamicTest.stream(iterator, c -> "for " + c, test);
  }
}
