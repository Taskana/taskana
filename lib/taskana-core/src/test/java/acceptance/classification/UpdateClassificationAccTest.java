package acceptance.classification;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationNotFoundException;

/**
 * Acceptance test for all "update classification" scenarios.
 */
public class UpdateClassificationAccTest extends AbstractAccTest {

    public UpdateClassificationAccTest() {
        super();
    }

    @Ignore
    @Test
    public void testUpdateClassification() throws SQLException, ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
        classification.setApplicationEntryPoint("testApplicationEntryPoint");
        classification.setCategory("PROCESS");
        classification.setCustom1("newCustom1");
        classification.setCustom2("newCustom2");
        classification.setCustom3("newCustom3");
        classification.setCustom4("newCustom4");
        classification.setCustom5("newCustom5");
        classification.setCustom6("newCustom6");
        classification.setCustom7("newCustom7");
        classification.setCustom8("newCustom8");
        classification.setDescription("newDescription");
        classification.setIsValidInDomain(false);
        classification.setName("newName");
        classification.setParentClassificationKey("T2000");
        classification.setPriority(1000);
        classification.setServiceLevel("newServiceLevel");
        // setType and setDomain must be made impossible! (remove setter from interface)
        // classification.setType("DOCUMENT");
        // classification.setDomain("DOMAIN_B");
        // Classification updatedClassification = classificationService.updateClassification(classification);

        // assertNotNull(updatedClassification);
        // assertNotEquals(classification, updatedClassification);
        // assert values are set properly

        // rename ...WithKey to simply getAllClassifications
        List<Classification> classifications = classificationService.getAllClassificationsWithKey("T6310", "DOMAIN_A");
        assertEquals(2, classifications.size());
        // assert that the old one has the correct valid until information

    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
