package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationInUseException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Acceptance test for all "delete classification" scenarios.
 */
public class DeleteClassificationAccTest extends AbstractAccTest {

    private ClassificationService classificationService;

    public DeleteClassificationAccTest() {
        super();
        classificationService = taskanaEngine.getClassificationService();
    }

    @Test
    public void testDeleteClassificationInDomain()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
        classificationService.deleteClassification("L140101", "DOMAIN_A");

        Classification classification = classificationService.getClassification("L140101", "DOMAIN_A");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());

        classification = classificationService.getClassification("L140101", "DOMAIN_A");
        assertTrue(classification.getDomain() == "");
        assertTrue("DOMAIN_A" != classification.getDomain());
    }

    @Test (expected = ClassificationInUseException.class)
    public void testThrowExeptionIfDeleteClassificationWithExistingTasks()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
        classificationService.deleteClassification("L1050", "DOMAIN_A");
    }

    @Test (expected = ClassificationInUseException.class)
    public void testThrowExeptionIfDeleteMasterClassificationWithExistingTasks()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
        classificationService.deleteClassification("L1050", "");
    }

    @Test
    public void testDeleteMasterClassification()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
        classificationService.deleteClassification("L12010", "");

        boolean classificationNotFound = false;
        try {
            classificationService.getClassification("L12010", "DOMAIN_A");
        } catch (ClassificationNotFoundException e) {
            classificationNotFound = true;
        }
        assertTrue(classificationNotFound);
    }

    @Test
    public void testThrowExceptionWhenChildClassificationIsInUseAndRollback()
        throws ClassificationInUseException, NotAuthorizedException, ClassificationNotFoundException {
        boolean classificationInUse = false;
        try {
            classificationService.deleteClassification("L11010", "DOMAIN_A");
        } catch (ClassificationInUseException e) {
            classificationInUse = true;
        }
        assertTrue(classificationInUse);
        classificationService.getClassification("L11010", "DOMAIN_A");

        classificationInUse = false;
        try {
        classificationService.deleteClassification("L11010", "");
        } catch (ClassificationInUseException e) {
            classificationInUse = true;
        }
        assertTrue(classificationInUse);
        classificationService.getClassification("L11010", "");
        classificationService.getClassification("L11010", "DOMAIN_A");
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testThrowClassificationNotFoundIfClassificationNotExists() throws ClassificationNotFoundException, ClassificationInUseException {
        classificationService.deleteClassification("not existing classification key", "");
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testThrowClassificationNotFoundIfClassificationNotExistsInDomain() throws ClassificationNotFoundException, ClassificationInUseException {
        classificationService.deleteClassification("L10000", "DOMAIN_B");
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
