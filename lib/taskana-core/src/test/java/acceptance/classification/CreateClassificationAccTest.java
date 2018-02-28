package acceptance.classification;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
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
    public void testCreateRootClassification()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException, NotAuthorizedException {
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
    public void testCreateClassificationWithRootCopy()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException, NotAuthorizedException {
        long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();
        Classification classification = classificationService.newClassification("Key1", "Dummy-Domain", "TASK");
        classification.setIsValidInDomain(true);
        classification = classificationService.createClassification(classification);

        // Check returning one is the "original"
        assertNotNull(classification.getId());
        assertNotNull(classification.getCreated());
        assertNotNull(classification.getModified());
        assertThat(classification.getIsValidInDomain(), equalTo(true));
        assertThat(classification.getDomain(), equalTo("Dummy-Domain"));

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

        // Check root-copy
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
        throws ClassificationAlreadyExistException, NotAuthorizedException {
        long amountOfClassificationsBefore = classificationService.createClassificationQuery().count();

        // Check key NULL
        try {
            Classification classification = classificationService.newClassification(null, "Dummy-Domain", "TASK");
            classification = classificationService.createClassification(classification);
        } catch (IllegalStateException e) {
            // nothing to do
        }

        // Check invalid ServiceLevel
        try {
            Classification classification = classificationService.newClassification("Key2", "Domain", "TASK");
            classification.setServiceLevel("abc");
            classification = classificationService.createClassification(classification);
        } catch (IllegalArgumentException e) {
            // nothing to do
        }

        // Check domain NULL will be replaced ""
        Classification classification = classificationService.newClassification("Key2", null, "TASK");
        classification = classificationService.createClassification(classification);
        long amountOfClassificationsAfter = classificationService.createClassificationQuery().count();
        assertThat(amountOfClassificationsAfter, equalTo(amountOfClassificationsBefore + 1));
        assertThat(classification.getDomain(), equalTo(""));
        assertThat(classification.getParentId(), equalTo(""));
        assertThat(classification.getIsValidInDomain(), equalTo(false));
        assertNotNull(classification.getCreated());
        assertNotNull(classification.getModified());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = ClassificationAlreadyExistException.class)
    public void testCreateClassificationAlreadyExisting()
        throws ClassificationAlreadyExistException, NotAuthorizedException {
        Classification classification = classificationService.newClassification("Key3", "", "TASK");
        classification = classificationService.createClassification(classification);
        classification = classificationService.createClassification(classification);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
