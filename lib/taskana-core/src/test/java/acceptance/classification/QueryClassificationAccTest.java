package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.TimeInterval;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "get classification" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryClassificationAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryClassificationAccTest() {
        super();
    }

    @Test
    public void testQueryClassificationValuesForColumnName() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<String> columnValueList = classificationService.createClassificationQuery()
            .listValues("NAME", null);
        assertNotNull(columnValueList);
        assertEquals(15, columnValueList.size());

        columnValueList = classificationService.createClassificationQuery()
            .listValues("TYPE", null);
        assertNotNull(columnValueList);
        assertEquals(2, columnValueList.size());

        columnValueList = classificationService.createClassificationQuery()
            .domainIn("")
            .listValues("TYPE", null);
        assertNotNull(columnValueList);
        assertEquals(2, columnValueList.size());

        columnValueList = classificationService.createClassificationQuery()
            .domainIn("")
            .listValues("CREATED", null);
        assertNotNull(columnValueList);

        columnValueList = classificationService.createClassificationQuery()
            .domainIn("")
            .validInDomainEquals(false)
            .listValues("VALID_IN_DOMAIN", null);
        assertNotNull(columnValueList);
        assertEquals(1, columnValueList.size());    // all are false in ""
    }

    @Test
    public void testFindClassificationsByCategoryAndDomain() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .categoryIn("MANUAL")
            .domainIn("DOMAIN_A")
            .list();

        assertNotNull(classificationSummaryList);
        assertEquals(2, classificationSummaryList.size());
    }

    @Test
    public void testGetOneClassificationForMultipleDomains() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .keyIn("L10000")
            .domainIn("DOMAIN_A", "DOMAIN_B", "")
            .list();

        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @Test
    public void testGetClassificationsForTypeAndParent() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .typeIn("TASK", "DOCUMENT")
            .parentIdIn("")
            .list();

        assertNotNull(classifications);
        assertEquals(25, classifications.size());

        List<ClassificationSummary> documentTypes = classifications.stream()
            .filter(c -> c.getType().equals("DOCUMENT"))
            .collect(
                Collectors.toList());
        assertEquals(2, documentTypes.size());

        List<ClassificationSummary> taskTypes = classifications.stream()
            .filter(c -> c.getType().equals("TASK"))
            .collect(
                Collectors.toList());
        assertEquals(23, taskTypes.size());
    }

    @Test
    public void testGetClassificationsForKeyAndCategories() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .keyIn("T2100", "L10000")
            .categoryIn("EXTERNAL", "MANUAL")
            .list();

        assertNotNull(classifications);
        assertEquals(5, classifications.size());

        List<ClassificationSummary> externCategory = classifications.stream()
            .filter(c -> c.getCategory().equals("EXTERNAL"))
            .collect(
                Collectors.toList());
        assertEquals(2, externCategory.size());

        List<ClassificationSummary> manualCategory = classifications.stream()
            .filter(c -> c.getCategory().equals("MANUAL"))
            .collect(
                Collectors.toList());
        assertEquals(3, manualCategory.size());
    }

    @Test
    public void testGetClassificationsWithParentId() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL")
            .parentIdIn("CLI:100000000000000000000000000000000014")
            .list();

        assertNotNull(classifications);
        assertEquals(1, classifications.size());

        classifications = classificationService.createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL", "AUTOMATIC")
            .parentIdIn("CLI:100000000000000000000000000000000014", "CLI:100000000000000000000000000000000010",
                "CLI:100000000000000000000000000000000011")
            .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @Test
    public void testGetClassificationsWithParentKey() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL")
            .parentKeyIn("L10000")
            .list();

        assertNotNull(classifications);
        assertEquals(1, classifications.size());

        classifications = classificationService.createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERNAL", "MANUAL", "AUTOMATIC")
            .parentKeyIn("L10000", "T2100", "T6310")
            .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @Test
    public void testGetClassificationsWithCustom1() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .custom1Like("VNR,RVNR,KOLVNR", "VNR")
            .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(14, classifications.size());
    }

    @Test
    public void testGetClassificationsWithCustom1Like() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .custom1Like("%RVNR%")
            .domainIn("DOMAIN_A")
            .typeIn("TASK")
            .list();
        assertNotNull(classifications);
        assertEquals(13, classifications.size());
    }

    @Test
    public void testGetClassificationsWithParentAndCustom2() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .parentIdIn("CLI:100000000000000000000000000000000004")
            .custom2Like("TEXT_1", "TEXT_2")
            .list();
        // zwei tests
        assertNotNull(classifications);
        assertEquals(3, classifications.size());
    }

    @Test
    public void testFindClassificationsByCreatedTimestamp() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .createdWithin(todaysInterval())
            .list();

        assertNotNull(classificationSummaryList);
        assertEquals(17, classificationSummaryList.size());
    }

    @Test
    public void testFindClassificationsByPriorityAndValidInDomain() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .validInDomainEquals(Boolean.TRUE)
            .priorityIn(1, 2, 3)
            .list();
        assertEquals(14, list.size());

    }

    @WithAccessId(
        userName = "businessadmin")
    @Test
    public void testFindClassificationByModifiedWithin()
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        String clId = "CLI:200000000000000000000000000000000015";
        classificationService.updateClassification(classificationService.getClassification(clId));
        List<ClassificationSummary> list = classificationService.createClassificationQuery()
                .modifiedWithin(new TimeInterval(
                        classificationService.getClassification(clId).getModified(),
                        Instant.now()))
                .list();
        assertEquals(1, list.size());
        assertEquals(clId, list.get(0).getId());
    }

    @Test
    public void testQueryForNameLike() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .nameLike("Dynamik%")
                .list();
        assertEquals(8, results.size());
    }

    @Test
    public void testQueryForNameIn() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .nameIn("Widerruf", "OLD-Leistungsfall")
                .list();
        assertEquals(5, results.size());
    }

    @Test
    public void testQueryForDescriptionLike() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .descriptionLike("Widerruf%")
                .list();
        assertEquals(8, results.size());
    }

    @Test
    public void testQueryForServiceLevelIn() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .serviceLevelIn("P2D")
                .list();
        assertEquals(5, results.size());
    }

    @Test
    public void testQueryForServiceLevelLike() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .serviceLevelLike("PT%")
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void testQueryForApplicationEntryPointIn() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .applicationEntryPointIn("specialPoint", "point0815")
                .list();
        assertEquals(3, results.size());
    }

    @Test
    public void testQueryForApplicationEntryPointLike() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .applicationEntryPointLike("point%")
                .list();
        assertEquals(3, results.size());
    }

    @Test
    public void testQueryForCustom1In() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom1In("VNR,RVNR,KOLVNR, ANR", "VNR")
                .list();
        assertEquals(13, results.size());
    }

    @Test
    public void testQueryForCustom2In() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom2In("CUSTOM2", "custom2")
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void testQueryForCustom3In() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom3In("Custom3", "custom3")
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void testQueryForCustom4In() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom4In("custom4")
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void testQueryForCustom5In() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom5In("custom5")
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void testQueryForCustom6In() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom6In("custom6")
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void testQueryForCustom7In() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom7In("custom7", "custom_7")
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void testQueryForCustom8In() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom8In("custom_8", "custom8")
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void testQueryForCustom3Like() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom3Like("cus%")
                .list();
        assertEquals(3, results.size());
    }

    @Test
    public void testQueryForCustom4Like() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom4Like("cus%")
                .list();
        assertEquals(3, results.size());
    }

    @Test
    public void testQueryForCustom5Like() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom5Like("cus%")
                .list();
        assertEquals(3, results.size());
    }

    @Test
    public void testQueryForCustom6Like() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom6Like("cus%")
                .list();
        assertEquals(3, results.size());
    }

    @Test
    public void testQueryForCustom7Like() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom7Like("cus%")
                .list();
        assertEquals(3, results.size());
    }

    @Test
    public void testQueryForCustom8Like() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .custom8Like("cus%")
                .list();
        assertEquals(3, results.size());
    }

    @Test
    public void testQueryForOrderByKeyAsc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByKey(asc)
                .list();
        assertEquals("A12", results.get(0).getKey());
    }

    @Test
    public void testQueryForOrderByParentIdDesc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByParentId(desc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000015", results.get(0).getParentId());
    }

    @Test
    public void testQueryForOrderByParentKeyDesc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByParentId(desc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000015", results.get(0).getParentId());
    }

    @Test
    public void testQueryForOrderByCategoryDesc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCategory(desc)
                .list();
        assertEquals("MANUAL", results.get(0).getCategory());
    }

    @Test
    public void testQueryForOrderByDomainAsc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByDomain(asc)
                .list();
        assertEquals("", results.get(0).getDomain());
    }

    @Test
    public void testQueryForOrderByPriorityDesc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByPriority(desc)
                .list();
        assertEquals(999, results.get(0).getPriority());
    }

    @Test
    public void testQueryForOrderByNameAsc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByName(asc)
                .list();
        assertEquals("Beratungsprotokoll", results.get(0).getName());
    }

    @Test
    public void testQueryForOrderByServiceLevelDesc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByServiceLevel(desc)
                .list();
        assertEquals("PT7H", results.get(0).getServiceLevel());
    }

    @Test
    public void testQueryForOrderByApplicationEntryPointAsc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByApplicationEntryPoint(asc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000007", results.get(results.size() - 5).getId());
    }

    @Test
    public void testQueryForOrderByParentKeyAsc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByParentKey(asc)
                .list();
        assertEquals("CLI:000000000000000000000000000000000001", results.get(0).getId());
    }

    @Test
    public void testQueryForOrderByCustom1Desc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCustom1(desc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:000000000000000000000000000000000002", results.get(0).getId());
    }

    @Test
    public void testQueryForOrderByCustom2Asc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCustom2(asc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:000000000000000000000000000000000002", results.get(0).getId());
    }

    @Test
    public void testQueryForOrderByCustom3Desc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCustom3(desc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000014", results.get(0).getId());
    }

    @Test
    public void testQueryForOrderByCustom4Asc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCustom4(asc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000010", results.get(results.size() - 4).getId());
    }

    @Test
    public void testQueryForOrderByCustom5Desc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCustom5(desc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000011", results.get(0).getId());
    }

    @Test
    public void testQueryForOrderByCustom6Asc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCustom6(asc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000010", results.get(results.size() - 3).getId());
    }

    @Test
    public void testQueryForOrderByCustom7Desc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCustom7(desc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000011", results.get(0).getId());
    }

    @Test
    public void testQueryForOrderByCustom8Asc() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
                .orderByCustom8(asc)
                .orderByName(asc)
                .list();
        assertEquals("CLI:100000000000000000000000000000000010", results.get(results.size() - 3).getId());
    }
}
