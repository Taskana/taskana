package acceptance.classification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.internal.util.DaysToWorkingDaysConverter;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;

/** Acceptance test for all "update classification" scenarios. */
@ExtendWith(JaasExtension.class)
public class UpdateClassificationAccTest extends AbstractAccTest {

  private ClassificationService classificationService;

  public UpdateClassificationAccTest() {
    super();
    classificationService = taskanaEngine.getClassificationService();
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
    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
    final Instant createdBefore = classification.getCreated();
    final Instant modifiedBefore = classification.getModified();

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
    classification.setServiceLevel("P3D");

    classificationService.updateClassification(classification);

    // Get and check the new value
    Classification updatedClassification =
        classificationService.getClassification("T2100", "DOMAIN_A");
    assertNotNull(updatedClassification);
    assertThat(updatedClassification.getName(), equalTo(newName));
    assertThat(updatedClassification.getApplicationEntryPoint(), equalTo(newEntryPoint));
    assertThat(updatedClassification.getCreated(), equalTo(createdBefore));
    assertTrue(modifiedBefore.isBefore(updatedClassification.getModified()));
  }

  @Test
  public void testUpdateClassificationFails()
      throws ClassificationNotFoundException, ConcurrencyException, InvalidArgumentException {
    String newName = "updated Name";
    String newEntryPoint = "updated EntryPoint";
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

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> classificationService.updateClassification(classification));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  public void testUpdateTaskOnClassificationKeyCategoryChange() throws Exception {
    setupTest();
    TaskImpl beforeTask =
        (TaskImpl)
            taskanaEngine.getTaskService().getTask("TKI:000000000000000000000000000000000000");

    Classification classification =
        classificationService.getClassification(
            beforeTask.getClassificationSummary().getKey(), beforeTask.getDomain());
    classification.setCategory("PROCESS");
    final Instant createdBefore = classification.getCreated();
    final Instant modifiedBefore = classification.getModified();
    classification = taskanaEngine.getClassificationService().updateClassification(classification);

    TaskImpl updatedTask =
        (TaskImpl)
            taskanaEngine.getTaskService().getTask("TKI:000000000000000000000000000000000000");
    assertThat(
        updatedTask.getClassificationCategory(),
        not(equalTo(beforeTask.getClassificationCategory())));
    assertThat(
        updatedTask.getClassificationSummary().getCategory(),
        not(equalTo(beforeTask.getClassificationSummary().getCategory())));
    assertThat(updatedTask.getClassificationCategory(), equalTo("PROCESS"));
    assertThat(updatedTask.getClassificationSummary().getCategory(), equalTo("PROCESS"));

    assertThat(classification.getCreated(), equalTo(createdBefore));
    // isBeforeOrEquals in case of too fast execution
    assertTrue(
        modifiedBefore.isBefore(classification.getModified())
            || modifiedBefore.equals(classification.getModified()));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  public void testUpdateClassificationNotLatestAnymore()
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InterruptedException, InvalidArgumentException {
    Classification base = classificationService.getClassification("T2100", "DOMAIN_A");
    final Classification classification =
        classificationService.getClassification("T2100", "DOMAIN_A");

    // UPDATE BASE
    base.setApplicationEntryPoint("SOME CHANGED POINT");
    base.setDescription("AN OTHER DESCRIPTION");
    base.setName("I AM UPDATED");
    Thread.sleep(20); // to avoid identity of modified timestamps between classification and base
    classificationService.updateClassification(base);

    classification.setName("NOW IT´S MY TURN");
    classification.setDescription("IT SHOULD BE TO LATE...");
    Assertions.assertThrows(
        ConcurrencyException.class,
        () -> classificationService.updateClassification(classification),
        "The Classification should not be updated, because it was modified while editing.");
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  public void testUpdateClassificationParentIdToInvalid()
      throws NotAuthorizedException, ClassificationNotFoundException, ConcurrencyException,
          InvalidArgumentException {
    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
    classification.setParentId("ID WHICH CANT BE FOUND");
    Assertions.assertThrows(
        ClassificationNotFoundException.class,
        () -> classificationService.updateClassification(classification));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "businessadmin"})
  @Test
  public void testUpdateClassificationParentKeyToInvalid()
      throws NotAuthorizedException, ClassificationNotFoundException, ConcurrencyException,
          InvalidArgumentException {
    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
    classification.setParentKey("KEY WHICH CANT BE FOUND");

    Assertions.assertThrows(
        ClassificationNotFoundException.class,
        () -> classificationService.updateClassification(classification));
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"admin"})
  @Test
  public void testUpdateClassificationPrioServiceLevel()
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InterruptedException, TaskNotFoundException, InvalidArgumentException {
    String newEntryPoint = "updated EntryPoint";
    final Instant before = Instant.now();
    Classification classification =
        classificationService.getClassification("CLI:100000000000000000000000000000000003");
    final Instant modifiedBefore = classification.getModified();
    classification.setPriority(1000);
    classification.setServiceLevel("P15D");

    updateClassificationAndRunAssociatedJobs(classification);
    // Get and check the new value
    Classification updatedClassification =
        classificationService.getClassification("CLI:100000000000000000000000000000000003");
    assertNotNull(updatedClassification);

    assertFalse(modifiedBefore.isAfter(updatedClassification.getModified()));
    // TODO - resume old behaviour after attachment query is possible.
    TaskService taskService = taskanaEngine.getTaskService();
    DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(true);
    DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(Instant.now());

    List<String> tasksWithP1D =
        new ArrayList<>(
            Arrays.asList(
                "TKI:000000000000000000000000000000000054",
                "TKI:000000000000000000000000000000000055",
                "TKI:000000000000000000000000000000000000",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000053"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithP1D, taskService, converter, 1, 1000);

    List<String> tasksWithP8D =
        new ArrayList<>(Arrays.asList("TKI:000000000000000000000000000000000008"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithP8D, taskService, converter, 8, 1000);

    List<String> tasksWithP14D =
        new ArrayList<>(Arrays.asList("TKI:000000000000000000000000000000000010"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithP14D, taskService, converter, 14, 1000);

    List<String> tasksWithP15D =
        new ArrayList<>(
            Arrays.asList(
                "TKI:000000000000000000000000000000000003",
                "TKI:000000000000000000000000000000000004",
                "TKI:000000000000000000000000000000000005",
                "TKI:000000000000000000000000000000000006",
                "TKI:000000000000000000000000000000000007",
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000012",
                "TKI:000000000000000000000000000000000013",
                "TKI:000000000000000000000000000000000014",
                "TKI:000000000000000000000000000000000015",
                "TKI:000000000000000000000000000000000016",
                "TKI:000000000000000000000000000000000017",
                "TKI:000000000000000000000000000000000018",
                "TKI:000000000000000000000000000000000019",
                "TKI:000000000000000000000000000000000020",
                "TKI:000000000000000000000000000000000021",
                "TKI:000000000000000000000000000000000022",
                "TKI:000000000000000000000000000000000023",
                "TKI:000000000000000000000000000000000024",
                "TKI:000000000000000000000000000000000025",
                "TKI:000000000000000000000000000000000026",
                "TKI:000000000000000000000000000000000027",
                "TKI:000000000000000000000000000000000028",
                "TKI:000000000000000000000000000000000029",
                "TKI:000000000000000000000000000000000030",
                "TKI:000000000000000000000000000000000031",
                "TKI:000000000000000000000000000000000032",
                "TKI:000000000000000000000000000000000033",
                "TKI:000000000000000000000000000000000034",
                "TKI:000000000000000000000000000000000035",
                "TKI:000000000000000000000000000000000100",
                "TKI:000000000000000000000000000000000101",
                "TKI:000000000000000000000000000000000102",
                "TKI:000000000000000000000000000000000103"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithP15D, taskService, converter, 15, 1000);
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  public void testUpdateClassificationWithSameKeyAndParentKey()
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InvalidArgumentException {

    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");

    classification.setParentKey(classification.getKey());
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> classificationService.updateClassification(classification));
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"businessadmin"})
  @Test
  public void testUpdateClassificationWithEmptyServiceLevel()
      throws DomainNotFoundException, ClassificationAlreadyExistException, NotAuthorizedException,
          InvalidArgumentException, ClassificationNotFoundException, ConcurrencyException {

    Classification classification =
        classificationService.newClassification("Key=0818", "DOMAIN_A", "TASK");
    Classification created = classificationService.createClassification(classification);
    created.setServiceLevel("");
    classificationService.updateClassification(created);
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"admin"})
  @Test
  public void testUpdateClassificationChangePriority()
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InterruptedException, TaskNotFoundException, InvalidArgumentException {
    final Instant before = Instant.now();
    Classification classification =
        classificationService.getClassification("CLI:100000000000000000000000000000000003");
    final Instant modifiedBefore = classification.getModified();

    classification.setPriority(99);
    classification.setServiceLevel("P1D");

    updateClassificationAndRunAssociatedJobs(classification);
    // Get and check the new value
    Classification updatedClassification =
        classificationService.getClassification("CLI:100000000000000000000000000000000003");
    assertNotNull(updatedClassification);
    assertFalse(modifiedBefore.isAfter(updatedClassification.getModified()));
    // TODO - resume old behaviour after attachment query is possible.
    TaskService taskService = taskanaEngine.getTaskService();
    DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(true);
    DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(Instant.now());

    List<String> tasksWithPrio99 =
        new ArrayList<>(
            Arrays.asList(
                "TKI:000000000000000000000000000000000003",
                "TKI:000000000000000000000000000000000004",
                "TKI:000000000000000000000000000000000005",
                "TKI:000000000000000000000000000000000006",
                "TKI:000000000000000000000000000000000007",
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000012",
                "TKI:000000000000000000000000000000000013",
                "TKI:000000000000000000000000000000000014",
                "TKI:000000000000000000000000000000000015",
                "TKI:000000000000000000000000000000000016",
                "TKI:000000000000000000000000000000000017",
                "TKI:000000000000000000000000000000000018",
                "TKI:000000000000000000000000000000000019",
                "TKI:000000000000000000000000000000000020",
                "TKI:000000000000000000000000000000000021",
                "TKI:000000000000000000000000000000000022",
                "TKI:000000000000000000000000000000000023",
                "TKI:000000000000000000000000000000000024",
                "TKI:000000000000000000000000000000000025",
                "TKI:000000000000000000000000000000000026",
                "TKI:000000000000000000000000000000000027",
                "TKI:000000000000000000000000000000000028",
                "TKI:000000000000000000000000000000000029",
                "TKI:000000000000000000000000000000000030",
                "TKI:000000000000000000000000000000000031",
                "TKI:000000000000000000000000000000000032",
                "TKI:000000000000000000000000000000000033",
                "TKI:000000000000000000000000000000000034",
                "TKI:000000000000000000000000000000000035",
                "TKI:000000000000000000000000000000000100",
                "TKI:000000000000000000000000000000000101",
                "TKI:000000000000000000000000000000000102",
                "TKI:000000000000000000000000000000000103",
                "TKI:200000000000000000000000000000000007",
                "TKI:000000000000000000000000000000000000",
                "TKI:000000000000000000000000000000000052",
                "TKI:000000000000000000000000000000000053",
                "TKI:000000000000000000000000000000000054",
                "TKI:000000000000000000000000000000000008",
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000010"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio99, taskService, converter, 1, 99);

    List<String> tasksWithPrio101 =
        new ArrayList<>(Arrays.asList("TKI:000000000000000000000000000000000011"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio101, taskService, converter, 1, 101);

    updatedClassification.setPriority(7);
    updateClassificationAndRunAssociatedJobs(updatedClassification);

    List<String> tasksWithPrio7 =
        new ArrayList<>(
            Arrays.asList(
                "TKI:000000000000000000000000000000000003",
                "TKI:000000000000000000000000000000000004",
                "TKI:000000000000000000000000000000000005",
                "TKI:000000000000000000000000000000000006",
                "TKI:000000000000000000000000000000000007",
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000012",
                "TKI:000000000000000000000000000000000013",
                "TKI:000000000000000000000000000000000014",
                "TKI:000000000000000000000000000000000015",
                "TKI:000000000000000000000000000000000016",
                "TKI:000000000000000000000000000000000017",
                "TKI:000000000000000000000000000000000018",
                "TKI:000000000000000000000000000000000019",
                "TKI:000000000000000000000000000000000020",
                "TKI:000000000000000000000000000000000021",
                "TKI:000000000000000000000000000000000022",
                "TKI:000000000000000000000000000000000023",
                "TKI:000000000000000000000000000000000024",
                "TKI:000000000000000000000000000000000025",
                "TKI:000000000000000000000000000000000026",
                "TKI:000000000000000000000000000000000027",
                "TKI:000000000000000000000000000000000028",
                "TKI:000000000000000000000000000000000029",
                "TKI:000000000000000000000000000000000030",
                "TKI:000000000000000000000000000000000031",
                "TKI:000000000000000000000000000000000032",
                "TKI:000000000000000000000000000000000033",
                "TKI:000000000000000000000000000000000034",
                "TKI:000000000000000000000000000000000035",
                "TKI:000000000000000000000000000000000100",
                "TKI:000000000000000000000000000000000101",
                "TKI:000000000000000000000000000000000102",
                "TKI:000000000000000000000000000000000103",
                "TKI:000000000000000000000000000000000000",
                "TKI:000000000000000000000000000000000052",
                "TKI:000000000000000000000000000000000053",
                "TKI:000000000000000000000000000000000054",
                "TKI:000000000000000000000000000000000055",
                "TKI:200000000000000000000000000000000007"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio7, taskService, converter, 1, 7);

    List<String> tasksWithPrio9 =
        new ArrayList<>(Arrays.asList("TKI:000000000000000000000000000000000008"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio9, taskService, converter, 1, 9);

    tasksWithPrio101 = new ArrayList<>(Arrays.asList("TKI:000000000000000000000000000000000011"));

    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio101, taskService, converter, 1, 101);
  }

  @WithAccessId(
      userName = "dummy",
      groupNames = {"admin"})
  @Test
  public void testUpdateClassificationChangeServiceLevel()
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InterruptedException, TaskNotFoundException, InvalidArgumentException {
    final Instant before = Instant.now();
    Classification classification =
        classificationService.getClassification("CLI:100000000000000000000000000000000003");
    final Instant modifiedBefore = classification.getModified();

    classification.setPriority(555);
    classification.setServiceLevel("P12D");

    updateClassificationAndRunAssociatedJobs(classification);
    // Get and check the new value
    Classification updatedClassification =
        classificationService.getClassification("CLI:100000000000000000000000000000000003");
    assertNotNull(updatedClassification);
    assertFalse(modifiedBefore.isAfter(updatedClassification.getModified()));
    // TODO - resume old behaviour after attachment query is possible.
    TaskService taskService = taskanaEngine.getTaskService();
    DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(true);
    DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(Instant.now());
    List<String> tasksWithPD12 =
        new ArrayList<>(
            Arrays.asList(
                "TKI:000000000000000000000000000000000003",
                "TKI:000000000000000000000000000000000004",
                "TKI:000000000000000000000000000000000005",
                "TKI:000000000000000000000000000000000006",
                "TKI:000000000000000000000000000000000007",
                "TKI:000000000000000000000000000000000009",
                "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000012",
                "TKI:000000000000000000000000000000000013",
                "TKI:000000000000000000000000000000000014",
                "TKI:000000000000000000000000000000000015",
                "TKI:000000000000000000000000000000000016",
                "TKI:000000000000000000000000000000000017",
                "TKI:000000000000000000000000000000000018",
                "TKI:000000000000000000000000000000000019",
                "TKI:000000000000000000000000000000000020",
                "TKI:000000000000000000000000000000000021",
                "TKI:000000000000000000000000000000000022",
                "TKI:000000000000000000000000000000000023",
                "TKI:000000000000000000000000000000000024",
                "TKI:000000000000000000000000000000000025",
                "TKI:000000000000000000000000000000000026",
                "TKI:000000000000000000000000000000000027",
                "TKI:000000000000000000000000000000000028",
                "TKI:000000000000000000000000000000000029",
                "TKI:000000000000000000000000000000000030",
                "TKI:000000000000000000000000000000000031",
                "TKI:000000000000000000000000000000000032",
                "TKI:000000000000000000000000000000000033",
                "TKI:000000000000000000000000000000000034",
                "TKI:000000000000000000000000000000000035",
                "TKI:000000000000000000000000000000000100",
                "TKI:000000000000000000000000000000000101",
                "TKI:000000000000000000000000000000000102",
                "TKI:000000000000000000000000000000000103",
                "TKI:200000000000000000000000000000000007"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPD12, taskService, converter, 12, 555);

    List<String> tasksWithPD8 =
        new ArrayList<>(Arrays.asList("TKI:000000000000000000000000000000000008"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPD8, taskService, converter, 8, 555);

    List<String> tasksWithPD1 =
        new ArrayList<>(
            Arrays.asList(
                "TKI:000000000000000000000000000000000000",
                "TKI:000000000000000000000000000000000011",
                "TKI:000000000000000000000000000000000052",
                "TKI:000000000000000000000000000000000053",
                "TKI:000000000000000000000000000000000054",
                "TKI:000000000000000000000000000000000055"));
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPD1, taskService, converter, 1, 555);
  }

  private void updateClassificationAndRunAssociatedJobs(Classification classification)
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InvalidArgumentException, InterruptedException {
    classificationService.updateClassification(classification);
    Thread.sleep(10);
    // run the ClassificationChangedJob
    JobRunner runner = new JobRunner(taskanaEngine);
    // run the TaskRefreshJob that was scheduled by the ClassificationChangedJob.
    runner.runJobs();
    Thread.sleep(10); // otherwise the next runJobs call intermittently doesn't find the Job created
    // by the previous step (it searches with DueDate < CurrentTime)
    runner.runJobs();
  }

  private void validateTaskPropertiesAfterClassificationChange(
      Instant before,
      List<String> tasksUpdated,
      TaskService taskService,
      DaysToWorkingDaysConverter converter,
      int serviceLevel,
      int priority)
      throws TaskNotFoundException, NotAuthorizedException, InvalidArgumentException {
    for (String taskId : tasksUpdated) {
      Task task = taskService.getTask(taskId);
      assertTrue(
          task.getModified().isAfter(before), "Task " + task.getId() + " has not been refreshed.");
      long calendarDays = converter.convertWorkingDaysToDays(task.getPlanned(), serviceLevel);

      String msg =
          String.format(
              "Task: %s and Due Date: %s do not match planned %s. Calendar days : %s.",
              taskId, task.getDue(), task.getPlanned(), calendarDays);
      assertEquals(task.getDue(), task.getPlanned().plus(Duration.ofDays(calendarDays)), msg);
    }
  }
}
