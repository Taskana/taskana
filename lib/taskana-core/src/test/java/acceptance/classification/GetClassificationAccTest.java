package acceptance.classification;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;

/**
 * Acceptance test for all "get classification" scenarios.
 */
public class GetClassificationAccTest extends AbstractAccTest {

    public GetClassificationAccTest() {
        super();
    }

    @Test
    public void testFindAllClassifications() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .list();
        Assert.assertNotNull(classificationSummaryList);
    }

    @Test
    public void testGetOneClassificationForDomain() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T6310", "DOMAIN_A");
        Assert.assertNotNull(classification);
    }

    @Test
    public void testGetOneClassificationById() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService
            .getClassification("CLI:100000000000000000000000000000000011");
        Assert.assertNotNull(classification);
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testGetOneClassificationByIdFails() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService
            .getClassification("CLI:100000000470000000000000000000000011");
        Assert.fail("ClassificationNotFoundException was expected");
    }

    @Test
    public void testGetOneClassificationForDomainAndGetClassificationFromRootDomain()
        throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("L10000", "DOMAIN_B");
        Assert.assertNotNull(classification);
        Assert.assertEquals("", classification.getDomain());
        Assert.assertEquals(999L, classification.getPriority());
    }

    @Test
    public void testGetOneClassificationForRootDomain() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("L10000", "");
        Assert.assertNotNull(classification);
        Assert.assertEquals("", classification.getDomain());
        Assert.assertEquals(999L, classification.getPriority());
    }
}
