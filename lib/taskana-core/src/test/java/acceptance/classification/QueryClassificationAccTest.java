package acceptance.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static pro.taskana.classification.api.ClassificationQueryColumnName.CREATED;
import static pro.taskana.classification.api.ClassificationQueryColumnName.NAME;
import static pro.taskana.classification.api.ClassificationQueryColumnName.TYPE;
import static pro.taskana.classification.api.ClassificationQueryColumnName.VALID_IN_DOMAIN;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.ClassificationSummary;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "get classification" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryClassificationAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;
  private static SortDirection desc = SortDirection.DESCENDING;

  QueryClassificationAccTest() {
    super();
  }

  @Test
  void testQueryClassificationValuesForColumnName() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<String> columnValueList =
        classificationService.createClassificationQuery().listValues(NAME, null);
    assertNotNull(columnValueList);
    assertEquals(16, columnValueList.size());

    columnValueList = classificationService.createClassificationQuery().listValues(TYPE, null);
    assertNotNull(columnValueList);
    assertEquals(2, columnValueList.size());

    columnValueList =
        classificationService.createClassificationQuery().domainIn("").listValues(TYPE, null);
    assertNotNull(columnValueList);
    assertEquals(2, columnValueList.size());

    columnValueList =
        classificationService.createClassificationQuery().domainIn("").listValues(CREATED, null);
    assertNotNull(columnValueList);

    columnValueList =
        classificationService
            .createClassificationQuery()
            .domainIn("")
            .validInDomainEquals(false)
            .listValues(VALID_IN_DOMAIN, null);
    assertNotNull(columnValueList);
    assertEquals(1, columnValueList.size()); // all are false in ""
  }

  @Test
  void testFindClassificationsByCategoryAndDomain() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .categoryIn("MANUAL")
            .domainIn("DOMAIN_A")
            .list();

    assertNotNull(classificationSummaryList);
    assertEquals(2, classificationSummaryList.size());
  }

  @Test
  void testGetOneClassificationForMultipleDomains() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("L10000")
            .domainIn("DOMAIN_A", "DOMAIN_B", "")
            .list();

    assertNotNull(classifications);
    assertEquals(2, classifications.size());
  }

  @Test
  void testGetClassificationsForTypeAndParent() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .typeIn("TASK", "DOCUMENT")
            .parentIdIn("")
            .list();

    assertNotNull(classifications);
    assertEquals(28, classifications.size());

    List<ClassificationSummary> documentTypes =
        classifications.stream()
            .filter(c -> c.getType().equals("DOCUMENT"))
            .collect(Collectors.toList());
    assertEquals(2, documentTypes.size());

    List<ClassificationSummary> taskTypes =
        classifications.stream()
            .filter(c -> c.getType().equals("TASK"))
            .collect(Collectors.toList());
    assertEquals(26, taskTypes.size());
  }

  @Test
  void testGetClassificationsForKeyAndCategories() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("T2100", "L10000")
            .categoryIn("EXTERNAL", "MANUAL")
            .list();

    assertNotNull(classifications);
    assertEquals(5, classifications.size());

    List<ClassificationSummary> externCategory =
        classifications.stream()
            .filter(c -> c.getCategory().equals("EXTERNAL"))
            .collect(Collectors.toList());
    assertEquals(2, externCategory.size());

    List<ClassificationSummary> manualCategory =
        classifications.stream()
            .filter(c -> c.getCategory().equals("MANUAL"))
            .collect(Collectors.toList());
    assertEquals(3, manualCategory.size());
  }

  @Test
  void testGetClassificationsWithParentId() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL")
            .parentIdIn("CLI:100000000000000000000000000000000014")
            .list();

    assertNotNull(classifications);
    assertEquals(1, classifications.size());

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
    assertNotNull(classifications);
    assertEquals(2, classifications.size());
  }

  @Test
  void testGetClassificationsWithParentKey() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL")
            .parentKeyIn("L10000")
            .list();

    assertNotNull(classifications);
    assertEquals(2, classifications.size());

    classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL", "AUTOMATIC")
            .parentKeyIn("L10000", "T2100", "T6310")
            .domainIn("DOMAIN_A")
            .list();
    assertNotNull(classifications);
    assertEquals(2, classifications.size());
  }

  @Test
  void testGetClassificationsWithCustom1() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("1", "VNR,RVNR,KOLVNR", "VNR")
            .domainIn("DOMAIN_A")
            .list();
    assertNotNull(classifications);
    assertEquals(14, classifications.size());
  }

  @Test
  void testGetClassificationsWithCustom1Like() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .customAttributeLike("1", "%RVNR%")
            .domainIn("DOMAIN_A")
            .typeIn("TASK")
            .list();
    assertNotNull(classifications);
    assertEquals(13, classifications.size());
  }

  @Test
  void testGetClassificationsWithParentAndCustom2() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .parentIdIn("CLI:100000000000000000000000000000000004")
            .customAttributeIn("2", "TEXT_1", "TEXT_2")
            .list();
    // zwei tests
    assertNotNull(classifications);
    assertEquals(3, classifications.size());
  }

  @Test
  void testFindClassificationsByCreatedTimestamp() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .createdWithin(todaysInterval())
            .list();

    assertNotNull(classificationSummaryList);
    assertEquals(17, classificationSummaryList.size());
  }

  @Test
  void testFindClassificationsByPriorityAndValidInDomain() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> list =
        classificationService
            .createClassificationQuery()
            .validInDomainEquals(Boolean.TRUE)
            .priorityIn(1, 2, 3)
            .list();
    assertEquals(15, list.size());
  }

  @WithAccessId(userName = "businessadmin")
  @Test
  void testFindClassificationByModifiedWithin()
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    String clId = "CLI:200000000000000000000000000000000015";
    classificationService.updateClassification(classificationService.getClassification(clId));
    List<ClassificationSummary> list =
        classificationService
            .createClassificationQuery()
            .modifiedWithin(
                new TimeInterval(
                    classificationService.getClassification(clId).getModified(), Instant.now()))
            .list();
    assertEquals(1, list.size());
    assertEquals(clId, list.get(0).getId());
  }

  @Test
  void testQueryForNameLike() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().nameLike("Dynamik%").list();
    assertEquals(8, results.size());
  }

  @Test
  void testQueryForNameIn() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .nameIn("Widerruf", "OLD-Leistungsfall")
            .list();
    assertEquals(6, results.size());
  }

  @Test
  void testQueryForDescriptionLike() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().descriptionLike("Widerruf%").list();
    assertEquals(9, results.size());
  }

  @Test
  void testQueryForServiceLevelIn() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().serviceLevelIn("P2D").list();
    assertEquals(5, results.size());
  }

  @Test
  void testQueryForServiceLevelLike() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().serviceLevelLike("PT%").list();
    assertEquals(0, results.size());
  }

  @Test
  void testQueryForApplicationEntryPointIn() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .applicationEntryPointIn("specialPoint", "point0815")
            .list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForApplicationEntryPointLike() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .applicationEntryPointLike("point%")
            .list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForCustom1In() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("1", "VNR,RVNR,KOLVNR, ANR", "VNR")
            .list();
    assertEquals(17, results.size());
  }

  @Test
  void testQueryForCustom2In() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("2", "CUSTOM2", "custom2")
            .list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForCustom3In() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("3", "Custom3", "custom3")
            .list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForCustom4In() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeIn("4", "custom4").list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForCustom5In() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeIn("5", "custom5").list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForCustom6In() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeIn("6", "custom6").list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForCustom7In() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("7", "custom7", "custom_7")
            .list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForCustom8In() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .customAttributeIn("8", "custom_8", "custom8")
            .list();
    assertEquals(3, results.size());
  }

  @Test
  void testQueryForCustom2Like() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("2", "cus%").list();
    assertEquals(4, results.size());
  }

  @Test
  void testQueryForCustom3Like() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("3", "cus%").list();
    assertEquals(4, results.size());
  }

  @Test
  void testQueryForCustom4Like() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("4", "cus%").list();
    assertEquals(4, results.size());
  }

  @Test
  void testQueryForCustom5Like() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("5", "cus%").list();
    assertEquals(4, results.size());
  }

  @Test
  void testQueryForCustom6Like() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("6", "cus%").list();
    assertEquals(4, results.size());
  }

  @Test
  void testQueryForCustom7Like() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("7", "cus%").list();
    assertEquals(4, results.size());
  }

  @Test
  void testQueryForCustom8Like() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().customAttributeLike("8", "cus%").list();
    assertEquals(4, results.size());
  }

  @Test
  void testQueryForOrderByKeyAsc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByKey(asc).list();
    assertEquals("A12", results.get(0).getKey());
  }

  @Test
  void testQueryForOrderByParentIdDesc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByDomain(asc)
            .orderByParentId(desc)
            .list();
    assertEquals("CLI:000000000000000000000000000000000020", results.get(0).getParentId());
  }

  @Test
  void testQueryForOrderByParentKeyDesc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByParentKey(desc).list();
    assertEquals("T6310", results.get(0).getParentKey());
  }

  @Test
  void testQueryForOrderByCategoryDesc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByCategory(desc).list();
    assertEquals("MANUAL", results.get(0).getCategory());
  }

  @Test
  void testQueryForOrderByDomainAsc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByDomain(asc).list();
    assertEquals("", results.get(0).getDomain());
  }

  @Test
  void testQueryForOrderByPriorityDesc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByPriority(desc).list();
    assertEquals(999, results.get(0).getPriority());
  }

  @Test
  void testQueryForOrderByNameAsc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByName(asc).list();
    assertEquals("Beratungsprotokoll", results.get(0).getName());
  }

  @Test
  void testQueryForOrderByServiceLevelDesc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().orderByServiceLevel(desc).list();
    assertEquals("P8D", results.get(0).getServiceLevel());
  }

  @Test
  void testQueryForOrderByApplicationEntryPointAsc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByApplicationEntryPoint(asc)
            .orderByName(asc)
            .list();
    assertEquals(
        "CLI:100000000000000000000000000000000007", results.get(results.size() - 6).getId());
  }

  @Test
  void testQueryForOrderByParentKeyAsc() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByParentKey(asc)
            .orderByDomain(asc)
            .list();
    assertEquals(
        "CLI:000000000000000000000000000000000019", results.get(results.size() - 5).getId());
  }

  @Test
  void testQueryForOrderByCustom1Desc() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("1", desc)
            .orderByDomain(asc)
            .orderByServiceLevel(desc)
            .list();
    assertEquals("CLI:000000000000000000000000000000000002", results.get(0).getId());
  }

  @Test
  void testQueryForOrderByCustom2Asc() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("2", asc)
            .orderByName(asc)
            .orderByParentKey(asc)
            .orderByDomain(asc)
            .list();
    assertEquals("CLI:000000000000000000000000000000000002", results.get(0).getId());
  }

  @Test
  void testQueryForOrderByCustom3Desc() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("3", desc)
            .orderByName(asc)
            .list();
    assertEquals("CLI:100000000000000000000000000000000014", results.get(0).getId());
  }

  @Test
  void testQueryForOrderByCustom4Asc() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("4", asc)
            .orderByName(asc)
            .list();
    assertEquals(
        "CLI:100000000000000000000000000000000010", results.get(results.size() - 5).getId());
  }

  @Test
  void testQueryForOrderByCustom5Desc() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("5", desc)
            .orderByName(asc)
            .list();
    assertEquals("CLI:100000000000000000000000000000000011", results.get(0).getId());
  }

  @Test
  void testQueryForOrderByCustom6Asc() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("6", asc)
            .orderByName(asc)
            .list();
    assertEquals(
        "CLI:100000000000000000000000000000000010", results.get(results.size() - 4).getId());
  }

  @Test
  void testQueryForOrderByCustom7Desc() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("7", desc)
            .orderByName(asc)
            .list();
    assertEquals("CLI:100000000000000000000000000000000011", results.get(0).getId());
  }

  @Test
  void testQueryForOrderByCustom8Asc() throws InvalidArgumentException {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .orderByCustomAttribute("8", asc)
            .orderByName(asc)
            .list();
    assertEquals(
        "CLI:100000000000000000000000000000000010", results.get(results.size() - 4).getId());
  }
}
