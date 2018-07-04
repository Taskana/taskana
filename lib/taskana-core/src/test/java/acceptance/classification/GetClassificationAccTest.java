package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

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
        assertNotNull(classificationSummaryList);
    }

    @Test
    public void testGetOneClassificationByKeyAndDomain() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T6310", "DOMAIN_A");
        assertNotNull(classification);
        assertEquals("CLI:100000000000000000000000000000000011", classification.getId());
        assertEquals("", classification.getParentId());
        assertEquals("AUTOMATIC", classification.getCategory());
        assertEquals("TASK", classification.getType());
        assertEquals(true, classification.getIsValidInDomain());
        assertEquals("T-GUK Honorarrechnung erstellen", classification.getName());
        assertEquals(2, classification.getPriority());
        assertEquals("P2D", classification.getServiceLevel());
        assertEquals("point0815", classification.getApplicationEntryPoint());
        assertEquals("VNR", classification.getCustom1());
        assertEquals("custom2", classification.getCustom2());
        assertEquals("custom3", classification.getCustom3());
        assertEquals("custom4", classification.getCustom4());
        assertEquals("custom5", classification.getCustom5());
        assertEquals("custom6", classification.getCustom6());
        assertEquals("custom7", classification.getCustom7());
        assertEquals("custom8", classification.getCustom8());
    }

    @Test
    public void testGetOneClassificationById() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService
            .getClassification("CLI:100000000000000000000000000000000011");
        assertNotNull(classification);
        assertEquals("T6310", classification.getKey());
        assertEquals("", classification.getParentId());
        assertEquals("AUTOMATIC", classification.getCategory());
        assertEquals("TASK", classification.getType());
        assertEquals("DOMAIN_A", classification.getDomain());
        assertEquals(true, classification.getIsValidInDomain());
        assertEquals("T-GUK Honorarrechnung erstellen", classification.getName());
        assertEquals(2, classification.getPriority());
        assertEquals("P2D", classification.getServiceLevel());
        assertEquals("point0815", classification.getApplicationEntryPoint());
        assertEquals("VNR", classification.getCustom1());
        assertEquals("custom2", classification.getCustom2());
        assertEquals("custom3", classification.getCustom3());
        assertEquals("custom4", classification.getCustom4());
        assertEquals("custom5", classification.getCustom5());
        assertEquals("custom6", classification.getCustom6());
        assertEquals("custom7", classification.getCustom7());
        assertEquals("custom8", classification.getCustom8());
    }

    @Test
    public void testGetClassificationAsSummary() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        ClassificationSummary classification = classificationService
            .getClassification("CLI:100000000000000000000000000000000011").asSummary();
        assertNotNull(classification);
        assertEquals("T6310", classification.getKey());
        assertEquals("", classification.getParentId());
        assertEquals("AUTOMATIC", classification.getCategory());
        assertEquals("TASK", classification.getType());
        assertEquals("DOMAIN_A", classification.getDomain());
        assertEquals("T-GUK Honorarrechnung erstellen", classification.getName());
        //assertEquals("Generali Unterstützungskasse Honorar wird fällig", classification.getDescription());
        assertEquals(2, classification.getPriority());
        assertEquals("P2D", classification.getServiceLevel());
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testGetOneClassificationByIdFails() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService
            .getClassification("CLI:100000000470000000000000000000000011");
        fail("ClassificationNotFoundException was expected");
    }

    @Test
    public void testGetOneClassificationForDomainAndGetClassificationFromMasterDomain()
        throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("L10000", "DOMAIN_B");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());
        assertEquals(999L, classification.getPriority());
    }

    @Test
    public void testGetOneClassificationForMasterDomain() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("L10000", "");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());
        assertEquals(999L, classification.getPriority());
    }
}
