package acceptance.classification;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.impl.DaysToWorkingDaysConverter;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.jobs.JobRunner;
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
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
        InvalidArgumentException {
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
        classification.setParentKey("L11010");
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
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
        InvalidArgumentException {
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
        throws Exception {
        setupTest();
        TaskImpl beforeTask = (TaskImpl) taskanaEngine.getTaskService()
            .getTask("TKI:000000000000000000000000000000000000");

        Classification classification = taskanaEngine.getClassificationService()
            .getClassification(beforeTask.getClassificationSummary().getKey(), beforeTask.getDomain());
        classification.setCategory("PROCESS");
        Instant createdBefore = classification.getCreated();
        Instant modifiedBefore = classification.getModified();
        classification = taskanaEngine.getClassificationService().updateClassification(classification);

        TaskImpl updatedTask = (TaskImpl) taskanaEngine.getTaskService()
            .getTask("TKI:000000000000000000000000000000000000");
        assertThat(updatedTask.getClassificationCategory(), not(equalTo(beforeTask.getClassificationCategory())));
        assertThat(updatedTask.getClassificationSummary().getCategory(),
            not(equalTo(beforeTask.getClassificationSummary().getCategory())));
        assertThat(updatedTask.getClassificationCategory(), equalTo("PROCESS"));
        assertThat(updatedTask.getClassificationSummary().getCategory(),
            equalTo("PROCESS"));

        assertThat(classification.getCreated(), equalTo(createdBefore));
        assertTrue(modifiedBefore.isBefore(classification.getModified()));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = ConcurrencyException.class)
    public void testUpdateClassificationNotLatestAnymore()
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException, InterruptedException,
        InvalidArgumentException {
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
    public void testUpdateClassificationParentIdToInvalid()
        throws NotAuthorizedException, ClassificationNotFoundException,
        ConcurrencyException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
        classification.setParentId("ID WHICH CANT BE FOUND");
        classification = classificationService.updateClassification(classification);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "businessadmin"})
    @Test(expected = ClassificationNotFoundException.class)
    public void testUpdateClassificationParentKeyToInvalid()
        throws NotAuthorizedException, ClassificationNotFoundException,
        ConcurrencyException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
        classification.setParentKey("KEY WHICH CANT BE FOUND");
        classification = classificationService.updateClassification(classification);
    }

    @WithAccessId(
        userName = "dummy",
        groupNames = {"admin"})
    @Test
    public void testUpdateClassificationPrioServiceLevel()
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
        InterruptedException, TaskNotFoundException, InvalidArgumentException {
        String newEntryPoint = "updated EntryPoint";
        Instant before = Instant.now();
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService
            .getClassification("CLI:100000000000000000000000000000000003");
        Instant createdBefore = classification.getCreated();
        Instant modifiedBefore = classification.getModified();
        classification.setPriority(1000);
        classification.setServiceLevel("P15D");

        classificationService.updateClassification(classification);
        Thread.sleep(100);
        JobRunner runner = new JobRunner(taskanaEngine);
        // need to run jobs twice, since the first job creates a second one.
        runner.runJobs();
        runner.runJobs();
        // Get and check the new value
        Classification updatedClassification = classificationService
            .getClassification("CLI:100000000000000000000000000000000003");
        assertNotNull(updatedClassification);

        assertTrue(!modifiedBefore.isAfter(updatedClassification.getModified()));
        List<String> affectedTasks = new ArrayList<>(
            Arrays.asList("TKI:000000000000000000000000000000000003", "TKI:000000000000000000000000000000000004"));
        // TODO - resume old behaviour after attachment query is possible.
        // List<String> affectedTasks = new ArrayList<>(
        // Arrays.asList("TKI:000000000000000000000000000000000000", "TKI:000000000000000000000000000000000003",
        // "TKI:000000000000000000000000000000000004", "TKI:000000000000000000000000000000000005",
        // "TKI:000000000000000000000000000000000006", "TKI:000000000000000000000000000000000007",
        // "TKI:000000000000000000000000000000000008", "TKI:000000000000000000000000000000000009",
        // "TKI:000000000000000000000000000000000010", "TKI:000000000000000000000000000000000011",
        // "TKI:000000000000000000000000000000000012", "TKI:000000000000000000000000000000000013",
        // "TKI:000000000000000000000000000000000014", "TKI:000000000000000000000000000000000015",
        // "TKI:000000000000000000000000000000000016", "TKI:000000000000000000000000000000000017",
        // "TKI:000000000000000000000000000000000018", "TKI:000000000000000000000000000000000019",
        // "TKI:000000000000000000000000000000000020", "TKI:000000000000000000000000000000000021",
        // "TKI:000000000000000000000000000000000022", "TKI:000000000000000000000000000000000023",
        // "TKI:000000000000000000000000000000000024", "TKI:000000000000000000000000000000000025",
        // "TKI:000000000000000000000000000000000026", "TKI:000000000000000000000000000000000027",
        // "TKI:000000000000000000000000000000000028", "TKI:000000000000000000000000000000000029",
        // "TKI:000000000000000000000000000000000030", "TKI:000000000000000000000000000000000031",
        // "TKI:000000000000000000000000000000000032", "TKI:000000000000000000000000000000000033",
        // "TKI:000000000000000000000000000000000034", "TKI:000000000000000000000000000000000035",
        // "TKI:000000000000000000000000000000000053", "TKI:000000000000000000000000000000000054",
        // "TKI:000000000000000000000000000000000055", "TKI:000000000000000000000000000000000100",
        // "TKI:000000000000000000000000000000000101", "TKI:000000000000000000000000000000000102",
        // "TKI:000000000000000000000000000000000103"));
        // List<String> indirectlyAffectedTasks = new ArrayList<>(Arrays.asList(
        // "TKI:000000000000000000000000000000000000", "TKI:000000000000000000000000000000000053",
        // "TKI:000000000000000000000000000000000054", "TKI:000000000000000000000000000000000055"));

        TaskService taskService = taskanaEngine.getTaskService();

        DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter
            .initialize(Collections.singletonList(new TimeIntervalColumnHeader(0)), Instant.now());

        for (String taskId : affectedTasks) {
            Task task = taskService.getTask(taskId);
            assertTrue("Task " + task.getId() + " has not been refreshed.", task.getModified().isAfter(before));
            assertTrue(task.getPriority() == 1000);
            // the following excluded tasks are affected via attachments. The task or an attachment still has a service
            // level below 15 days
            // therefore, we cannot compare the due date of these tasks to an SL of 15 days.
            if (taskId.equals("TKI:000000000000000000000000000000000008")) {
                long calendarDays = converter.convertWorkingDaysToDays(task.getPlanned(), 8);
                assertTrue(task.getDue().equals(task.getPlanned().plus(Duration.ofDays(calendarDays))));
                // } else if (indirectlyAffectedTasks.contains(taskId)) {
                // long calendarDays = converter.convertWorkingDaysToDays(task.getPlanned(), 1);
                // assertTrue(task.getDue().equals(task.getPlanned().plus(Duration.ofDays(calendarDays))));
            } else {
                long calendarDays = converter.convertWorkingDaysToDays(task.getPlanned(), 15);
                assertTrue(task.getDue().equals(task.getPlanned().plus(Duration.ofDays(calendarDays))));
            }

        }
    }

}
