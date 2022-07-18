package acceptance.classification.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;

@ExtendWith(JaasExtension.class)
class UpdateClassificationAccTest extends AbstractAccTest {

  private final ClassificationService classificationService =
      taskanaEngine.getClassificationService();

  @WithAccessId(user = "admin")
  @Test
  void should_SetDefaultServiceLevel_When_TryingToUpdateClassificationWithMissingServiceLevel()
      throws Exception {
    Classification classification =
        classificationService.newClassification("Key1230", "DOMAIN_A", "TASK");
    classification.setServiceLevel("P1D");
    classification = classificationService.createClassification(classification);
    classification.setServiceLevel(null);
    classification = classificationService.updateClassification(classification);
    assertThat(classification.getServiceLevel()).isEqualTo("P0D");
    classification.setServiceLevel("");
    classification = classificationService.updateClassification(classification);
    assertThat(classification.getServiceLevel()).isEqualTo("P0D");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateClassification() throws Exception {
    String newName = "updated Name";
    String newEntryPoint = "updated EntryPoint";
    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
    final Instant createdBefore = classification.getCreated();
    final Instant modifiedBefore = classification.getModified();

    classification.setApplicationEntryPoint(newEntryPoint);
    classification.setCategory("PROCESS");
    classification.setCustomField(ClassificationCustomField.CUSTOM_1, "newCustom1");
    classification.setCustomField(ClassificationCustomField.CUSTOM_2, "newCustom2");
    classification.setCustomField(ClassificationCustomField.CUSTOM_3, "newCustom3");
    classification.setCustomField(ClassificationCustomField.CUSTOM_4, "newCustom4");
    classification.setCustomField(ClassificationCustomField.CUSTOM_5, "newCustom5");
    classification.setCustomField(ClassificationCustomField.CUSTOM_6, "newCustom6");
    classification.setCustomField(ClassificationCustomField.CUSTOM_7, "newCustom7");
    classification.setCustomField(ClassificationCustomField.CUSTOM_8, "newCustom8");
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
    assertThat(updatedClassification).isNotNull();
    assertThat(updatedClassification.getName()).isEqualTo(newName);
    assertThat(updatedClassification.getApplicationEntryPoint()).isEqualTo(newEntryPoint);
    assertThat(updatedClassification.getCreated()).isEqualTo(createdBefore);
    assertThat(modifiedBefore).isBefore(updatedClassification.getModified());
  }

  @Test
  void should_ThrowException_When_UserIsNotAuthorized() throws Exception {
    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
    classification.setCustomField(ClassificationCustomField.CUSTOM_1, "newCustom1");
    ThrowingCallable call = () -> classificationService.updateClassification(classification);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin() throws Exception {

    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");

    classification.setApplicationEntryPoint("updated EntryPoint");
    classification.setName("updated Name");

    ThrowingCallable updateClassificationCall =
        () -> classificationService.updateClassification(classification);
    assertThatThrownBy(updateClassificationCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin", groups = "user-1-1") // to read the task
  @Test
  void testUpdateTaskOnClassificationKeyCategoryChange() throws Exception {
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
    assertThat(beforeTask.getClassificationCategory())
        .isNotEqualTo(updatedTask.getClassificationCategory());
    assertThat(beforeTask.getClassificationSummary().getCategory())
        .isNotEqualTo(updatedTask.getClassificationSummary().getCategory());
    assertThat(updatedTask.getClassificationCategory()).isEqualTo("PROCESS");
    assertThat(updatedTask.getClassificationSummary().getCategory()).isEqualTo("PROCESS");
    assertThat(classification.getCreated()).isEqualTo(createdBefore);
    // isBeforeOrEquals in case of too fast execution
    assertThat(modifiedBefore).isBeforeOrEqualTo(classification.getModified());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateClassificationNotLatestAnymore() throws Exception {
    Classification base = classificationService.getClassification("T2100", "DOMAIN_A");
    final Classification classification =
        classificationService.getClassification("T2100", "DOMAIN_A");

    // UPDATE BASE
    base.setApplicationEntryPoint("SOME CHANGED POINT");
    base.setDescription("AN OTHER DESCRIPTION");
    base.setName("I AM UPDATED");
    Thread.sleep(20); // to avoid identity of modified timestamps between classification and base
    classificationService.updateClassification(base);

    classification.setName("NOW IT'S MY TURN");
    classification.setDescription("IT SHOULD BE TO LATE...");
    ThrowingCallable call = () -> classificationService.updateClassification(classification);
    assertThatThrownBy(call).isInstanceOf(ConcurrencyException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateClassificationParentIdToInvalid() throws Exception {
    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
    classification.setParentId("ID WHICH CANT BE FOUND");
    ThrowingCallable call = () -> classificationService.updateClassification(classification);
    assertThatThrownBy(call).isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateClassificationParentKeyToInvalid() throws Exception {
    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");
    classification.setParentKey("KEY WHICH CANT BE FOUND");
    ThrowingCallable call = () -> classificationService.updateClassification(classification);
    assertThatThrownBy(call).isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testUpdateClassificationPrioServiceLevel() throws Exception {
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
    assertThat(updatedClassification).isNotNull();

    assertThat(modifiedBefore.isAfter(updatedClassification.getModified())).isFalse();
    // TODO - resume old behaviour after attachment query is possible.
    TaskService taskService = taskanaEngine.getTaskService();

    List<String> tasksWithP1D =
        List.of(
            "TKI:000000000000000000000000000000000054",
            "TKI:000000000000000000000000000000000055",
            "TKI:000000000000000000000000000000000000",
            "TKI:000000000000000000000000000000000053");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithP1D, taskService, converter, 1, 1000);

    List<String> tasksWithP8D = List.of("TKI:000000000000000000000000000000000008");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithP8D, taskService, converter, 8, 1000);

    List<String> tasksWithP14D = List.of("TKI:000000000000000000000000000000000010");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithP14D, taskService, converter, 14, 1000);

    List<String> tasksWithP15D =
        List.of(
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
            "TKI:000000000000000000000000000000000103");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithP15D, taskService, converter, 15, 1000);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateClassificationWithSameKeyAndParentKey() throws Exception {

    Classification classification = classificationService.getClassification("T2100", "DOMAIN_A");

    classification.setParentKey(classification.getKey());
    ThrowingCallable call = () -> classificationService.updateClassification(classification);
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testUpdateClassificationWithEmptyServiceLevel() throws Exception {

    Classification classification =
        classificationService.newClassification("Key=0818", "DOMAIN_A", "TASK");
    classification.setServiceLevel("P1D");
    Classification created = classificationService.createClassification(classification);
    created.setServiceLevel("");

    assertThatCode(() -> classificationService.updateClassification(created))
        .doesNotThrowAnyException();
  }

  @WithAccessId(user = "admin")
  @Test
  void testUpdateClassificationChangePriority() throws Exception {
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
    assertThat(updatedClassification).isNotNull();
    assertThat(modifiedBefore.isAfter(updatedClassification.getModified())).isFalse();
    // TODO - resume old behaviour after attachment query is possible.
    TaskService taskService = taskanaEngine.getTaskService();

    List<String> tasksWithPrio99 =
        List.of(
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
            "TKI:000000000000000000000000000000000010");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio99, taskService, converter, 1, 99);

    List<String> tasksWithPrio101 = List.of("TKI:000000000000000000000000000000000011");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio101, taskService, converter, 1, 101);

    updatedClassification.setPriority(7);
    updateClassificationAndRunAssociatedJobs(updatedClassification);

    List<String> tasksWithPrio7 =
        List.of(
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
            "TKI:200000000000000000000000000000000007");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio7, taskService, converter, 1, 7);

    List<String> tasksWithPrio9 = List.of("TKI:000000000000000000000000000000000008");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio9, taskService, converter, 1, 9);

    tasksWithPrio101 = List.of("TKI:000000000000000000000000000000000011");

    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPrio101, taskService, converter, 1, 101);
  }

  @WithAccessId(user = "admin")
  @Test
  void testUpdateClassificationChangeServiceLevel() throws Exception {
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
    assertThat(updatedClassification).isNotNull();
    assertThat(updatedClassification.getModified()).isAfter(modifiedBefore);
    // TODO - resume old behaviour after attachment query is possible.
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> tasksWithPD12 =
        List.of(
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
            "TKI:200000000000000000000000000000000007");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPD12, taskService, converter, 12, 555);

    List<String> tasksWithPD8 = List.of("TKI:000000000000000000000000000000000008");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPD8, taskService, converter, 8, 555);

    List<String> tasksWithPD1 =
        List.of(
            "TKI:000000000000000000000000000000000000",
            "TKI:000000000000000000000000000000000052",
            "TKI:000000000000000000000000000000000053",
            "TKI:000000000000000000000000000000000054",
            "TKI:000000000000000000000000000000000055");
    validateTaskPropertiesAfterClassificationChange(
        before, tasksWithPD1, taskService, converter, 1, 555);
  }

  private void updateClassificationAndRunAssociatedJobs(Classification classification)
      throws Exception {
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
      WorkingDaysToDaysConverter converter,
      int serviceLevel,
      int priority)
      throws Exception {
    for (String taskId : tasksUpdated) {
      Task task = taskService.getTask(taskId);

      assertThat(task.getModified())
          .describedAs("Task " + task.getId() + " has not been refreshed.")
          .isAfter(before);
      Instant expDue =
          converter.addWorkingDaysToInstant(task.getPlanned(), Duration.ofDays(serviceLevel));

      assertThat(task.getDue()).isEqualTo(expDue);
      assertThat(task.getPriority()).isEqualTo(priority);
    }
  }
}
