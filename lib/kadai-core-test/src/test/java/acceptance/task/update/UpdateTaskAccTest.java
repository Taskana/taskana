package acceptance.task.update;

import static io.kadai.task.api.TaskCustomField.CUSTOM_1;
import static io.kadai.task.api.TaskCustomField.CUSTOM_10;
import static io.kadai.task.api.TaskCustomField.CUSTOM_12;
import static io.kadai.task.api.TaskCustomField.CUSTOM_14;
import static io.kadai.task.api.TaskCustomField.CUSTOM_16;
import static io.kadai.task.api.TaskCustomField.CUSTOM_2;
import static io.kadai.task.api.TaskCustomField.CUSTOM_3;
import static io.kadai.task.api.TaskCustomField.CUSTOM_5;
import static io.kadai.task.api.TaskCustomField.CUSTOM_7;
import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.internal.util.Pair;
import io.kadai.common.internal.util.Triplet;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.task.internal.models.TaskImpl;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.ObjectReferenceBuilder;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.UserBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;

@KadaiIntegrationTest
public class UpdateTaskAccTest {
  @KadaiInject TaskService taskService;
  @KadaiInject UserService userService;

  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;
  WorkbasketSummary wbWithoutEditTasks;
  WorkbasketSummary wbWithoutReadTasks;
  WorkbasketSummary wbWithoutRead;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification()
            .serviceLevel("P1D")
            .buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    wbWithoutEditTasks = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    wbWithoutReadTasks = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    wbWithoutRead = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(wbWithoutEditTasks.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(wbWithoutReadTasks.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(wbWithoutRead.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdatePrimaryObjectReferenceOfTask_When_Requested() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);
    task.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "7654321"));

