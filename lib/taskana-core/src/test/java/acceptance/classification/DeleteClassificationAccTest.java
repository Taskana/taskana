package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Acceptance test for all "delete classification" scenarios.
 */
public class DeleteClassificationAccTest extends AbstractAccTest {

    public DeleteClassificationAccTest() {
        super();
    }

    @Ignore
    @Test
    public void testDeleteClassificationInDomain()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        // classificationService.deleteClassification("L140101", "DOMAIN_A");

        Classification classification = classificationService.getClassification("L140101", "DOMAIN_A");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());

        List<ClassificationSummary> classifications = classificationService.getAllClassifications("L140101",
            "DOMAIN_A");
        ClassificationSummary temp = classifications.get(classifications.size() - 1);
        classification = classificationService.getClassification(temp.getKey(), temp.getDomain());
    }

    @Ignore
    @Test
    public void testDeleteClassificationFromRootDomain()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        // classificationService.deleteClassification("L1050", "DOMAIN_A");
        // classificationService.deleteClassification("L1050", "");

        Classification classification = classificationService.getClassification("L140101", "DOMAIN_A");
        assertTrue(classification == null);
    }

    // @Ignore
    // @Test(expected = ClassificationInUseException.class)
    // public void testGetExceptionIfDeletedClassificationFromRootDomainIsStillUsedInDomain()
    // throws SQLException, ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
    // ClassificationService classificationService = taskanaEngine.getClassificationService();
    // classificationService.deleteClassification("L10000", "");
    // }
    //
    // @Ignore
    // @Test(expected = ClassificationInUseException.class)
    // public void testGetExceptionIfDeletedClassificationIsStillUsedInTask()
    // throws SQLException, ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
    // ClassificationService classificationService = taskanaEngine.getClassificationService();
    // classificationService.deleteClassification("L10000", "DOMAIN_A");
    // }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
