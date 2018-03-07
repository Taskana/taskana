package acceptance.classification;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "update classification" scenarios.
 */
@RunWith(JAASRunner.class)
public class UpdateClassificationAccTest extends AbstractAccTest {

    public UpdateClassificationAccTest() {
        super();
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"businessadmin"})
    @Test
    public void testUpdateClassification()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException {
        String newName = "updated Name";
        String newEntryPoint = "updated EntryPoint";
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
        Instant createdBefore = classification.getCreated();
        Instant modifiedBefore = classification.getModified();

        classification.setApplicationEntryPoint(newEntryPoint);
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
        classification.setName(newName);
        classification.setParentId("CLI:100000000000000000000000000000000004");
        classification.setPriority(1000);
        classification.setServiceLevel("P2DT3H4M");

        classificationService.updateClassification(classification);

        // Get and check the new value
        Classification updatedClassification = classificationService.getClassification("T2100", "DOMAIN_A");
        assertNotNull(updatedClassification);
        assertThat(updatedClassification.getName(), equalTo(newName));
        assertThat(updatedClassification.getApplicationEntryPoint(), equalTo(newEntryPoint));
        assertThat(updatedClassification.getCreated(), equalTo(createdBefore));
        assertTrue(modifiedBefore.isBefore(updatedClassification.getModified()));
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUpdateClassificationFails()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException {
        String newName = "updated Name";
        String newEntryPoint = "updated EntryPoint";
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");

        classification.setApplicationEntryPoint(newEntryPoint);
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
        classification.setName(newName);
        classification.setParentId("T2000");
        classification.setPriority(1000);
        classification.setServiceLevel("P2DT3H4M");

        classificationService.updateClassification(classification);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test
    public void testUpdateTaskOnClassificationKeyCategoryChange()
        throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException {
        TaskImpl beforeTask = (TaskImpl) taskanaEngine.getTaskService()
            .getTask("TKI:000000000000000000000000000000000000");

        Classification classification = taskanaEngine.getClassificationService()
            .getClassification(beforeTask.getClassificationSummary().getKey(), beforeTask.getDomain());
        classification.setCategory("NEW CATEGORY");
        Instant createdBefore = classification.getCreated();
        Instant modifiedBefore = classification.getModified();
        classification = taskanaEngine.getClassificationService().updateClassification(classification);

        TaskImpl updatedTask = (TaskImpl) taskanaEngine.getTaskService()
            .getTask("TKI:000000000000000000000000000000000000");
        assertThat(updatedTask.getClassificationCategory(), not(equalTo(beforeTask.getClassificationCategory())));
        assertThat(updatedTask.getClassificationSummary().getCategory(),
            not(equalTo(beforeTask.getClassificationSummary().getCategory())));
        assertThat(updatedTask.getClassificationCategory(), equalTo("NEW CATEGORY"));
        assertThat(updatedTask.getClassificationSummary().getCategory(),
            equalTo("NEW CATEGORY"));

        assertThat(classification.getCreated(), equalTo(createdBefore));
        assertTrue(modifiedBefore.isBefore(classification.getModified()));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = ConcurrencyException.class)
    public void testUpdateClassificationNotLatestAnymore()
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException, InterruptedException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification base = classificationService.getClassification("T2100", "DOMAIN_A");
        Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");

        // UPDATE BASE
        base.setApplicationEntryPoint("SOME CHANGED POINT");
        base.setDescription("AN OTHER DESCRIPTION");
        base.setName("I AM UPDATED");
        Thread.sleep(20); // to avoid identity of modified timestamps between classification and base
        classificationService.updateClassification(base);

        classification.setName("NOW IT´S MY TURN");
        classification.setDescription("IT SHOULD BE TO LATE...");
        classificationService.updateClassification(classification);
        fail("The Classification should not be updated, because it was modified while editing.");
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = ClassificationNotFoundException.class)
    public void testUpdateClassificationParentToInvalid()
        throws NotAuthorizedException, ClassificationNotFoundException,
        ConcurrencyException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
        classification.setParentId("ID WHICH CANT BE FOUND");
        classification = classificationService.updateClassification(classification);
    }

}