    Task updatedTask = taskService.updateTask(task);

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getPrimaryObjRef().getCompany()).isEqualTo("COMPANY_A");
    assertThat(updatedTask.getPrimaryObjRef().getSystem()).isEqualTo("SYSTEM_A");
    assertThat(updatedTask.getPrimaryObjRef().getSystemInstance()).isEqualTo("INSTANCE_A");
    assertThat(updatedTask.getPrimaryObjRef().getType()).isEqualTo("VNR");
    assertThat(updatedTask.getPrimaryObjRef().getValue()).isEqualTo("7654321");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateBusinessAndParentBusinessProcessIdOfTask_When_Requested() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);
    task.setBusinessProcessId("MY_PROCESS_ID");
    task.setParentBusinessProcessId("MY_PARENT_PROCESS_ID");

    Task updatedTask = taskService.updateTask(task);

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getBusinessProcessId()).isEqualTo("MY_PROCESS_ID");
    assertThat(updatedTask.getParentBusinessProcessId()).isEqualTo("MY_PARENT_PROCESS_ID");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateTimeStampsAndFlagsOfTask_When_Requested() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);
    final Instant modifiedOriginal = task.getModified();
    task.setBusinessProcessId("MY_PROCESS_ID");

    Task updatedTask = taskService.updateTask(task);

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getCreated()).isNotNull();
    assertThat(updatedTask.getModified()).isNotNull();
    assertThat(modifiedOriginal.isAfter(updatedTask.getModified())).isFalse();
    assertThat(updatedTask.getModified()).isNotEqualTo(updatedTask.getCreated());
    assertThat(updatedTask.getCreated()).isEqualTo(task.getCreated());
    assertThat(updatedTask.isRead()).isEqualTo(task.isRead());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_PreventTimestampServiceLevelMismatch_When_ConfigurationPreventsIt() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);

    Instant planned = Instant.parse("2018-03-02T00:00:00Z");
    task.setPlanned(planned);
    Instant due = Instant.parse("2018-04-15T00:00:00Z");
    task.setDue(due);

    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessageContaining("not matching the service level");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdatePrimaryObjectReferenceOfTask_When_ObjectReferenceSystemAndSystemInstanceIsNull()
      throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService);
    task.setPrimaryObjRef(createObjectReference("COMPANY_A", null, null, "VNR", "7654321"));

    Task updatedTask = taskService.updateTask(task);

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getPrimaryObjRef().getSystem()).isNull();
    assertThat(updatedTask.getPrimaryObjRef().getSystemInstance()).isNull();
    assertThat(updatedTask.getPrimaryObjRef().getCompany()).isEqualTo("COMPANY_A");
    assertThat(updatedTask.getPrimaryObjRef().getType()).isEqualTo("VNR");
    assertThat(updatedTask.getPrimaryObjRef().getValue()).isEqualTo("7654321");
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest>
      should_ThrowException_When_MandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
          throws Exception {
    List<Pair<String, ObjectReference>> list =
        List.of(
            Pair.of("When Primary Object Reference is null", null),
            Pair.of(
                "When Value of Primary Object Reference is null",
                createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null)),
            Pair.of(
                "When Type of Primary Object Reference is null",
                createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567")),
            Pair.of(
                "When Company of Primary Object Reference is null",
                createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567")));

    ThrowingConsumer<Pair<String, ObjectReference>> test =
        pair -> {
          Task task =
              TaskBuilder.newTask()
                  .classificationSummary(defaultClassificationSummary)
                  .workbasketSummary(defaultWorkbasketSummary)
                  .primaryObjRef(defaultObjectReference)
                  .buildAndStore(taskService);
          task.setPrimaryObjRef(pair.getRight());
          assertThatThrownBy(() -> taskService.updateTask(task))
              .isInstanceOf(InvalidArgumentException.class);
        };
    return DynamicTest.stream(list.iterator(), Pair::getLeft, test);
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_ThrowException_When_UserIsNotAuthorized() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");

    task.setNote("Test Note");
    ThrowingCallable call = () -> taskService.updateTask(task);

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo("user-taskrouter");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getRequiredPermissions())
        .containsExactlyInAnyOrder(WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_TaskHasAlreadyBeenUpdated() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");
    final Task task2 = taskService.getTask(task.getId());

    task.setCustomField(CUSTOM_1, "willi");
    Thread.sleep(10);
    taskService.updateTask(task);
    task2.setCustomField(CUSTOM_2, "Walter");
    // TODO flaky test ... if speed is too high,
    assertThatThrownBy(() -> taskService.updateTask(task2))
        .isInstanceOf(ConcurrencyException.class)
        .hasMessage(
            "The entity with id '%s' cannot be updated since it has been modified while editing.",
            task.getId());
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_UpdateTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");
    ClassificationSummary classificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService, "admin");

    task.setClassificationKey(classificationSummary.getKey());
    ThrowingCallable updateTaskCall = () -> taskService.updateTask(task);

    assertThatCode(updateTaskCall).doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateTaskProperties_When_ClassificationOfTaskIsChanged() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");
    ClassificationSummary classificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService, "admin");

    task.setClassificationKey(classificationSummary.getKey());
    Task updatedTask = taskService.updateTask(task);

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getClassificationSummary().getKey())
        .isEqualTo(classificationSummary.getKey());
    assertThat(updatedTask.getClassificationSummary()).isNotEqualTo(defaultClassificationSummary);
    assertThat(task.getPlanned()).isEqualTo(updatedTask.getPlanned());
    assertThat(task.getName()).isEqualTo(updatedTask.getName());
    assertThat(task.getDescription()).isEqualTo(updatedTask.getDescription());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateReadFlagOfTask_When_SetReadToTrue() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");

    Task updatedTask = taskService.setTaskRead(task.getId(), true);

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.isRead()).isTrue();
    assertThat(updatedTask.getCreated()).isNotEqualTo(updatedTask.getModified());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateReadFlagOfTask_When_SetReadToFalse() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");

    Task updatedTask = taskService.setTaskRead(task.getId(), false);

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.isRead()).isFalse();
    assertThat(updatedTask.getCreated()).isNotEqualTo(updatedTask.getModified());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_UpdatingTaskWithInvalidId() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");

    ThrowingCallable call = () -> taskService.setTaskRead("INVALID", true);

    TaskNotFoundException e = catchThrowableOfType(TaskNotFoundException.class, call);
    assertThat(e.getTaskId()).isEqualTo("INVALID");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateTask_When_CustomPropertiesOfTaskWereChanged() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");

    task.setCustomField(CUSTOM_1, "T2100");
    Task updatedTask = taskService.updateTask(task);

    assertThat(updatedTask).isNotNull();
    assertThat(task.getCustomField(CUSTOM_1)).isEqualTo("T2100");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_SetPlannedCorrectly_When_PlannedIsSetExplicitly() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");

    Instant planned = Instant.parse("2024-06-17T23:14:31.0Z");
    task.setPlanned(planned);
    Task updatedTask = taskService.updateTask(task);

    assertThat(updatedTask.getPlanned()).isEqualTo(planned);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_ModificationOfWorkbasketKeyIsAttempted() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");
    WorkbasketSummary workbasketSummary =
        defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService, "admin");

    ((TaskImpl) task).setWorkbasketKey(workbasketSummary.getKey());

    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateNoTasks_When_UpdateTasksWithUnmatchedObjectReferenceWasCalled()
      throws Exception {
    TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference)
        .buildAndStore(taskService, "admin");

    ObjectReferenceImpl por = new ObjectReferenceImpl();
    por.setCompany("00");
    por.setSystem("PASystem");
    por.setSystemInstance("00");
    por.setType("VNR");
    por.setValue("22334455");
    Map<TaskCustomField, String> customProperties = new HashMap<>();
    customProperties.put(CUSTOM_7, "This is modifiedValue 7");
    customProperties.put(CUSTOM_14, null);
    customProperties.put(CUSTOM_3, "This is modifiedValue 3");
    customProperties.put(CUSTOM_16, "This is modifiedValue 16");

    List<String> taskIds = taskService.updateTasks(por, customProperties);
    assertThat(taskIds).isEmpty();
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_UpdateNoTasksWithPor_When_UserHasMissingPermission() throws Exception {
    List<Pair<String, WorkbasketSummary>> list =
        List.of(
            Pair.of("With Missing Read Permission", wbWithoutRead),
            Pair.of("With Missing ReadTasks Permission", wbWithoutReadTasks),
            Pair.of("With Missing EditTasks Permission", wbWithoutEditTasks));
    ThrowingConsumer<Pair<String, WorkbasketSummary>> testUpdateTask =
        t -> {
          ObjectReference objectReference =
              ObjectReferenceBuilder.newObjectReference()
                  .company("00")
                  .system("PASystem")
                  .systemInstance("00")
                  .type("VNR")
                  .value("22334455")
                  .build();
          TaskBuilder.newTask()
              .classificationSummary(defaultClassificationSummary)
              .workbasketSummary(t.getRight())
              .primaryObjRef(objectReference)
              .buildAndStore(taskService, "admin");
          Map<TaskCustomField, String> customProperties = new HashMap<>();
          customProperties.put(CUSTOM_7, "This is modifiedValue 7");
          customProperties.put(CUSTOM_14, null);
          customProperties.put(CUSTOM_3, "This is modifiedValue 3");
          customProperties.put(CUSTOM_16, "This is modifiedValue 16");

          List<String> taskIds = taskService.updateTasks(objectReference, customProperties);

          assertThat(taskIds).isEmpty();
        };
    return DynamicTest.stream(list.iterator(), Pair::getLeft, testUpdateTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateTasks_When_MatchingPrimaryObjectReferenceWasChanged() throws Exception {
    ObjectReference objectReference =
        ObjectReferenceBuilder.newObjectReference()
            .company("00")
            .system("PASystem")
            .systemInstance("00")
            .type("VNR")
            .value("22334455")
            .build();
    TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(objectReference)
        .buildAndStore(taskService, "admin");
    TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(objectReference)
        .buildAndStore(taskService, "admin");

    Map<TaskCustomField, String> customProperties = new HashMap<>();
    customProperties.put(CUSTOM_7, "This is modifiedValue 7");
    customProperties.put(CUSTOM_14, null);
    customProperties.put(CUSTOM_3, "This is modifiedValue 3");
    customProperties.put(CUSTOM_16, "This is modifiedValue 16");

    List<String> taskIds = taskService.updateTasks(objectReference, customProperties);
    assertThat(taskIds).hasSize(2);
    for (String taskId : taskIds) {
      Task task = taskService.getTask(taskId);
      assertThat(task.getCustomField(CUSTOM_3)).isEqualTo("This is modifiedValue 3");
      assertThat(task.getCustomField(CUSTOM_7)).isEqualTo("This is modifiedValue 7");
      assertThat(task.getCustomField(CUSTOM_16)).isEqualTo("This is modifiedValue 16");
      assertThat(task.getCustomField(CUSTOM_14)).isNull();
    }
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateTaskCustomAttributes_When_UpdateTasksIsCalled() throws Exception {
    Task task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .customAttribute(CUSTOM_2, "Value 2")
            .buildAndStore(taskService, "admin");
    Task task2 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .customAttribute(CUSTOM_2, "Value 2")
            .buildAndStore(taskService, "admin");
    List<String> taskIds = List.of(task1.getId(), task2.getId());
    Map<TaskCustomField, String> customProperties = new HashMap<>();
    customProperties.put(CUSTOM_1, "This is modifiedValue 1");
    customProperties.put(CUSTOM_5, "This is modifiedValue 5");
    customProperties.put(CUSTOM_10, "This is modifiedValue 10");
    customProperties.put(CUSTOM_12, "This is modifiedValue 12");
    customProperties.put(CUSTOM_16, null);

    List<String> changedTasks = taskService.updateTasks(taskIds, customProperties);
    assertThat(changedTasks).hasSize(2);
    for (String taskId : changedTasks) {
      Task task = taskService.getTask(taskId);
      assertThat(task.getCustomField(CUSTOM_1)).isEqualTo("This is modifiedValue 1");
      assertThat(task.getCustomField(CUSTOM_5)).isEqualTo("This is modifiedValue 5");
      assertThat(task.getCustomField(CUSTOM_10)).isEqualTo("This is modifiedValue 10");
      assertThat(task.getCustomField(CUSTOM_12)).isEqualTo("This is modifiedValue 12");
      assertThat(task.getCustomField(CUSTOM_2)).isNotNull();
      assertThat(task.getCustomField(CUSTOM_16)).isNull();
    }
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_UpdateNoTasksWithTaskId_When_UserHasMissingPermission()
      throws Exception {
    List<Pair<String, WorkbasketSummary>> list =
        List.of(
            Pair.of("With Missing Read Permission", wbWithoutRead),
            Pair.of("With Missing ReadTasks Permission", wbWithoutReadTasks),
            Pair.of("With Missing EditTasks Permission", wbWithoutEditTasks));
    ThrowingConsumer<Pair<String, WorkbasketSummary>> testUpdateTask =
        t -> {
          Task task =
              TaskBuilder.newTask()
                  .classificationSummary(defaultClassificationSummary)
                  .workbasketSummary(t.getRight())
                  .primaryObjRef(defaultObjectReference)
                  .buildAndStore(taskService, "admin");
          Map<TaskCustomField, String> customProperties = new HashMap<>();
          customProperties.put(CUSTOM_7, "This is modifiedValue 7");
          customProperties.put(CUSTOM_14, null);
          customProperties.put(CUSTOM_3, "This is modifiedValue 3");
          customProperties.put(CUSTOM_16, "This is modifiedValue 16");

          List<String> taskIds = taskService.updateTasks(List.of(task.getId()), customProperties);

          assertThat(taskIds).isEmpty();
        };
    return DynamicTest.stream(list.iterator(), Pair::getLeft, testUpdateTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateCallbackInfo() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");

    HashMap<String, String> callbackInfo = new HashMap<>();
    for (int i = 1; i <= 10; i++) {
      callbackInfo.put("info_" + i, "Value of info_" + i);
    }
    task.setCallbackInfo(callbackInfo);

    Task retrievedUpdatedTask = taskService.updateTask(task);
    assertThat(retrievedUpdatedTask.getCallbackInfo()).isEqualTo(callbackInfo);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateRetrieved_When_UpdateTaskIsCalled() throws Exception {
    Task task =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference)
            .buildAndStore(taskService, "admin");

    Instant retrievedTime = Instant.parse("2019-09-13T08:44:17.588Z");
    task.setReceived(retrievedTime);

    Task retrievedUpdatedTask = taskService.updateTask(task);
    assertThat(retrievedUpdatedTask).extracting(TaskSummary::getReceived).isEqualTo(retrievedTime);
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_MissingOneOfThePermissions() throws Exception {
    List<Triplet<String, WorkbasketSummary, WorkbasketPermission>> list =
        List.of(
            Triplet.of("With Missing Read Permission", wbWithoutRead, WorkbasketPermission.READ),
            Triplet.of(
                "With Missing ReadTasks Permission",
                wbWithoutReadTasks,
                WorkbasketPermission.READTASKS),
            Triplet.of(
                "With Missing EditTasks Permission",
                wbWithoutEditTasks,
                WorkbasketPermission.EDITTASKS));
    ThrowingConsumer<Triplet<String, WorkbasketSummary, WorkbasketPermission>> testUpdateTask =
        t -> {
          String anyUserName = "TestUser28";
          Task task =
              TaskBuilder.newTask()
                  .classificationSummary(defaultClassificationSummary)
                  .workbasketSummary(t.getMiddle())
                  .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
                  .state(TaskState.READY)
                  .owner(anyUserName)
                  .buildAndStore(taskService, "admin");

          task.setNote("Test Note");
          ThrowingCallable call = () -> taskService.updateTask(task);

          NotAuthorizedOnWorkbasketException e =
              catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

          if (t.getRight() != WorkbasketPermission.EDITTASKS) {
            assertThat(e.getRequiredPermissions())
                .containsExactlyInAnyOrder(
                    WorkbasketPermission.READ, WorkbasketPermission.READTASKS);
          } else {
            assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.EDITTASKS);
          }
          assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
          assertThat(e.getWorkbasketId()).isEqualTo(t.getMiddle().getId());
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, testUpdateTask);
  }

  private ObjectReferenceImpl createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReferenceImpl objectReference = new ObjectReferenceImpl();
    objectReference.setCompany(company);
    objectReference.setSystem(system);
    objectReference.setSystemInstance(systemInstance);
    objectReference.setType(type);
    objectReference.setValue(value);
    return objectReference;
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithEnforceServiceLevelDisabledAndAdditionalUserInfoEnabled
      implements KadaiConfigurationModifier {
    @KadaiInject TaskService taskService;

    @Override
    public KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder) {
      return builder.addAdditionalUserInfo(true).enforceServiceLevel(false);
    }

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      UserBuilder.newUser()
          .id("user-1-2")
          .firstName("Max")
          .lastName("Mustermann")
          .longName("Max Mustermann")
          .buildAndStore(userService, "businessadmin");
      UserBuilder.newUser()
          .id("user-1-1")
          .firstName("Ella")
          .lastName("Mustermann")
          .longName("Ella Mustermann")
          .buildAndStore(userService, "businessadmin");
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_AllowTimestampServiceLevelMismatch_When_ConfigurationAllowsIt() throws Exception {
      Task task =
          TaskBuilder.newTask()
              .classificationSummary(defaultClassificationSummary)
              .workbasketSummary(defaultWorkbasketSummary)
              .primaryObjRef(defaultObjectReference)
              .buildAndStore(taskService);

      Instant planned = Instant.parse("2018-01-02T00:00:00Z");
      task.setPlanned(planned);
      Instant due = Instant.parse("2018-02-15T00:00:00Z");
      task.setDue(due);
      Task updatedTask = taskService.updateTask(task);

      assertThat(updatedTask.getPlanned()).isEqualTo(planned);
      assertThat(updatedTask.getDue()).isEqualTo(due);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongName_When_NotChangingOwner() throws Exception {
      Task task =
          TaskBuilder.newTask()
              .owner("user-1-2")
              .classificationSummary(defaultClassificationSummary)
              .workbasketSummary(defaultWorkbasketSummary)
              .primaryObjRef(defaultObjectReference)
              .buildAndStore(taskService);

      task.setNote("New Note");
      Task updatedTask = taskService.updateTask(task);

      assertThat(updatedTask.getNote()).isEqualTo("New Note");
      assertThat(updatedTask.getOwner()).isEqualTo("user-1-2");
      assertThat(updatedTask.getOwnerLongName()).isEqualTo("Max Mustermann");
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongName_When_ChangingOwner() throws Exception {
      Task task =
          TaskBuilder.newTask()
              .owner("user-1-2")
              .classificationSummary(defaultClassificationSummary)
              .workbasketSummary(defaultWorkbasketSummary)
              .primaryObjRef(defaultObjectReference)
              .buildAndStore(taskService);

      task.setOwner("user-1-1");
      Task updatedTask = taskService.updateTask(task);

      assertThat(updatedTask.getOwner()).isEqualTo("user-1-1");
      assertThat(updatedTask.getOwnerLongName()).isEqualTo("Ella Mustermann");
    }
  }
}
