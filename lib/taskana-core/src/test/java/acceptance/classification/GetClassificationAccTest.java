package acceptance.classification;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Acceptance test for all "get classification" scenarios.
 */
public class GetClassificationAccTest extends AbstractAccTest {

    public GetClassificationAccTest() {
        super();
    }

    @Test
    public void testFindAllClassifications()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.getClassificationTree();
        Assert.assertNotNull(classificationSummaryList);
    }

    @Test
    public void testGetOneClassificationForDomain() throws SQLException, ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T6310", "DOMAIN_A");
        Assert.assertNotNull(classification);
    }

    @Test
    public void testGetOneClassificationForDomainAndGetClassificationFromRootDomain()
        throws SQLException, ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("L10000", "DOMAIN_B");
        Assert.assertNotNull(classification);
        Assert.assertEquals("", classification.getDomain());
        Assert.assertEquals(999L, classification.getPriority());
    }

    @Test
    public void testGetOneClassificationForRootDomain() throws SQLException, ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("L10000", "");
        Assert.assertNotNull(classification);
        Assert.assertEquals("", classification.getDomain());
        Assert.assertEquals(999L, classification.getPriority());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
