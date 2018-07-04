package acceptance.classification;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "create classification" scenarios.
 */
@RunWith(JAASRunner.class)
public class CreateClassificationAccTest extends AbstractAccTest {

    private ClassificationService classificationService;

    public CreateClassificationAccTest() {
        super();
        classificationService = taskanaEngine.getClassificationService();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    public void testCreateMasterClassification()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
        Classification classification = classificationService.newClassification("Key0", "", "TASK");
        classification.setIsValidInDomain(true);
        classification = classificationService.createClassification(classification);

        // check only 1 created
        long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
        assertThat(amountOfClassificationsAfter, equalTo(amountOfClassificationsBefore + 1));

        classification = classificationService.getClassification(classification.getId());
        assertNotNull(classification);
        assertNotNull(classification.getCreated());
        assertNotNull(classification.getModified());
        assertNotNull(classification.getId());
        assertThat(classification.getIsValidInDomain(), equalTo(false));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    public void testCreateClassificationWithMasterCopy()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
        Classification classification = classificationService.newClassification("Key1", "DOMAIN_A", "TASK");
        classification.setIsValidInDomain(true);
        classification = classificationService.createClassification(classification);

        // Check returning one is the "original"
        Classification createdClassification = classificationService.getClassification(classification.getId());
        assertNotNull(createdClassification.getId());
        assertNotNull(createdClassification.getCreated());
        assertNotNull(createdClassification.getModified());
        assertThat(createdClassification.getIsValidInDomain(), equalTo(true));
        assertThat(createdClassification.getDomain(), equalTo("DOMAIN_A"));

        // Check 2 new created
        long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
        assertThat(amountOfClassificationsAfter, equalTo(amountOfClassificationsBefore + 2));

        // Check main
        classification = classificationService.getClassification(classification.getId());
        assertNotNull(classification);
        assertNotNull(classification.getCreated());
        assertNotNull(classification.getModified());
        assertNotNull(classification.getId());
        assertThat(classification.getIsValidInDomain(), equalTo(true));

        // Check master-copy
        classification = classificationService.getClassification(classification.getKey(), "");
        assertNotNull(classification);
        assertNotNull(classification.getCreated());
        assertNotNull(classification.getModified());
        assertNotNull(classification.getId());
        assertThat(classification.getIsValidInDomain(), equalTo(false));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    public void testCreateClassificationWithInvalidValues()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException {
        long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();

        // Check key NULL
        try {
            Classification classification = classificationService.newClassification(null, "DOMAIN_A", "TASK");
            classification = classificationService.createClassification(classification);
        } catch (InvalidArgumentException e) {
            // nothing to do
        }

        // Check invalid ServiceLevel
        try {
            Classification classification = classificationService.newClassification("Key2", "DOMAIN_B", "TASK");
            classification.setServiceLevel("abc");
            classification = classificationService.createClassification(classification);
        } catch (InvalidArgumentException e) {
            // nothing to do
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = ClassificationAlreadyExistException.class)
    public void testCreateClassificationAlreadyExisting()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
        InvalidArgumentException {
        Classification classification = classificationService.newClassification("Key3", "", "TASK");
        classification = classificationService.createClassification(classification);
        classification = classificationService.createClassification(classification);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = DomainNotFoundException.class)
    public void testCreateClassificationInUnknownDomain()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
        InvalidArgumentException {
        Classification classification = classificationService.newClassification("Key3", "UNKNOWN_DOMAIN", "TASK");
        classification = classificationService.createClassification(classification);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = InvalidArgumentException.class)
    public void testCreateClassificationOfUnknownType()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
        InvalidArgumentException {
        Classification classification = classificationService.newClassification("Key3", "DOMAIN_A", "UNKNOWN_TYPE");
        classification = classificationService.createClassification(classification);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = InvalidArgumentException.class)
    public void testCreateClassificationOfUnknownCategory()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
        InvalidArgumentException {
        Classification classification = classificationService.newClassification("Key4", "DOMAIN_A", "TASK");
        classification.setCategory("UNKNOWN_CATEGORY");
        classification = classificationService.createClassification(classification);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = InvalidArgumentException.class)
    public void testCreateClassificationWithInvalidParentId()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
        InvalidArgumentException {
        Classification classification = classificationService.newClassification("Key5", "", "TASK");
        classification.setParentId("ID WHICH CANT BE FOUND");
        classification = classificationService.createClassification(classification);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = InvalidArgumentException.class)
    public void testCreateClassificationWithInvalidParentKey()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
        InvalidArgumentException {
        Classification classification = classificationService.newClassification("Key5", "", "TASK");
        classification.setParentKey("KEY WHICH CANT BE FOUND");
        classification = classificationService.createClassification(classification);
    }

}
