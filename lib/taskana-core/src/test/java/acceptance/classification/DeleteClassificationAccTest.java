package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationInUseException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "delete classification" scenarios.
 */
@RunWith(JAASRunner.class)
public class DeleteClassificationAccTest extends AbstractAccTest {

    private ClassificationService classificationService;

    public DeleteClassificationAccTest() {
        super();
        classificationService = taskanaEngine.getClassificationService();
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testDeleteClassificationInDomain()
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
        classificationService.deleteClassification("L140101", "DOMAIN_A");

        Classification classification = classificationService.getClassification("L140101", "DOMAIN_A");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test(expected = NotAuthorizedException.class)
    public void testDeleteClassificationInDomainUserIsNotAuthorized()
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
        classificationService.deleteClassification("L140101", "DOMAIN_A");
        fail("NotAuthorizedException should have been thrown");
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = ClassificationInUseException.class)
    public void testThrowExeptionIfDeleteClassificationWithExistingTasks()
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
        classificationService.deleteClassification("L1050", "DOMAIN_A");
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = ClassificationInUseException.class)
    public void testThrowExeptionIfDeleteMasterClassificationWithExistingTasks()
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {
        classificationService.deleteClassification("L1050", "");
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testDeleteMasterClassification()
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {

        classificationService.deleteClassification("L3060", "");

        boolean classificationNotFound = false;
        try {
            classificationService.getClassification("L3060", "DOMAIN_A");
        } catch (ClassificationNotFoundException e) {
            classificationNotFound = true;
        }
        assertTrue(classificationNotFound);
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test(expected = ClassificationInUseException.class)
    public void testDeleteMasterClassificationWithExistingAttachment()
        throws ClassificationNotFoundException, NotAuthorizedException, ClassificationInUseException {

        classificationService.deleteClassification("L12010", "");

        boolean classificationNotFound = false;
        try {
            classificationService.getClassification("L12010", "DOMAIN_A");
        } catch (ClassificationNotFoundException e) {
            classificationNotFound = true;
        }
        assertTrue(classificationNotFound);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    public void testThrowExceptionWhenChildClassificationIsInUseAndRollback()
        throws NotAuthorizedException, ClassificationNotFoundException {
        boolean classificationInUse = false;
        try {
            classificationService.deleteClassification("L11010", "DOMAIN_A");
        } catch (ClassificationInUseException e) {
            classificationInUse = true;
        }
        Classification rollback = classificationService.getClassification("L11010", "DOMAIN_A");
        assertTrue(classificationInUse);
        assertEquals("DOMAIN_A", rollback.getDomain());

        classificationInUse = false;
        try {
            classificationService.deleteClassification("L11010", "");
        } catch (ClassificationInUseException e) {
            classificationInUse = true;
        }
        Classification rollbackMaster = classificationService.getClassification("L11010", "");
        Classification rollbackA = classificationService.getClassification("L11010", "DOMAIN_A");
        assertTrue(classificationInUse);
        assertEquals(rollbackMaster.getKey(), rollbackA.getKey());
        assertNotEquals(rollbackMaster.getDomain(), rollbackA.getDomain());
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test(expected = ClassificationNotFoundException.class)
    public void testThrowClassificationNotFoundIfClassificationNotExists()
        throws ClassificationNotFoundException, ClassificationInUseException, NotAuthorizedException {
        classificationService.deleteClassification("not existing classification key", "");
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test(expected = ClassificationNotFoundException.class)
    public void testThrowClassificationNotFoundIfClassificationNotExistsInDomain()
        throws ClassificationNotFoundException, ClassificationInUseException, NotAuthorizedException {
        classificationService.deleteClassification("L10000", "DOMAIN_B");
    }

}
