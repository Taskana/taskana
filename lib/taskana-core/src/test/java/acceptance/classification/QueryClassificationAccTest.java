package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Acceptance test for all "get classification" scenarios.
 */
public class QueryClassificationAccTest extends AbstractAccTest {

    public QueryClassificationAccTest() {
        super();
    }

    @Test
    public void testQueryClassificationValuesForColumnName()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<String> columnValueList = classificationService.createClassificationQuery()
            .listValues("NAME", null);
        assertNotNull(columnValueList);
        assertEquals(15, columnValueList.size());

        columnValueList = classificationService.createClassificationQuery()
            .listValues("TYPE", null);
        assertNotNull(columnValueList);
        assertEquals(3, columnValueList.size());

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
    public void testFindClassificationsByCategoryAndDomain()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .categoryIn("MANUAL")
            .domainIn("DOMAIN_A")
            .list();

        assertNotNull(classificationSummaryList);
        assertEquals(2, classificationSummaryList.size());
    }

    @Test
    public void testGetOneClassificationForMultipleDomains()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .keyIn("L10000")
            .domainIn("DOMAIN_A", "DOMAIN_B", "")
            .list();

        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @Test
    public void testGetClassificationsForTypeAndParent()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .typeIn("TASK", "DOCUMENT")
            .parentIdIn("")
            .list();

        assertNotNull(classifications);
        assertEquals(23, classifications.size());

        List<ClassificationSummary> documentTypes = classifications.stream()
            .filter(c -> c.getType().equals("DOCUMENT"))
            .collect(
                Collectors.toList());
        assertEquals(2, documentTypes.size());

        List<ClassificationSummary> taskTypes = classifications.stream()
            .filter(c -> c.getType().equals("TASK"))
            .collect(
                Collectors.toList());
        assertEquals(21, taskTypes.size());
    }

    @Test
    public void testGetClassificationsForKeyAndCategories()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .keyIn("T2100", "L10000")
            .categoryIn("EXTERN", "MANUAL")
            .list();

        assertNotNull(classifications);
        assertEquals(5, classifications.size());

        List<ClassificationSummary> externCategory = classifications.stream()
            .filter(c -> c.getCategory().equals("EXTERN"))
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
    public void testGetClassificationsWithParentKey()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERN", "MANUAL")
            .parentIdIn("CLI:100000000000000000000000000000000014")
            .list();

        assertNotNull(classifications);
        assertEquals(1, classifications.size());

        classifications = classificationService.createClassificationQuery()
            .keyIn("A12", "A13")
            .categoryIn("EXTERN", "MANUAL", "AUTOMATIC")
            .parentIdIn("CLI:100000000000000000000000000000000014", "CLI:100000000000000000000000000000000010",
                "CLI:100000000000000000000000000000000011")
            .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @Test
    public void testGetClassificationsWithCustom1()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .custom1Like("VNR,RVNR,KOLVNR", "VNR")
            .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(13, classifications.size());
    }

    @Test
    public void testGetClassificationsWithCustom1Like()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            .custom1Like("%RVNR%")
            .domainIn("DOMAIN_A")
            .typeIn("TASK")
            .list();
        assertNotNull(classifications);
        assertEquals(11, classifications.size());
    }

    @Test
    public void testGetClassificationsWithParentAndCustom2()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
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
    public void testFindClassificationsByCreatedTimestamp()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .createdWithin(todaysInterval())
            .list();

        assertNotNull(classificationSummaryList);
        assertEquals(16, classificationSummaryList.size());
    }

    @Test
    public void testFindClassificationsByPriorityAndValidInDomain()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .validInDomainEquals(Boolean.TRUE)
            .priorityIn(1, 2, 3)
            .list();
        assertEquals(14, list.size());

    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
