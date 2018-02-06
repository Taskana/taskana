package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
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

    @Ignore
    @Test
    public void testFindClassificationsByCategoryAndDomain()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            // .categoryIn("MANUAL")
            // .domainIn("DOMAIN_A")
            .list();

        assertNotNull(classificationSummaryList);
        assertEquals(2, classificationSummaryList.size());
    }

    @Ignore
    @Test
    public void testGetOneClassificationForMultipleDomains()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            // .keyIn("L10000")
            // .domainIn("DOMAIN_A", "DOMAIN_B", "")
            .list();

        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @Ignore
    @Test
    public void testGetClassificationsForTypeAndParent()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            // .typeIn("TASK", "DOCUMENT")
            // .parentClassificationKeyIn("")
            .list();

        assertNotNull(classifications);
        assertEquals(20, classifications.size());

        List<ClassificationSummary> documentTypes = classifications.stream()
            .filter(c -> c.getType().equals("DOCUMENT"))
            .collect(
                Collectors.toList());
        assertEquals(2, documentTypes.size());

        List<ClassificationSummary> taskTypes = classifications.stream()
            .filter(c -> c.getType().equals("TASK"))
            .collect(
                Collectors.toList());
        assertEquals(18, taskTypes.size());
    }

    @Ignore
    @Test
    public void testGetClassificationsForKeyAndCategories()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            // .keyIn("T2100", "L10000")
            // .categoryIn("EXTERN", "MANUAL")
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

    @Ignore
    @Test
    public void testGetClassificationsWithParentKey()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            // .keyIn("A12", "A13")
            // .categoryIn("EXTERN", "MANUAL")
            // .parentClassificationKeyIn("L10000")
            .list();

        assertNotNull(classifications);
        assertEquals(1, classifications.size());

        classifications = classificationService.createClassificationQuery()
            // .keyIn("A12", "A13")
            // .categoryIn("EXTERN", "MANUAL", "AUTOMATIC")
            // .parentClassificationKeyIn("L10000", "T2100", "T6310")
            // .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @Ignore
    @Test
    public void testGetClassificationsWithCustomFields()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            // .custom1In("VNR,RVNR,KOLVNR", "VNR")
            // .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(13, classifications.size());
    }

    @Ignore
    @Test
    public void testGetClassificationsWithCustom1()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            // .custom1In("VNR,RVNR,KOLVNR", "VNR")
            // .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(13, classifications.size());
    }

    @Ignore
    @Test
    public void testGetClassificationsWithCustom1Like()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            // .custom1Like("%RVNR%")
            // .domainIn("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(11, classifications.size());
    }

    @Ignore
    @Test
    public void testGetClassificationsWithParentAndCustom2()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classifications = classificationService.createClassificationQuery()
            // .parentClassificationKeyIn("L11010")
            // .custom2In("TEXT_1")
            .list();
        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
