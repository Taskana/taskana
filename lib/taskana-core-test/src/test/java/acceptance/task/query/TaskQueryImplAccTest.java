package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.task.api.CallbackState.CALLBACK_PROCESSING_REQUIRED;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.builder.ObjectReferenceBuilder;
import pro.taskana.task.internal.builder.TaskAttachmentBuilder;
import pro.taskana.task.internal.builder.TaskBuilder;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.builder.WorkbasketAccessItemBuilder;

@TaskanaIntegrationTest
class TaskQueryImplAccTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject CurrentUserContext currentUserContext;
  @TaskanaInject ClassificationService classificationService;

  ClassificationSummary defaultClassificationSummary;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
  }

  private TaskAttachmentBuilder createAttachment() {
    return TaskAttachmentBuilder.newAttachment()
        .objectReference(defaultTestObjectReference().build())
        .classificationSummary(defaultClassificationSummary);
  }

  private TaskBuilder taskInWorkbasket(WorkbasketSummary wb) {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .primaryObjRef(defaultTestObjectReference().build())
        .workbasketSummary(wb);
  }

  private WorkbasketSummary createWorkbasketWithPermission() throws PrivilegedActionException {
    WorkbasketSummary workbasketSummary =
        defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService, "businessadmin");
    persistPermission(workbasketSummary);
    return workbasketSummary;
  }

  private void persistPermission(WorkbasketSummary workbasketSummary)
      throws PrivilegedActionException {
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasketSummary.getId())
        .accessId(currentUserContext.getUserid())
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService, "businessadmin");
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class FilterTest {

    @WithAccessId(user = "user-1-2")
    @Test
    void should_ReturnAllTasks_When_NotApplyingAnyFilter() throws Exception {
      WorkbasketSummary wb = createWorkbasketWithPermission();
      TaskSummary taskSummary1 = taskInWorkbasket(wb).buildAndStoreAsSummary(taskService);
      TaskSummary taskSummary2 = taskInWorkbasket(wb).buildAndStoreAsSummary(taskService);

      List<TaskSummary> list = taskService.createTaskQuery().list();

      assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_ReturnAllTasksWithAllAttributesSet_When_NotApplyingAnyFilter() throws Exception {
      WorkbasketSummary wb = createWorkbasketWithPermission();
      Attachment attachment =
          TaskAttachmentBuilder.newAttachment()
              .classificationSummary(defaultClassificationSummary)
              .objectReference(defaultTestObjectReference().build())
              .build();

      TaskSummary taskSummary1 =
          taskInWorkbasket(wb)
              .externalId("external id")
              .received(Instant.parse("2020-04-19T13:13:00.000Z"))
              .created(Instant.parse("2020-04-20T13:13:00.000Z"))
              .claimed(Instant.parse("2020-04-21T13:13:00.000Z"))
              .completed(Instant.parse("2020-04-22T13:13:00.000Z"))
              .modified(Instant.parse("2020-04-22T13:13:00.000Z"))
              .planned(Instant.parse("2020-04-21T13:13:00.000Z"))
              .due(Instant.parse("2020-04-21T13:13:00.000Z"))
              .name("Name")
              .note("Note")
              .description("Description")
              .state(TaskState.COMPLETED)
              .businessProcessId("BPI:SomeNumber")
              .parentBusinessProcessId("BPI:OtherNumber")
              .owner("user-1-2")
              .primaryObjRef(defaultTestObjectReference().build())
              .manualPriority(7)
              .read(true)
              .transferred(true)
              .attachments(attachment)
              .customAttribute(TaskCustomField.CUSTOM_1, "custom1")
              .customAttribute(TaskCustomField.CUSTOM_2, "custom2")
              .customAttribute(TaskCustomField.CUSTOM_3, "custom3")
              .customAttribute(TaskCustomField.CUSTOM_4, "custom4")
              .customAttribute(TaskCustomField.CUSTOM_5, "custom5")
              .customAttribute(TaskCustomField.CUSTOM_6, "custom6")
              .customAttribute(TaskCustomField.CUSTOM_7, "custom7")
              .customAttribute(TaskCustomField.CUSTOM_8, "custom8")
              .customAttribute(TaskCustomField.CUSTOM_9, "custom9")
              .customAttribute(TaskCustomField.CUSTOM_10, "custom10")
              .customAttribute(TaskCustomField.CUSTOM_11, "custom11")
              .customAttribute(TaskCustomField.CUSTOM_12, "custom12")
              .customAttribute(TaskCustomField.CUSTOM_13, "custom13")
              .customAttribute(TaskCustomField.CUSTOM_14, "custom14")
              .customAttribute(TaskCustomField.CUSTOM_15, "custom15")
              .customAttribute(TaskCustomField.CUSTOM_16, "custom16")
              .callbackInfo(Map.of("custom", "value"))
              .callbackState(CallbackState.CALLBACK_PROCESSING_COMPLETED)
              .buildAndStoreAsSummary(taskService);
      TaskSummary taskSummary2 = taskInWorkbasket(wb).buildAndStoreAsSummary(taskService);

      List<TaskSummary> list = taskService.createTaskQuery().workbasketIdIn(wb.getId()).list();

      assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2);
      assertThat(taskSummary1).hasNoNullFieldsOrPropertiesExcept("ownerLongName");
    }

    @WithAccessId(user = "user-1-1")
    @Test
    @SuppressWarnings("unused")
    void should_ResolveUnderScore_When_UsingAnyLikeQuery() throws Exception {
      WorkbasketSummary wb = createWorkbasketWithPermission();
      TaskSummary taskSummary1 =
          taskInWorkbasket(wb).owner("user-1-1").buildAndStoreAsSummary(taskService);
      TaskSummary taskSummary2 =
          taskInWorkbasket(wb).owner("user-2-2").buildAndStoreAsSummary(taskService);

      List<TaskSummary> list =
          taskService.createTaskQuery().workbasketIdIn(wb.getId()).ownerLike("u_er-1-1").list();

      assertThat(list).containsExactly(taskSummary1);
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class TaskId {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_FilterByTaskId_When_QueryingForIdIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .idIn(taskSummary1.getId())
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_FilterByTaskId_When_QueryingForIdNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .idNotIn(taskSummary1.getId())
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ExternalId {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).externalId("EXT1").buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).externalId("EXT2").buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_FilterByExternalId_When_QueryingForExternalId() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).externalIdIn("EXT1").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_FilterByExternalId_When_QueryingForExternalIdNotIn() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).externalIdNotIn("EXT1").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Received {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .received(Instant.parse("2020-01-03T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .received(Instant.parse("2020-02-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_FilterByReceived_When_QueryingForReceivedWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .receivedWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_FilterByReceived_When_QueryingForReceivedNotWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .receivedNotWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Created {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .created(Instant.parse("2020-01-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .created(Instant.parse("2020-02-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_FilterCreated_When_QueryingForCreatedWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .createdWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_FilterCreated_When_QueryingForCreatedNotWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .createdNotWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Claimed {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .claimed(Instant.parse("2020-01-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .claimed(Instant.parse("2020-02-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .claimedWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNotWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .claimedNotWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Modified {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .modified(Instant.parse("2020-01-03T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .modified(Instant.parse("2020-02-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .modifiedWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNotWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .modifiedNotWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Planned {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .planned(Instant.parse("2020-01-03T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .planned(Instant.parse("2020-02-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .plannedWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNotWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .plannedNotWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Due {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .due(Instant.parse("2020-01-03T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .due(Instant.parse("2020-02-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .dueWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNotWithin() {

        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .dueNotWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Completed {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .completed(Instant.parse("2020-01-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .completed(Instant.parse("2020-02-01T00:00:00Z"))
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .completedWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNotWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .completedNotWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Name {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).name("Widerruf").buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).name("Schadenfall").buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).name("Widerruf neu").buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForName() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).nameIn("Widerruf").list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameNotIn() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).nameNotIn("Widerruf").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameLike() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).nameLike("Widerruf%").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .nameNotLike("Widerruf%")
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Creator {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).buildAndStoreAsSummary(taskService, "admin");
        taskSummary3 = taskInWorkbasket(wb).buildAndStoreAsSummary(taskService, "taskadmin");
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCreator() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).creatorIn("user-1-1").list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCreatorNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .creatorNotIn("user-1-1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCreatorWithLike() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).creatorLike("%admin").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCreatorWithNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .creatorNotLike("%admin")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Note {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).note("Note1").buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).note("Note2").buildAndStoreAsSummary(taskService);
        taskSummary3 = taskInWorkbasket(wb).note("Lorem ipsum").buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNoteLike() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).noteLike("Not%").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNoteNotLike() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).noteNotLike("Not%").list();

        assertThat(list).containsExactly(taskSummary3);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Description {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb).description("Description").buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).description("Description2").buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).description("Beschreibung").buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForDescriptionLike() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).descriptionLike("Des%").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForDescriptionNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .descriptionNotLike("Des%")
                .list();

        assertThat(list).containsExactly(taskSummary3);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Priority {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).priority(1).buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).priority(2).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPriorityIn() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).priorityIn(1).list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPriorityNotIn() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).priorityNotIn(1).list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class State {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb).state(TaskState.READY).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).state(TaskState.COMPLETED).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForStateIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .stateIn(TaskState.READY)
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForStateNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .stateNotIn(TaskState.READY)
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ClassificationId {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ClassificationSummary classificationSummary1 =
            defaultTestClassification()
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        ClassificationSummary classificationSummary2 =
            defaultTestClassification()
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        taskSummary1 =
            taskInWorkbasket(wb)
                .classificationSummary(classificationSummary1)
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .classificationSummary(classificationSummary2)
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationIdIn(taskSummary1.getClassificationSummary().getId())
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationIdNotIn(taskSummary1.getClassificationSummary().getId())
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ClassificationKey {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ClassificationSummary class1 =
            defaultTestClassification()
                .key("L1150")
                .buildAndStore(classificationService, "businessadmin");
        ClassificationSummary class2 =
            defaultTestClassification()
                .key("L1050")
                .buildAndStore(classificationService, "businessadmin");
        ClassificationSummary class3 =
            defaultTestClassification()
                .key("L10501")
                .buildAndStore(classificationService, "businessadmin");
        taskSummary1 =
            taskInWorkbasket(wb).classificationSummary(class1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).classificationSummary(class2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).classificationSummary(class3).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationKeyIn("L1050")
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationKeyNotIn("L1050")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationKeyLike("L1050%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationKeyNotLike("L1050%")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ClassificationCategory {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ClassificationSummary class1 =
            defaultTestClassification()
                .type("TASK")
                .category("MANUAL")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        ClassificationSummary class2 =
            defaultTestClassification()
                .type("TASK")
                .category("EXTERNAL")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        ClassificationSummary class3 =
            defaultTestClassification()
                .type("TASK")
                .category("AUTOMATIC")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        taskSummary1 =
            taskInWorkbasket(wb).classificationSummary(class1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).classificationSummary(class2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).classificationSummary(class3).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCategoryIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationCategoryIn("EXTERNAL")
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCategoryNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationCategoryNotIn("EXTERNAL")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCategoryLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationCategoryLike("%AL")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCategoryNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationCategoryNotLike("%AL")
                .list();

        assertThat(list).containsExactly(taskSummary3);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ClassificationName {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ClassificationSummary class1 =
            defaultTestClassification()
                .name("Widerruf")
                .buildAndStore(classificationService, "businessadmin");
        ClassificationSummary class2 =
            defaultTestClassification()
                .name("Dynamikänderung")
                .buildAndStore(classificationService, "businessadmin");
        ClassificationSummary class3 =
            defaultTestClassification()
                .name("Dynamik-Ablehnung")
                .buildAndStore(classificationService, "businessadmin");
        taskSummary1 =
            taskInWorkbasket(wb).classificationSummary(class1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).classificationSummary(class2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).classificationSummary(class3).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForClassificationName() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationNameIn("Widerruf")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationNameNotIn("Widerruf")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationNameLike("Dynamik%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .classificationNameNotLike("Dynamik%")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class WorkbasketId {

      WorkbasketSummary wb1;
      WorkbasketSummary wb2;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb1 = createWorkbasketWithPermission();
        wb2 = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb1).buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb2).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdIn() {
        List<TaskSummary> list = taskService.createTaskQuery().workbasketIdIn(wb1.getId()).list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb1.getId(), wb2.getId())
                .workbasketIdNotIn(wb1.getId())
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class WorkbasketKeyDomain {

      WorkbasketSummary wb1;
      WorkbasketSummary wb2;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb1 =
            defaultTestWorkbasket()
                .key("Schaden")
                .domain("DOMAIN_A")
                .buildAndStoreAsSummary(workbasketService, "businessadmin");
        persistPermission(wb1);
        wb2 =
            defaultTestWorkbasket()
                .key("Schaden")
                .domain("DOMAIN_B")
                .buildAndStoreAsSummary(workbasketService, "businessadmin");
        persistPermission(wb2);

        taskSummary1 = taskInWorkbasket(wb1).buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb2).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyDomainIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb1.getId(), wb2.getId())
                .workbasketKeyDomainIn(new KeyDomain("Schaden", "DOMAIN_A"))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyDomainNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb1.getId(), wb2.getId())
                .workbasketKeyDomainNotIn(new KeyDomain("Schaden", "DOMAIN_A"))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class BusinessProcessId {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb).businessProcessId("BPI1").buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).businessProcessId("BPI20").buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).businessProcessId("BPI21").buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .businessProcessIdIn("BPI1")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .businessProcessIdNotIn("BPI1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .businessProcessIdLike("BPI2%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .businessProcessIdNotLike("BPI2%")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ParentBusinessProcessId {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .parentBusinessProcessId("PBPI1")
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .parentBusinessProcessId("PBPI20")
                .buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb)
                .parentBusinessProcessId("PBPI22")
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .parentBusinessProcessIdIn("PBPI1")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .parentBusinessProcessIdNotIn("PBPI1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .parentBusinessProcessIdLike("PBPI2%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .parentBusinessProcessIdNotLike("PBPI2%")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Owner {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).owner("user-2-1").buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).owner("user-1-2").buildAndStoreAsSummary(taskService);
        taskSummary3 = taskInWorkbasket(wb).owner("user-1-3").buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForOwner() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).ownerIn("user-1-2").list();

        assertThat(list).containsExactly(taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForOwnerNotIn() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).ownerNotIn("user-1-2").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForOwnerLike() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).ownerLike("user-1%").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForOwnerNotLike() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).ownerNotLike("user-1%").list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PrimaryObjectReference {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      ObjectReference por1;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        por1 = defaultTestObjectReference().company("15").build();
        ObjectReference por2 = defaultTestObjectReference().build();
        taskSummary1 = taskInWorkbasket(wb).primaryObjRef(por1).buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).primaryObjRef(por2).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceIn(por1)
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PrimaryObjectReferenceCompany {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().company("Company1").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().company("Company20").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().company("Company21").build())
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorCompanyIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceCompanyIn("Company1")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorCompanyNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceCompanyNotIn("Company1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorCompanyLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceCompanyLike("Company2_")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorCompanyNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceCompanyNotLike("Company2_")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PrimaryObjectReferenceSystem {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().system("System1").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().system("System20").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().system("System21").build())
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorSystem() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceSystemIn("System1")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorSystemNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceSystemNotIn("System1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorSystemLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceSystemLike("System2_")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForPorSystemNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceSystemNotLike("System2_")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PrimaryObjectReferenceSystemInstance {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().systemInstance("Instance1").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().systemInstance("Instance20").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().systemInstance("Instance21").build())
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForInstance() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceSystemInstanceIn("Instance1")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForInstanceNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceSystemInstanceNotIn("Instance1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForInstanceLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceSystemInstanceLike("Instance2_")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForInstanceNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceSystemInstanceNotLike("Instance2_")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PrimaryObjectReferenceType {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().type("Type1").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().type("Type20").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().type("Type21").build())
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForTypeIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceTypeIn("Type1")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForTypeNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceTypeNotIn("Type1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForTypeLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceTypeLike("Type2_")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForTypeNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceTypeNotLike("Type2_")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PrimaryObjectReferenceValue {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().value("Value1").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().value("Value20").build())
                .buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb)
                .primaryObjRef(defaultTestObjectReference().value("Value21").build())
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceValueIn("Value1")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceValueNotIn("Value1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceValueLike("Value2_")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .primaryObjectReferenceValueNotLike("Value2_")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Read {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).read(true).buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).read(false).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForReadEqualsTrue() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).readEquals(true).list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForReadEqualsFalse() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).readEquals(false).list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Transferred {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 = taskInWorkbasket(wb).transferred(true).buildAndStoreAsSummary(taskService);
        taskSummary2 = taskInWorkbasket(wb).transferred(false).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForTransferredEqualsTrue() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).transferredEquals(true).list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForTransferredEqualsFalse() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .transferredEquals(false)
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentClassificationId {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      ClassificationSummary classification1;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        classification1 =
            defaultTestClassification()
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        ClassificationSummary classification2 =
            defaultTestClassification()
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        Attachment attachment1 = createAttachment().classificationSummary(classification1).build();
        Attachment attachment2 = createAttachment().classificationSummary(classification2).build();
        taskSummary1 =
            taskInWorkbasket(wb).attachments(attachment1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).attachments(attachment2).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationIdIn(classification1.getId())
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForIdNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationIdNotIn(classification1.getId())
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentClassificationKey {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ClassificationSummary classification1 =
            defaultTestClassification()
                .key("Key-1-1")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        ClassificationSummary classification2 =
            defaultTestClassification()
                .key("Key-2-1")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        ClassificationSummary classification3 =
            defaultTestClassification()
                .key("Key-2-2")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        Attachment attachment1 = createAttachment().classificationSummary(classification1).build();
        Attachment attachment2 = createAttachment().classificationSummary(classification2).build();
        Attachment attachment3 = createAttachment().classificationSummary(classification3).build();
        taskSummary1 =
            taskInWorkbasket(wb).attachments(attachment1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).attachments(attachment2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).attachments(attachment3).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationKeyIn("Key-2-1")
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationKeyNotIn("Key-2-1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationKeyLike("Key-2%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForKeyNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationKeyNotLike("Key-2%")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentClassificationName {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ClassificationSummary classification1 =
            defaultTestClassification()
                .name("Widerruf neu")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        ClassificationSummary classification2 =
            defaultTestClassification()
                .name("Widerruf")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        ClassificationSummary classification3 =
            defaultTestClassification()
                .name("Beratungsprotokoll")
                .buildAndStoreAsSummary(classificationService, "businessadmin");
        Attachment attachment1 = createAttachment().classificationSummary(classification1).build();
        Attachment attachment2 = createAttachment().classificationSummary(classification2).build();
        Attachment attachment3 = createAttachment().classificationSummary(classification3).build();
        taskSummary1 =
            taskInWorkbasket(wb).attachments(attachment1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).attachments(attachment2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).attachments(attachment3).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationNameIn("Widerruf")
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationNameNotIn("Widerruf")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationNameLike("Widerruf%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNameNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentClassificationNameNotLike("Widerruf%")
                .list();

        assertThat(list).containsExactly(taskSummary3);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentChannel {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        Attachment attachment1 = createAttachment().channel("channel_A_1").build();
        Attachment attachment2 = createAttachment().channel("channel_A_2").build();
        Attachment attachment3 = createAttachment().channel("channel_B_1").build();
        taskSummary1 =
            taskInWorkbasket(wb).attachments(attachment1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).attachments(attachment2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).attachments(attachment3).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForChannelIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentChannelIn("channel_A_1")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForChannelNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentChannelNotIn("channel_A_1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForChannelLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentChannelLike("channel_A%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForChannelNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentChannelNotLike("channel_A%")
                .list();

        assertThat(list).containsExactly(taskSummary3);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentReferenceValue {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        Attachment attachment1 =
            createAttachment()
                .objectReference(defaultTestObjectReference().value("Value1").build())
                .build();
        Attachment attachment2 =
            createAttachment()
                .objectReference(defaultTestObjectReference().value("Value2-1").build())
                .build();
        Attachment attachment3 =
            createAttachment()
                .objectReference(defaultTestObjectReference().value("Value2-3").build())
                .build();
        taskSummary1 =
            taskInWorkbasket(wb).attachments(attachment1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).attachments(attachment2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb).attachments(attachment3).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentReferenceValueIn("Value2-1")
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentReferenceValueNotIn("Value2-1")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForReferenceLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentReferenceValueLike("Value2%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForReferenceNotLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentReferenceValueNotLike("Value2%")
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentReceived {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        Attachment attachment1 =
            createAttachment().received(Instant.parse("2020-01-01T00:00:00Z")).build();
        Attachment attachment2 =
            createAttachment().received(Instant.parse("2020-02-01T00:00:00Z")).build();
        taskSummary1 =
            taskInWorkbasket(wb).attachments(attachment1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).attachments(attachment2).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentReceivedWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForNotWithin() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .attachmentNotReceivedWithin(
                    new TimeInterval(
                        Instant.parse("2020-01-01T00:00:00Z"),
                        Instant.parse("2020-01-05T00:00:00Z")))
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class QueryingObjectReferenceCombinations {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;
      TaskSummary taskSummary4;
      TaskSummary taskSummary5;
      TaskSummary taskSummary6;
      TaskSummary taskSummary7;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ObjectReference sor1 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("FirstType")
                .build();
        taskSummary1 =
            taskInWorkbasket(wb).objectReferences(sor1).buildAndStoreAsSummary(taskService);

        ObjectReference sor2 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("SecondValue")
                .type("SecondType")
                .build();
        taskSummary2 =
            taskInWorkbasket(wb).objectReferences(sor2).buildAndStoreAsSummary(taskService);

        ObjectReference sor2copy = sor2.copy();
        ObjectReference sor1copy = sor1.copy();
        taskSummary3 =
            taskInWorkbasket(wb)
                .objectReferences(sor2copy, sor1copy)
                .buildAndStoreAsSummary(taskService);

        ObjectReference sor3 =
            ObjectReferenceBuilder.newObjectReference()
                .company("SecondCompany")
                .value("SecondValue")
                .type("SecondType")
                .build();
        taskSummary4 =
            taskInWorkbasket(wb).objectReferences(sor1, sor3).buildAndStoreAsSummary(taskService);

        ObjectReference sor4 =
            ObjectReferenceBuilder.newObjectReference()
                .company("SecondCompany")
                .value("ThirdValue")
                .type("ThirdType")
                .build();
        taskSummary5 =
            taskInWorkbasket(wb).objectReferences(sor4).buildAndStoreAsSummary(taskService);

        ObjectReference sor5 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("ThirdValue")
                .type("FirstType")
                .build();
        taskSummary6 =
            taskInWorkbasket(wb).objectReferences(sor5).buildAndStoreAsSummary(taskService);

        ObjectReference sor6 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("ThirdType")
                .build();
        taskSummary7 =
            taskInWorkbasket(wb).objectReferences(sor6).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueInCompanyIn() {
        List<TaskSummary> tasks =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorValueIn("SecondValue")
                .sorCompanyIn("FirstCompany")
                .list();

        assertThat(tasks).hasSize(2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueInMultipleCompanyIn() {
        List<TaskSummary> tasks =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorValueIn("SecondValue")
                .sorCompanyIn("FirstCompany", "SecondCompany")
                .list();

        assertThat(tasks).hasSize(3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueInMultipleTypeLikeMultiple() {
        List<TaskSummary> tasks =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorValueIn("FirstValue", "ThirdValue")
                .sorTypeLike("First%", "Third%")
                .list();

        assertThat(tasks).hasSize(5);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ObjectReferenceValue {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ObjectReference sor1 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("FirstType")
                .build();
        ObjectReference sor2 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("SecondValue")
                .type("SecondType")
                .build();

        ObjectReference sor2copy = sor2.copy();
        ObjectReference sor1copy = sor1.copy();

        taskSummary1 =
            taskInWorkbasket(wb).objectReferences(sor1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).objectReferences(sor2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb)
                .objectReferences(sor2copy, sor1copy)
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorValueIn("FirstValue")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_CountCorrectly_When_QueryingForValueIn() {
        long numberOfTasks =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorValueIn("FirstValue")
                .count();

        assertThat(numberOfTasks).isEqualTo(2);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForValueLike() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).sorValueLike("%Value").list();
        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2, taskSummary3);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ObjectReferenceType {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;
      TaskSummary taskSummary4;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ObjectReference sor1 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("FirstType")
                .build();
        taskSummary1 =
            taskInWorkbasket(wb).objectReferences(sor1).buildAndStoreAsSummary(taskService);

        ObjectReference sor2 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("SecondType")
                .build();
        taskSummary2 =
            taskInWorkbasket(wb).objectReferences(sor2).buildAndStoreAsSummary(taskService);

        ObjectReference sor2copy = sor2.copy();
        ObjectReference sor1copy = sor1.copy();
        taskSummary3 =
            taskInWorkbasket(wb)
                .objectReferences(sor2copy, sor1copy)
                .buildAndStoreAsSummary(taskService);

        ObjectReference sor3 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("ThirdType")
                .build();
        taskSummary4 =
            taskInWorkbasket(wb).objectReferences(sor3).buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForTypeIn() {
        List<TaskSummary> list =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).sorTypeIn("FirstType").list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnSingleTaskSummary_When_QueryingForTypeLikeUsingSingle() {
        TaskSummary result =
            taskService.createTaskQuery().workbasketIdIn(wb.getId()).sorTypeLike("Third%").single();
        assertThat(result).isEqualTo(taskSummary4);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnEmptyList_When_QueryingForNonexistentTypeLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorTypeLike("%NoSuchType")
                .list();
        assertThat(list).isEmpty();
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ObjectReferenceCompany {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ObjectReference sor1 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("FirstType")
                .build();
        ObjectReference sor2 =
            ObjectReferenceBuilder.newObjectReference()
                .company("SecondCompany")
                .value("FirstValue")
                .type("SecondType")
                .build();
        ObjectReference sor2copy = sor2.copy();
        ObjectReference sor1copy = sor1.copy();

        taskSummary1 =
            taskInWorkbasket(wb).objectReferences(sor1).buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb).objectReferences(sor2).buildAndStoreAsSummary(taskService);
        taskSummary3 =
            taskInWorkbasket(wb)
                .objectReferences(sor2copy, sor1copy)
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCompanyInWithLimit() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorCompanyIn("FirstCompany")
                .list(0, 1);
        assertThat(list).hasSize(1).containsAnyOf(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCompanyLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorCompanyLike("%Company")
                .list();
        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnEmptyList_When_QueryingForNonexistentCompanyLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorCompanyLike("%NoSuchCompany")
                .list();
        assertThat(list).isEmpty();
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ObjectReferenceSystem {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;
      TaskSummary taskSummary4;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ObjectReference sor1 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("FirstType")
                .system("FirstSystem")
                .build();
        taskSummary1 =
            taskInWorkbasket(wb).objectReferences(sor1).buildAndStoreAsSummary(taskService);

        ObjectReference sor2 =
            ObjectReferenceBuilder.newObjectReference()
                .company("SecondCompany")
                .value("FirstValue")
                .type("SecondType")
                .system("SecondSystem")
                .build();
        taskSummary2 =
            taskInWorkbasket(wb).objectReferences(sor2).buildAndStoreAsSummary(taskService);

        ObjectReference sor2copy = sor2.copy();
        ObjectReference sor1copy = sor1.copy();
        taskSummary3 =
            taskInWorkbasket(wb)
                .objectReferences(sor2copy, sor1copy)
                .buildAndStoreAsSummary(taskService);

        ObjectReference objRefNoSystem =
            ObjectReferenceBuilder.newObjectReference()
                .company("SecondCompany")
                .value("FirstValue")
                .type("SecondType")
                .build();
        taskSummary4 =
            taskInWorkbasket(wb)
                .objectReferences(objRefNoSystem)
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForSystemIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorSystemIn("FirstSystem")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForSystemLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorSystemLike("%System")
                .list();
        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnEmptyList_When_QueryingForNonexistentSystemLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorSystemLike("%NoSuchSystem")
                .list();
        assertThat(list).isEmpty();
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ObjectReferenceSystemInstance {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;
      TaskSummary taskSummary3;
      TaskSummary taskSummary4;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        ObjectReference sor1 =
            ObjectReferenceBuilder.newObjectReference()
                .company("FirstCompany")
                .value("FirstValue")
                .type("FirstType")
                .systemInstance("FirstSystemInstance")
                .build();
        taskSummary1 =
            taskInWorkbasket(wb).objectReferences(sor1).buildAndStoreAsSummary(taskService);

        ObjectReference sor2 =
            ObjectReferenceBuilder.newObjectReference()
                .company("SecondCompany")
                .value("FirstValue")
                .type("SecondType")
                .systemInstance("SecondSystemInstance")
                .build();
        taskSummary2 =
            taskInWorkbasket(wb).objectReferences(sor2).buildAndStoreAsSummary(taskService);

        ObjectReference sor2copy = sor2.copy();
        ObjectReference sor1copy = sor1.copy();
        taskSummary3 =
            taskInWorkbasket(wb)
                .objectReferences(sor2copy, sor1copy)
                .buildAndStoreAsSummary(taskService);

        ObjectReference objRefNoSystem =
            ObjectReferenceBuilder.newObjectReference()
                .company("SecondCompany")
                .value("FirstValue")
                .type("SecondType")
                .build();
        taskSummary4 =
            taskInWorkbasket(wb)
                .objectReferences(objRefNoSystem)
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForSystemInstanceIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorSystemInstanceIn("FirstSystemInstance")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForSystemInstanceLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorSystemInstanceLike("%SystemInstance")
                .list();
        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary2, taskSummary3);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ReturnEmptyList_When_QueryingForNonexistentSystemInstanceLike() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .sorSystemInstanceLike("%NoSuchSystemInstance")
                .list();
        assertThat(list).isEmpty();
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class CustomAttributes {}

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class CallbackStates {

      WorkbasketSummary wb;
      TaskSummary taskSummary1;
      TaskSummary taskSummary2;

      @WithAccessId(user = "user-1-1")
      @BeforeAll
      void setup() throws Exception {
        wb = createWorkbasketWithPermission();
        taskSummary1 =
            taskInWorkbasket(wb)
                .callbackState(CallbackState.CLAIMED)
                .buildAndStoreAsSummary(taskService);
        taskSummary2 =
            taskInWorkbasket(wb)
                .callbackState(CALLBACK_PROCESSING_REQUIRED)
                .buildAndStoreAsSummary(taskService);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCallbackStateIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .callbackStateIn(CallbackState.CLAIMED)
                .list();

        assertThat(list).containsExactly(taskSummary1);
      }

      @WithAccessId(user = "user-1-1")
      @Test
      void should_ApplyFilter_When_QueryingForCallbackStateNotIn() {
        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .callbackStateNotIn(CallbackState.CLAIMED)
                .list();

        assertThat(list).containsExactly(taskSummary2);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class WildcardSearch {

      @WithAccessId(user = "user-1-1")
      @Test
      @SuppressWarnings("unused")
      void should_ApplyFilter_When_QueryingForWildcardSearch() throws Exception {
        WorkbasketSummary wb = createWorkbasketWithPermission();
        TaskSummary taskSummary1 =
            taskInWorkbasket(wb).name("Hans").buildAndStoreAsSummary(taskService);
        TaskSummary taskSummary2 =
            taskInWorkbasket(wb).name("Bert").buildAndStoreAsSummary(taskService);
        TaskSummary taskSummary3 =
            taskInWorkbasket(wb).description("Hansi").buildAndStoreAsSummary(taskService);
        WildcardSearchField[] wildcards = {
          WildcardSearchField.DESCRIPTION, WildcardSearchField.NAME
        };

        List<TaskSummary> list =
            taskService
                .createTaskQuery()
                .wildcardSearchFieldsIn(wildcards)
                .wildcardSearchValueLike("Hans%")
                .list();

        assertThat(list).containsExactlyInAnyOrder(taskSummary1, taskSummary3);
      }
    }
  }
}
