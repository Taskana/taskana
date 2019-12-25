package acceptance.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;

/**
 * Acceptance test for all "get classification" scenarios.
 */
class GetClassificationAccTest extends AbstractAccTest {

    private ClassificationService classificationService;

    GetClassificationAccTest() {
        super();
        classificationService = taskanaEngine.getClassificationService();
    }

    @Test
    void testFindAllClassifications() {
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .list();
        assertNotNull(classificationSummaryList);
    }

    @Test
    void testGetOneClassificationByKeyAndDomain() throws ClassificationNotFoundException {
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
    void testGetOneClassificationById() throws ClassificationNotFoundException {
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
    void testGetClassificationWithSpecialCharacter() throws ClassificationNotFoundException {
        Classification classification = classificationService.getClassification(
            "CLI:100000000000000000000000000000000009");
        assertEquals("Zustimmungserklärung", classification.getName());
    }

    @Test
    void testGetClassificationAsSummary() throws ClassificationNotFoundException {
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

    @Test
    void testGetOneClassificationByIdFails() {

        Assertions.assertThrows(ClassificationNotFoundException.class, () ->
            classificationService.getClassification("CLI:100000000470000000000000000000000011"));
    }

    @Test
    void testGetClassificationByNullKeyFails() {
        Assertions.assertThrows(ClassificationNotFoundException.class, () ->
            classificationService.getClassification(null, ""));
    }

    @Test
    void testGetClassificationByInvalidKeyAndDomain() {
        Assertions.assertThrows(ClassificationNotFoundException.class, () ->
            classificationService.getClassification("Key0815", "NOT_EXISTING"));
    }

    @Test
    void testGetOneClassificationForDomainAndGetClassificationFromMasterDomain()
        throws ClassificationNotFoundException {
        Classification classification = classificationService.getClassification("L10000", "DOMAIN_B");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());
        assertEquals(999L, classification.getPriority());
    }

    @Test
    void testGetOneClassificationForMasterDomain() throws ClassificationNotFoundException {
        Classification classification = classificationService.getClassification("L10000", "");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());
        assertEquals(999L, classification.getPriority());
    }
}
