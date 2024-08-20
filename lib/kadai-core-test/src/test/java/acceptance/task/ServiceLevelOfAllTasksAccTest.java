package acceptance.task;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration.Builder;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.BulkOperationResults;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.WorkingTimeCalculator;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.TaskAttachmentBuilder;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Acceptance test for changing the {@linkplain io.kadai.task.api.models.Task#getPlanned() planned}
 * Instant of {@linkplain io.kadai.task.api.models.Task Tasks} in bulk.
 */
@KadaiIntegrationTest
class ServiceLevelOfAllTasksAccTest {

  private static final String SMALL_CLASSIFICATION_SERVICE_LEVEL = "P2D";
  private static final String GREAT_CLASSIFICATION_SERVICE_LEVEL = "P7D";

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithWorkingTimeCalculation {
    @KadaiInject TaskService taskService;
    @KadaiInject WorkbasketService workbasketService;
    @KadaiInject ClassificationService classificationService;

    ClassificationSummary classificationSummarySmallServiceLevel;
    ClassificationSummary classificationSummaryGreatServiceLevel;
    Attachment attachmentSummarySmallServiceLevel;
    Attachment attachmentSummaryGreatServiceLevel;
    WorkbasketSummary defaultWorkbasketSummary;
    ObjectReference defaultObjectReference;

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      classificationSummarySmallServiceLevel =
          defaultTestClassification()
              .serviceLevel(SMALL_CLASSIFICATION_SERVICE_LEVEL)
              .buildAndStoreAsSummary(classificationService);
      classificationSummaryGreatServiceLevel =
          defaultTestClassification()
              .serviceLevel(GREAT_CLASSIFICATION_SERVICE_LEVEL)
              .buildAndStoreAsSummary(classificationService);

      defaultObjectReference = defaultTestObjectReference().build();

      attachmentSummarySmallServiceLevel =
          TaskAttachmentBuilder.newAttachment()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .objectReference(defaultObjectReference)
              .build();
      attachmentSummaryGreatServiceLevel =
          TaskAttachmentBuilder.newAttachment()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .objectReference(defaultObjectReference)
              .build();

      defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
          .workbasketId(defaultWorkbasketSummary.getId())
          .accessId("user-1-1")
          .permission(WorkbasketPermission.OPEN)
          .permission(WorkbasketPermission.READ)
          .permission(WorkbasketPermission.READTASKS)
          .permission(WorkbasketPermission.APPEND)
          .buildAndStore(workbasketService);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_SetPlannedOnMultipleTasks() throws Exception {
      Instant planned = Instant.parse("2020-05-03T07:00:00.000Z");
      TaskSummary task1 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .buildAndStoreAsSummary(taskService);
      TaskSummary task2 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .attachments(attachmentSummaryGreatServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task3 =
          createDefaultTask()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .attachments(attachmentSummarySmallServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      List<String> taskIds = List.of(task1.getId(), task2.getId(), task3.getId());

      BulkOperationResults<String, KadaiException> bulkLog =
          taskService.setPlannedPropertyOfTasks(planned, taskIds);

      assertThat(bulkLog.containsErrors()).isFalse();
      List<TaskSummary> result =
          taskService.createTaskQuery().idIn(task1.getId(), task2.getId(), task3.getId()).list();
      assertThat(result).extracting(TaskSummary::getPlanned).containsOnly(planned);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ChangeDue_When_SettingPlannedAndClassificationHasSmallerServiceLevel()
        throws Exception {
      Instant planned = Instant.parse("2020-05-04T07:00:00.000Z");
      TaskSummary task1 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .attachments(attachmentSummaryGreatServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task2 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .buildAndStoreAsSummary(taskService);
      List<String> taskIds = List.of(task1.getId(), task2.getId());

      BulkOperationResults<String, KadaiException> bulkLog =
          taskService.setPlannedPropertyOfTasks(planned, taskIds);

      assertThat(bulkLog.containsErrors()).isFalse();
      List<TaskSummary> result =
          taskService.createTaskQuery().idIn(task1.getId(), task2.getId()).list();
      Instant expectedDue = Instant.parse("2020-05-06T06:59:59.999Z");
      assertThat(result).extracting(TaskSummary::getDue).containsOnly(expectedDue);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ChangeDue_When_SettingPlannedAndAttachmentHasSmallerOrEqualServiceLevel()
        throws Exception {
      Instant planned = Instant.parse("2020-05-04T07:00:00.000Z");
      TaskSummary task1 =
          createDefaultTask()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .attachments(attachmentSummarySmallServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task2 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .attachments(attachmentSummarySmallServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      List<String> taskIds = List.of(task1.getId(), task2.getId());

      BulkOperationResults<String, KadaiException> bulkLog =
          taskService.setPlannedPropertyOfTasks(planned, taskIds);

      assertThat(bulkLog.containsErrors()).isFalse();
      List<TaskSummary> result =
          taskService.createTaskQuery().idIn(task1.getId(), task2.getId()).list();
      Instant expectedDue = Instant.parse("2020-05-06T06:59:59.999Z");
      assertThat(result).extracting(TaskSummary::getDue).containsOnly(expectedDue);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ChangeDue_When_SettingPlannedAndUsingDifferentServiceLevels() throws Exception {
      Instant planned = Instant.parse("2020-05-04T07:00:00.000Z");
      TaskSummary task1 =
          createDefaultTask()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .attachments(attachmentSummarySmallServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task2 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .attachments(attachmentSummaryGreatServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task3 =
          createDefaultTask()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .attachments(attachmentSummaryGreatServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      List<String> taskIds = List.of(task1.getId(), task2.getId(), task3.getId());

      BulkOperationResults<String, KadaiException> bulkLog =
          taskService.setPlannedPropertyOfTasks(planned, taskIds);

      assertThat(bulkLog.containsErrors()).isFalse();
      List<TaskSummary> result =
          taskService.createTaskQuery().idIn(task1.getId(), task2.getId(), task3.getId()).list();
      Instant expectedDueSmallServiceLevel = Instant.parse("2020-05-06T06:59:59.999Z");
      Instant expectedDueGreatServiceLevel = Instant.parse("2020-05-13T06:59:59.999Z");
      assertThat(result)
          .extracting(TaskSummary::getDue)
          .containsOnly(expectedDueSmallServiceLevel, expectedDueGreatServiceLevel);
    }

    private TaskBuilder createDefaultTask() {
      return (TaskBuilder.newTask()
          .workbasketSummary(defaultWorkbasketSummary)
          .primaryObjRef(defaultObjectReference));
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithWorkingDaysCalculation implements KadaiConfigurationModifier {

    @KadaiInject KadaiEngine kadaiEngine;
    @KadaiInject TaskService taskService;
    @KadaiInject WorkbasketService workbasketService;
    @KadaiInject ClassificationService classificationService;
    ClassificationSummary classificationSummarySmallServiceLevel;
    ClassificationSummary classificationSummaryGreatServiceLevel;
    Attachment attachmentSummarySmallServiceLevel;
    Attachment attachmentSummaryGreatServiceLevel;
    WorkbasketSummary defaultWorkbasketSummary;
    ObjectReference defaultObjectReference;
    WorkingTimeCalculator converter;

    @Override
    public Builder modify(Builder builder) {
      return builder.useWorkingTimeCalculation(false);
    }

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      classificationSummarySmallServiceLevel =
          defaultTestClassification()
              .serviceLevel(SMALL_CLASSIFICATION_SERVICE_LEVEL)
              .buildAndStoreAsSummary(classificationService);
      classificationSummaryGreatServiceLevel =
          defaultTestClassification()
              .serviceLevel(GREAT_CLASSIFICATION_SERVICE_LEVEL)
              .buildAndStoreAsSummary(classificationService);

      defaultObjectReference = defaultTestObjectReference().build();

      attachmentSummarySmallServiceLevel =
          TaskAttachmentBuilder.newAttachment()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .objectReference(defaultObjectReference)
              .build();
      attachmentSummaryGreatServiceLevel =
          TaskAttachmentBuilder.newAttachment()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .objectReference(defaultObjectReference)
              .build();

      defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
      WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
          .workbasketId(defaultWorkbasketSummary.getId())
          .accessId("user-1-1")
          .permission(WorkbasketPermission.OPEN)
          .permission(WorkbasketPermission.READ)
          .permission(WorkbasketPermission.READTASKS)
          .permission(WorkbasketPermission.APPEND)
          .buildAndStore(workbasketService);
      converter = kadaiEngine.getWorkingTimeCalculator();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_SetPlannedOnMultipleTasks() throws Exception {
      Instant planned = Instant.parse("2020-05-03T07:00:00.000Z");
      TaskSummary task1 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .buildAndStoreAsSummary(taskService);
      TaskSummary task2 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .attachments(attachmentSummaryGreatServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task3 =
          createDefaultTask()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .attachments(attachmentSummarySmallServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      List<String> taskIds = List.of(task1.getId(), task2.getId(), task3.getId());

      BulkOperationResults<String, KadaiException> bulkLog =
          taskService.setPlannedPropertyOfTasks(planned, taskIds);

      assertThat(bulkLog.containsErrors()).isFalse();
      List<TaskSummary> result =
          taskService.createTaskQuery().idIn(task1.getId(), task2.getId(), task3.getId()).list();
      assertThat(result).extracting(TaskSummary::getPlanned).containsOnly(planned);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ChangeDue_When_SettingPlannedAndClassificationHasSmallerServiceLevel()
        throws Exception {
      Instant planned = Instant.parse("2020-05-03T07:00:00.000Z");
      TaskSummary task1 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .attachments(attachmentSummaryGreatServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task2 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .buildAndStoreAsSummary(taskService);
      List<String> taskIds = List.of(task1.getId(), task2.getId());

      BulkOperationResults<String, KadaiException> bulkLog =
          taskService.setPlannedPropertyOfTasks(planned, taskIds);

      assertThat(bulkLog.containsErrors()).isFalse();
      List<TaskSummary> result =
          taskService.createTaskQuery().idIn(task1.getId(), task2.getId()).list();
      assertThat(result)
          .extracting(TaskSummary::getDue)
          .containsOnly(
              converter.addWorkingTime(
                  planned, Duration.parse(SMALL_CLASSIFICATION_SERVICE_LEVEL)));
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ChangeDue_When_SettingPlannedAndAttachmentHasSmallerOrEqualServiceLevel()
        throws Exception {
      Instant planned = Instant.parse("2020-05-03T07:00:00.000Z");
      TaskSummary task1 =
          createDefaultTask()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .attachments(attachmentSummarySmallServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task2 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .attachments(attachmentSummarySmallServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      List<String> taskIds = List.of(task1.getId(), task2.getId());

      BulkOperationResults<String, KadaiException> bulkLog =
          taskService.setPlannedPropertyOfTasks(planned, taskIds);

      assertThat(bulkLog.containsErrors()).isFalse();
      List<TaskSummary> result =
          taskService.createTaskQuery().idIn(task1.getId(), task2.getId()).list();
      assertThat(result)
          .extracting(TaskSummary::getDue)
          .containsOnly(
              converter.addWorkingTime(
                  planned, Duration.parse(SMALL_CLASSIFICATION_SERVICE_LEVEL)));
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_ChangeDue_When_SettingPlannedAndUsingDifferentServiceLevels() throws Exception {
      Instant planned = Instant.parse("2020-05-03T07:00:00.000Z");
      TaskSummary task1 =
          createDefaultTask()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .attachments(attachmentSummarySmallServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task2 =
          createDefaultTask()
              .classificationSummary(classificationSummarySmallServiceLevel)
              .attachments(attachmentSummaryGreatServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      TaskSummary task3 =
          createDefaultTask()
              .classificationSummary(classificationSummaryGreatServiceLevel)
              .attachments(attachmentSummaryGreatServiceLevel.copy())
              .buildAndStoreAsSummary(taskService);
      List<String> taskIds = List.of(task1.getId(), task2.getId(), task3.getId());

      BulkOperationResults<String, KadaiException> bulkLog =
          taskService.setPlannedPropertyOfTasks(planned, taskIds);

      assertThat(bulkLog.containsErrors()).isFalse();
      List<TaskSummary> result =
          taskService.createTaskQuery().idIn(task1.getId(), task2.getId(), task3.getId()).list();
      assertThat(result)
          .extracting(TaskSummary::getDue)
          .containsOnly(
              converter.addWorkingTime(planned, Duration.parse(SMALL_CLASSIFICATION_SERVICE_LEVEL)),
              converter.addWorkingTime(
                  planned, Duration.parse(GREAT_CLASSIFICATION_SERVICE_LEVEL)));
    }

    private TaskBuilder createDefaultTask() {
      return (TaskBuilder.newTask()
          .workbasketSummary(defaultWorkbasketSummary)
          .primaryObjRef(defaultObjectReference));
    }
  }
}
