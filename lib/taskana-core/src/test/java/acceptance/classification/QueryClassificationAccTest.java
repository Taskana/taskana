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
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
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
    public void testFindClassificationsByCategoryAndDomain()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<Classification> classificationList = classificationService.createClassificationQuery()
            .category("MANUAL")
            .domain("DOMAIN_A")
            .list();

        assertNotNull(classificationList);
        assertEquals(2, classificationList.size());
    }

    @Test
    public void testGetOneClassificationForMultipleDomains()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<Classification> classifications = classificationService.createClassificationQuery()
            .key("L10000")
            .domain("DOMAIN_A", "DOMAIN_B", "")
            .list();

        assertNotNull(classifications);
        assertEquals(3, classifications.size());
        assertEquals(999, classifications.get(0).getPriority());
        assertEquals(1, classifications.get(1).getPriority());
        assertEquals(1, classifications.get(2).getPriority());
    }

    @Test
    public void testGetClassificationsForTypeAndParent()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<Classification> classifications = classificationService.createClassificationQuery()
            .type("TASK", "DOCUMENT")
            .parentClassificationKey("")
            .list();

        assertNotNull(classifications);
        assertEquals(21, classifications.size());

        List<Classification> documentTypes = classifications.stream()
            .filter(c -> c.getType().equals("DOCUMENT"))
            .collect(
                Collectors.toList());
        assertEquals(2, documentTypes.size());

        List<Classification> taskTypes = classifications.stream().filter(c -> c.getType().equals("TASK")).collect(
            Collectors.toList());
        assertEquals(19, taskTypes.size());
    }

    @Test
    public void testGetClassificationsForKeyAndCategories()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<Classification> classifications = classificationService.createClassificationQuery()
            .key("T2100", "L10000")
            .category("EXTERN", "MANUAL")
            .list();

        assertNotNull(classifications);
        assertEquals(6, classifications.size());

        List<Classification> externCategory = classifications.stream()
            .filter(c -> c.getCategory().equals("EXTERN"))
            .collect(
                Collectors.toList());
        assertEquals(3, externCategory.size());

        List<Classification> manualCategory = classifications.stream()
            .filter(c -> c.getCategory().equals("MANUAL"))
            .collect(
                Collectors.toList());
        assertEquals(3, manualCategory.size());
    }

    @Test
    public void testGetClassificationsWithParentKey()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<Classification> classifications = classificationService.createClassificationQuery()
            .key("A12", "A13")
            .category("EXTERN", "MANUAL")
            .parentClassificationKey("L10000")
            .list();

        assertNotNull(classifications);
        assertEquals(1, classifications.size());

        classifications = classificationService.createClassificationQuery()
            .key("A12", "A13")
            .category("EXTERN", "MANUAL", "AUTOMATIC")
            .parentClassificationKey("L10000", "T2100", "T6310")
            .domain("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(2, classifications.size());
    }

    @Test
    public void testGetClassificationsWithCutsomFields()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<Classification> classifications = classificationService.createClassificationQuery()
            .customFields("VNR,RVNR,KOLVNR", "VNR")
            .domain("DOMAIN_A")
            .list();
        assertNotNull(classifications);
        assertEquals(14, classifications.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
