package acceptance.classification;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.classification.api.ClassificationQueryColumnName.CREATED;
import static pro.taskana.classification.api.ClassificationQueryColumnName.NAME;
import static pro.taskana.classification.api.ClassificationQueryColumnName.TYPE;
import static pro.taskana.classification.api.ClassificationQueryColumnName.VALID_IN_DOMAIN;
import static pro.taskana.common.api.BaseQuery.SortDirection.ASCENDING;
import static pro.taskana.common.api.BaseQuery.SortDirection.DESCENDING;

import acceptance.AbstractAccTest;
import java.text.Collator;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;

/** Acceptance test for all "get classification" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryClassificationAccTest extends AbstractAccTest {

  private static ClassificationService classificationService;

  @BeforeAll
  static void setup() {
    classificationService = taskanaEngine.getClassificationService();
  }

  @Test
  void testQueryClassificationValuesForColumnName() {
    List<String> columnValueList =
        classificationService.createClassificationQuery().listValues(NAME, null);
    assertThat(columnValueList).hasSize(16);

    columnValueList = classificationService.createClassificationQuery().listValues(TYPE, null);
    assertThat(columnValueList).hasSize(2);

    columnValueList =
        classificationService.createClassificationQuery().domainIn("").listValues(TYPE, null);
    assertThat(columnValueList).hasSize(2);

    columnValueList =
        classificationService.createClassificationQuery().domainIn("").listValues(CREATED, null);
    assertThat(columnValueList).hasSize(1);

    columnValueList =
        classificationService
            .createClassificationQuery()
            .domainIn("")
            .validInDomainEquals(false)
            .listValues(VALID_IN_DOMAIN, null);
    assertThat(columnValueList).hasSize(1); // all are false in ""
  }

  @Test
  void testFindClassificationsByCategoryAndDomain() {
    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .categoryIn("MANUAL")
            .domainIn("DOMAIN_A")
            .list();

    assertThat(classificationSummaryList).hasSize(3);
  }

  @Test
  void testGetOneClassificationForMultipleDomains() {
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("L10000")
            .domainIn("DOMAIN_A", "DOMAIN_B", "")
            .list();

    assertThat(classifications).hasSize(2);
  }

  @Test
  void testGetClassificationsForTypeAndParent() {
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .typeIn("TASK", "DOCUMENT")
            .parentIdIn("")
            .list();

    assertThat(classifications)
        .hasSize(30)
        .extracting(ClassificationSummary::getType)
        .containsOnly("TASK", "DOCUMENT")
        .areExactly(28, new Condition<>("TASK"::equals, "TASK"))
        .areExactly(2, new Condition<>("DOCUMENT"::equals, "DOCUMENT"));
  }

  @Test
  void testGetClassificationsForKeyAndCategories() {
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("T2100", "L10000")
            .categoryIn("EXTERNAL", "MANUAL")
            .list();

    assertThat(classifications)
        .hasSize(5)
        .extracting(ClassificationSummary::getCategory)
        .containsOnly("EXTERNAL", "MANUAL")
        .areExactly(2, new Condition<>("EXTERNAL"::equals, "EXTERNAL"))
        .areExactly(3, new Condition<>("MANUAL"::equals, "MANUAL"));
  }

  @Test
  void testGetClassificationsWithParentId() {
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL")
            .parentIdIn("CLI:100000000000000000000000000000000014")
            .list();

    assertThat(classifications).hasSize(1);

    classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL", "AUTOMATIC")
            .parentIdIn(
                "CLI:100000000000000000000000000000000014",
                "CLI:100000000000000000000000000000000010",
                "CLI:100000000000000000000000000000000011")
            .domainIn("DOMAIN_A")
            .list();
    assertThat(classifications).hasSize(2);
  }

  @Test
  void testGetClassificationsWithParentKey() {
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL")
            .parentKeyIn("L10000")
            .list();
    assertThat(classifications).hasSize(2);

    classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL", "AUTOMATIC")
            .parentKeyIn("L10000", "T2100", "T6310")
            .domainIn("DOMAIN_A")
            .list();
    assertThat(classifications).hasSize(2);
  }

  @Test
  void testGetClassificationsWithCustom1() throws InvalidArgumentException {
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("1", "VNR,RVNR,KOLVNR", "VNR")
            .domainIn("DOMAIN_A")
            .list();
    assertThat(classifications).hasSize(14);
  }

  @Test
  void testGetClassificationsWithCustom1Like() throws InvalidArgumentException {
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .customAttributeLike("1", "%RVNR%")
            .domainIn("DOMAIN_A")
            .typeIn("TASK")
            .list();
    assertThat(classifications).hasSize(14);
  }

  @Test
  void testGetClassificationsWithParentAndCustom2() throws InvalidArgumentException {
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .parentIdIn("CLI:100000000000000000000000000000000004")
            .customAttributeIn("2", "TEXT_1", "TEXT_2")
            .list();

    assertThat(classifications).hasSize(3);
  }

  @Test
  void testFindClassificationsByCreatedTimestamp() {
    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .createdWithin(toDaysInterval())
            .list();

    assertThat(classificationSummaryList).hasSize(18);
  }

  @Test
  void testFindClassificationsByPriorityAndValidInDomain() {
    List<ClassificationSummary> list =
        classificationService
            .createClassificationQuery()
            .validInDomainEquals(Boolean.TRUE)
            .priorityIn(1, 2, 3)
            .list();

    assertThat(list).hasSize(16);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testFindClassificationByModifiedWithin()
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InvalidArgumentException {
    String clId = "CLI:200000000000000000000000000000000015";
    classificationService.updateClassification(classificationService.getClassification(clId));

    List<ClassificationSummary> list =
        classificationService
            .createClassificationQuery()
            .modifiedWithin(
                new TimeInterval(
                    classificationService.getClassification(clId).getModified(), Instant.now()))
            .list();

    assertThat(list).hasSize(1).first().extracting(ClassificationSummary::getId).isEqualTo(clId);
  }

  @Test
  void testQueryForNameLike() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().nameLike("Dynamik%").list();
    assertThat(results).hasSize(8);
  }

  @Test
  void testQueryForNameIn() {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .nameIn("Widerruf", "OLD-Leistungsfall")
            .list();
    assertThat(results).hasSize(6);
  }

  @Test
  void testQueryForDescriptionLike() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().descriptionLike("Widerruf%").list();
    assertThat(results).hasSize(9);
  }

  @Test
  void testQueryForServiceLevelIn() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().serviceLevelIn("P2D").list();
    assertThat(results).hasSize(5);
  }

  @Test
  void testQueryForServiceLevelLike() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().serviceLevelLike("PT%").list();
    assertThat(results).isEmpty();
  }

  @Test
  void testQueryForApplicationEntryPointIn() {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .applicationEntryPointIn("specialPoint", "point0815")
            .list();
    assertThat(results).hasSize(3);
  }

  @Test
  void testQueryForApplicationEntryPointLike() {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .applicationEntryPointLike("point%")
            .list();
    assertThat(results).hasSize(3);
  }

  @Test
  void testQueryForCustom1In() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("1", "VNR,RVNR,KOLVNR, ANR", "VNR")
            .list();
    assertThat(results).hasSize(17);
  }

  @Test
  void testQueryForCustom2In() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("2", "CUSTOM2", "custom2")
            .list();
    assertThat(results).hasSize(5);
  }

  @Test
  void testQueryForCustom3In() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("3", "Custom3", "custom3")
            .list();
    assertThat(results).hasSize(5);
  }

  @Test
  void testQueryForCustom4In() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeIn("4", "custom4").list();
    assertThat(results).hasSize(5);
  }

  @Test
  void testQueryForCustom5In() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeIn("5", "custom5").list();
    assertThat(results).hasSize(5);
  }

  @Test
  void testQueryForCustom6In() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeIn("6", "custom6").list();
    assertThat(results).hasSize(5);
  }

  @Test
  void testQueryForCustom7In() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("7", "custom7", "custom_7")
            .list();
    assertThat(results).hasSize(5);
  }

  @Test
  void testQueryForCustom8In() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("8", "custom_8", "custom8")
            .list();
    assertThat(results).hasSize(5);
  }

  @Test
  void testQueryForCustom2Like() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("2", "cus%").list();
    assertThat(results).hasSize(6);
  }

  @Test
  void testQueryForCustom3Like() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("3", "cus%").list();
    assertThat(results).hasSize(6);
  }

  @Test
  void testQueryForCustom4Like() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("4", "cus%").list();
    assertThat(results).hasSize(6);
  }

  @Test
  void testQueryForCustom5Like() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("5", "cus%").list();
    assertThat(results).hasSize(6);
  }

  @Test
  void testQueryForCustom6Like() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("6", "cus%").list();
    assertThat(results).hasSize(6);
  }

  @Test
  void testQueryForCustom7Like() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("7", "cus%").list();
    assertThat(results).hasSize(6);
  }

  @Test
  void testQueryForCustom8Like() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("8", "cus%").list();
    assertThat(results).hasSize(6);
  }

  @Test
  void testQueryForOrderByKeyAsc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByKey(ASCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @Test
  void testQueryForOrderByParentIdDesc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByParentId(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getParentId)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void testQueryForOrderByParentKeyDesc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByParentKey(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getParentKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void testQueryForOrderByCategoryDesc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByCategory(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCategory)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void testQueryForOrderByDomainAsc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByDomain(ASCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getDomain)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @Test
  void testQueryForOrderByPriorityDesc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByPriority(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .isSortedAccordingTo(
            Comparator.comparingInt(ClassificationSummary::getPriority).reversed());
  }

  @Test
  void testQueryForOrderByNameAsc_old() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByName(ASCENDING).list();
    assertThat(results.get(0).getName()).isEqualTo("Beratungsprotokoll");
  }

  // This test checks the collation of the used databases H2, Postgres and DB2 11.1
  @Test
  void testQueryForOrderByNameAsc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByName(ASCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getName)
        .isSortedAccordingTo(Collator.getInstance(Locale.GERMANY));
  }

  @Test
  void testQueryForOrderByServiceLevelDesc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByServiceLevel(DESCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getServiceLevel)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void testQueryForOrderByApplicationEntryPointAsc() {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByApplicationEntryPoint(ASCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getApplicationEntryPoint)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @Test
  void testQueryForOrderByParentKeyAsc() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByParentKey(ASCENDING).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getParentKey)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @Test
  void testQueryForOrderByCustom1Desc() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("1", DESCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCustom1)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void testQueryForOrderByCustom2Asc() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("2", ASCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCustom2)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @Test
  void testQueryForOrderByCustom3Desc() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("3", DESCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCustom3)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void testQueryForOrderByCustom4Asc() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("4", ASCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCustom4)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @Test
  void testQueryForOrderByCustom5Desc() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("5", DESCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCustom5)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void testQueryForOrderByCustom6Asc() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("6", ASCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCustom6)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @Test
  void testQueryForOrderByCustom7Desc() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("7", DESCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCustom7)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void testQueryForOrderByCustom8Asc() throws InvalidArgumentException {
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("8", ASCENDING)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(ClassificationSummary::getCustom8)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }
}
