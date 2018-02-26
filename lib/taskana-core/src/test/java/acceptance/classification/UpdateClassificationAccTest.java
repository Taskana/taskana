package acceptance.classification;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationNotFoundException;
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

    @Test
    public void testUpdateClassification()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException {
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

        // Get and check the new value
        Classification updatedClassification = classificationService.getClassification("T2100", "DOMAIN_A");
        assertNotNull(updatedClassification);
        assertThat(updatedClassification.getName(), equalTo(newName));
        assertThat(updatedClassification.getApplicationEntryPoint(), equalTo(newEntryPoint));
    }

    @Test
    public void testUpdateUnpersistedClassification()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException {
        String newName = "updated Name";
        String newEntryPoint = "updated EntryPoint";
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.newClassification("NO REGISTERED KEY", "OTHER_DOMAIN",
            "DOCUMENT");
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

        try {
            classification = classificationService.getClassification(classification.getKey(),
                classification.getDomain());
            fail("CLASSIFICATION SHOULD BE UNPERSISTED FOR THIS TESTCASE!");
        } catch (ClassificationNotFoundException ex) {
        }

        classificationService.updateClassification(classification);

        // Get and check the new value
        Classification persistedClassification = classificationService.getClassification(classification.getKey(),
            classification.getDomain());
        assertNotNull(persistedClassification);
        assertThat(persistedClassification.getName(), equalTo(newName));
        assertThat(persistedClassification.getApplicationEntryPoint(), equalTo(newEntryPoint));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testUpdateTaskOnClassificationKeyCategoryChange()
        throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException {
        TaskImpl beforeTask = (TaskImpl) taskanaEngine.getTaskService()
            .getTask("TKI:000000000000000000000000000000000000");

        Classification classification = taskanaEngine.getClassificationService()
            .getClassification(beforeTask.getClassificationSummary().getKey(), beforeTask.getDomain());
        classification.setCategory("NEW CATEGORY");
        taskanaEngine.getClassificationService().updateClassification(classification);

        TaskImpl updatedTask = (TaskImpl) taskanaEngine.getTaskService()
            .getTask("TKI:000000000000000000000000000000000000");
        assertThat(updatedTask.getClassificationCategory(), not(equalTo(beforeTask.getClassificationCategory())));
        assertThat(updatedTask.getClassificationSummary().getCategory(),
            not(equalTo(beforeTask.getClassificationSummary().getCategory())));
        assertThat(updatedTask.getClassificationCategory(), equalTo("NEW CATEGORY"));
        assertThat(updatedTask.getClassificationSummary().getCategory(),
            equalTo("NEW CATEGORY"));
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
