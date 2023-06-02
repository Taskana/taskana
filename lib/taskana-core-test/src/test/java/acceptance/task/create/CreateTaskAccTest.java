package acceptance.task.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.testapi.TaskanaConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.ClassificationBuilder;
import pro.taskana.testapi.builder.ObjectReferenceBuilder;
import pro.taskana.testapi.builder.TaskAttachmentBuilder;
import pro.taskana.testapi.builder.UserBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.models.User;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class CreateTaskAccTest {
  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject UserService userService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;
  Attachment defaultAttachment;
  User defaultUser;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
    defaultAttachment =
        TaskAttachmentBuilder.newAttachment()
            .objectReference(defaultObjectReference)
            .classificationSummary(defaultClassificationSummary)
            .created(Instant.now())
            .modified(Instant.now())
            .build();
    defaultUser =
        UserBuilder.newUser()
            .id("user-1-2")
            .longName("Mustermann, Max - (user-1-2)")
            .firstName("Max")
            .lastName("Mustermann")
            .buildAndStore(userService);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_NotSetAttachmentSummariesToNull_When_CreatingNewTaskWithTaskService()
      throws Exception {
    Task task = taskService.newTask(defaultWorkbasketSummary.getId());

    assertThat(task.getAttachments()).isNotNull();
    assertThat(task.asSummary().getAttachmentSummaries()).isNotNull();
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_CreateTask_When_UserIsMemberOfTaskRouterRole() throws Exception {
    Task newTask = createDefaultTask();

    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
  }

  @WithAccessId(user = "user-1-1", groups = "cn=routers,cn=groups,OU=Test,O=TASKANA")
  @Test
  void should_CreateTask_When_UserIsMemberOfGroupWithTaskRouterRole() throws Exception {
    Task newTask = createDefaultTask();

    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test // Fail
  void should_BeAbleToCreateNewTask_When_UsingTaskCopy() throws Exception {
    Attachment attachment =
        TaskAttachmentBuilder.newAttachment()
            .objectReference(defaultObjectReference)
            .classificationSummary(defaultClassificationSummary)
            .created(Instant.now())
            .modified(Instant.now())
            .customAttributes(createSimpleCustomPropertyMap(3))
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .channel("E-MAIL")
            .build();
    Task task = createDefaultTask();
    task.addAttachment(attachment);
    taskService.createTask(task);
    Task oldTask = taskService.getTask(task.getId());

    Task newTask = oldTask.copy();
    newTask = taskService.createTask(newTask);

    assertThat(newTask.getId()).isNotNull();
    assertThat(newTask.getId()).isNotEqualTo(oldTask.getId());
    assertThat(newTask.getAttachments())
        .extracting(AttachmentSummary::getTaskId)
        .contains(newTask.getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CreateSimpleManualTask() throws Exception {
    Task task = createDefaultTask();

    Task createdTask = taskService.createTask(task);

    Instant expectedPlanned = moveForwardToWorkingDay(createdTask.getCreated());
    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(createdTask.getOwner()).isEqualTo("user-1-2");
    assertThat(createdTask.getWorkbasketKey()).isEqualTo(defaultWorkbasketSummary.getKey());
    assertThat(createdTask.getName()).isEqualTo(defaultClassificationSummary.getName());
    assertThat(createdTask.getPrimaryObjRef()).isEqualTo(defaultObjectReference);
    assertThat(createdTask.getCreated()).isNotNull();
    assertThat(createdTask.getModified()).isNotNull();
    assertThat(createdTask.getBusinessProcessId()).isNotNull();
    assertThat(createdTask.getClaimed()).isNull();
    assertThat(createdTask.getCompleted()).isNull();
    assertThat(createdTask.getModified()).isEqualTo(createdTask.getCreated());
    assertThat(createdTask.getPlanned()).isEqualTo(expectedPlanned);
    assertThat(createdTask.getReceived()).isNull();
    assertThat(createdTask.getState()).isEqualTo(TaskState.READY);
    assertThat(createdTask.getParentBusinessProcessId()).isNull();
    assertThat(createdTask.getPriority()).isEqualTo(defaultClassificationSummary.getPriority());
    assertThat(createdTask.isRead()).isFalse();
    assertThat(createdTask.isTransferred()).isFalse();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_PreventTimestampServiceLevelMismatch_When_ConfigurationPreventsIt() throws Exception {
    Task task = createDefaultTask();
    Instant planned = Instant.parse("2018-01-02T00:00:00Z");
    task.setPlanned(planned);
    Instant due = Instant.parse("2018-02-15T00:00:00Z");
    task.setDue(due);

    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessageContaining("not matching the service level");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CreateTask_When_ObjectReferenceSystemAndSystemInstanceIsNull() throws Exception {
    ObjectReference objectReference =
        ObjectReferenceBuilder.newObjectReference()
            .system(null)
            .systemInstance(null)
            .company(defaultObjectReference.getCompany())
            .type(defaultObjectReference.getType())
            .value(defaultObjectReference.getValue())
            .build();
    Task task = taskService.newTask(defaultWorkbasketSummary.getId());
    task.setClassificationKey(defaultClassificationSummary.getKey());
    task.setPrimaryObjRef(objectReference);
    task.setOwner("user-1-2");

    Task createdTask = taskService.createTask(task);

    assertThat(createdTask).isNotNull();
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_CreateTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Task task = createDefaultTask();

    Task createdTask = taskService.createTask(task);

    assertThat(createdTask).isNotNull();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_CreatingTheSameTaskTwice() throws Exception {
    Task task = createDefaultTask();
    task.setExternalId("MyExternalId");
    Task createdTask = taskService.createTask(task);

    assertThat(createdTask).isNotNull();

    Task task2 = createDefaultTask();
    task2.setExternalId("MyExternalId");

    ThrowingCallable call = () -> taskService.createTask(task2);
    TaskAlreadyExistException e = catchThrowableOfType(call, TaskAlreadyExistException.class);
    assertThat(e.getExternalId()).isEqualTo("MyExternalId");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CreateTask_When_CustomAttributesAreSpecified() throws Exception {
    Task task = createDefaultTask();
    Map<String, String> customAttributesForCreate = createSimpleCustomPropertyMap(13);
    task.setCustomAttributeMap(customAttributesForCreate);
    Task createdTask = taskService.createTask(task);

    assertThat(createdTask).isNotNull();
    // verify that the map is correctly retrieved from the database
    Task retrievedTask = taskService.getTask(createdTask.getId());
    Map<String, String> customAttributesFromDb = retrievedTask.getCustomAttributeMap();
    assertThat(customAttributesFromDb).isNotNull();
    assertThat(customAttributesFromDb).isEqualTo(customAttributesForCreate);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CreateTask_When_AttachmentIsSpecified() throws Exception {
    Map<String, String> customAttributesForCreate = createSimpleCustomPropertyMap(27);
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .type("DOCUMENT")
            .category("EXTERNAL")
            .buildAndStoreAsSummary(classificationService, "admin");
    Task task = createDefaultTask();
    Attachment attachment =
        TaskAttachmentBuilder.newAttachment()
            .objectReference(defaultObjectReference)
            .classificationSummary(classificationSummary)
            .created(Instant.now())
            .modified(Instant.now())
            .customAttributes(customAttributesForCreate)
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .channel("E-MAIL")
            .build();
    task.addAttachment(attachment);

    Task createdTask = taskService.createTask(task);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getAttachments()).hasSize(1);
    Attachment firstAttachment = createdTask.getAttachments().get(0);
    assertThat(firstAttachment.getCustomAttributeMap().toString())
        .contains(
            IntStream.rangeClosed(1, 27)
                .mapToObj(String::valueOf)
                .map(
                    number ->
                        String.format("Property_%s=Property Value of Property_%s", number, number))
                .collect(Collectors.toSet()));
    assertThat(firstAttachment.getCreated()).isNotNull();
    assertThat(firstAttachment.getModified()).isNotNull();
    assertThat(firstAttachment.getModified()).isEqualTo(firstAttachment.getCreated());
    assertThat(firstAttachment.getClassificationSummary()).isNotNull();
    assertThat(firstAttachment.getClassificationSummary().getId()).isNotNull();
    assertThat(firstAttachment.getClassificationSummary().getKey()).isNotNull();
    assertThat(firstAttachment.getClassificationSummary().getType()).isNotNull();
    assertThat(firstAttachment.getClassificationSummary().getCategory()).isNotNull();
    assertThat(firstAttachment.getClassificationSummary().getDomain()).isNotNull();
    assertThat(firstAttachment.getClassificationSummary().getServiceLevel()).isNotNull();
    assertThat(firstAttachment.getReceived()).isNotNull();
    assertThat(firstAttachment.getChannel()).isNotNull();
    assertThat(firstAttachment.getObjectReference()).isNotNull();
    // verify that the map is correctly retrieved from the database
    Map<String, String> customAttributesFromDb = firstAttachment.getCustomAttributeMap();
    assertThat(customAttributesFromDb).isNotNull();
    assertThat(customAttributesForCreate).isEqualTo(customAttributesFromDb);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CreateTask_When_MultipleAttachmentsAreSpecified() throws Exception {
    Instant earlierInstant = Instant.parse("2018-01-12T00:00:00Z");
    Instant laterInstant = Instant.parse("2018-01-15T00:00:00Z");
    Task task = createDefaultTask();
    Attachment attachment =
        TaskAttachmentBuilder.newAttachment()
            .objectReference(defaultObjectReference)
            .classificationSummary(defaultClassificationSummary)
            .created(Instant.now())
            .modified(Instant.now())
            .received(laterInstant)
            .channel("E-MAIL")
            .build();
    Attachment attachment2 =
        TaskAttachmentBuilder.newAttachment()
            .objectReference(defaultObjectReference)
            .classificationSummary(defaultClassificationSummary)
            .created(Instant.now())
            .modified(Instant.now())
            .received(earlierInstant)
            .channel("E-MAIL")
            .build();
    task.addAttachment(attachment);
    task.addAttachment(attachment2);

    Task createdTask = taskService.createTask(task);
    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo("user-1-2");
    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(readTask.getAttachments()).isNotNull();
    assertThat(readTask.getAttachments()).hasSize(2);
    assertThat(readTask.getAttachments().get(1).getCreated()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getCreated()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getModified()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified())
        .isEqualTo(readTask.getAttachments().get(1).getCreated());
    assertThat(readTask.getAttachments().get(0).getModified())
        .isEqualTo(readTask.getAttachments().get(0).getCreated());
    assertThat(readTask.getAttachments().get(0).getObjectReference()).isNotNull();
    assertThat(readTask.getReceived()).isEqualTo(earlierInstant);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_SetPriorityAndDurationCorrectly_When_UsingClassificationOfAttachment()
      throws Exception {
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .priority(0)
            .serviceLevel("P3D")
            .buildAndStoreAsSummary(classificationService, "admin");
    ClassificationSummary classificationSummary1 =
        defaultTestClassification()
            .priority(50)
            .serviceLevel("P0D")
            .buildAndStoreAsSummary(classificationService, "admin");
    ClassificationSummary classificationSummary2 =
        defaultTestClassification()
            .priority(99)
            .serviceLevel("P5D")
            .buildAndStoreAsSummary(classificationService, "admin");
    Task task = taskService.newTask(defaultWorkbasketSummary.getId());
    task.setClassificationKey(classificationSummary.getKey());
    task.setPrimaryObjRef(defaultObjectReference);
    task.setOwner("user-1-2");
    task.setPlanned(Instant.now());
    Attachment attachment1 =
        TaskAttachmentBuilder.newAttachment()
            .objectReference(defaultObjectReference)
            .classificationSummary(classificationSummary1)
            .created(Instant.now())
            .modified(Instant.now())
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .channel("E-MAIL")
            .build();
    Attachment attachment2 =
        TaskAttachmentBuilder.newAttachment()
            .objectReference(defaultObjectReference)
            .classificationSummary(classificationSummary2)
            .created(Instant.now())
            .modified(Instant.now())
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .channel("E-MAIL")
            .build();
    task.addAttachment(attachment1);
    task.addAttachment(attachment2);

    Task createdTask = taskService.createTask(task);

    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getPriority()).isEqualTo(99);
    assertThat(createdTask.getDue()).isNotNull().isEqualTo(createdTask.getPlanned());
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_AttachmentIsInvalid() throws Exception {
    List<Pair<String, ObjectReference>> list =
        List.of(
            Pair.of("With Object Reference set to Null", null),
            Pair.of(
                "With Value of Object Reference set to Null",
                createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId", null)),
            Pair.of(
                "With Type of Object Reference set to Null",
                createObjectReference(
                    "COMPANY_A",
                    "SYSTEM_B",
                    "INSTANCE_B",
                    null,
                    "12345678901234567890123456789012345678901234567890")),
            Pair.of(
                "With Company of Object Reference set to Null",
                createObjectReference(
                    null,
                    "SYSTEM_B",
                    "INSTANCE_B",
                    "ArchiveId",
                    "12345678901234567890123456789012345678901234567890")));
    ThrowingConsumer<Pair<String, ObjectReference>> testCreateTask =
        t -> {
          Task taskWithInvalidAttachment = createDefaultTask();
          defaultAttachment.setObjectReference(t.getRight());
          taskWithInvalidAttachment.addAttachment(defaultAttachment);

          ThrowingCallable call = () -> taskService.createTask(taskWithInvalidAttachment);

          assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
        };
    return DynamicTest.stream(list.iterator(), Pair::getLeft, testCreateTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UseCustomName_For_NewTask() throws Exception {
    Task task = createDefaultTask();
    task.setName("Test Name");

    Task createdTask = taskService.createTask(task);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getName()).isEqualTo("Test Name");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UseClassificationMetadataFromCorrectDomain_For_NewTask() throws Exception {
    ClassificationSummary classificationSummary =
        ClassificationBuilder.newClassification()
            .key(defaultClassificationSummary.getKey())
            .priority(99)
            .domain("DOMAIN_B")
            .buildAndStoreAsSummary(classificationService, "admin");
    Task task = taskService.newTask(defaultWorkbasketSummary.getKey(), "DOMAIN_A");
    task.setClassificationKey(classificationSummary.getKey());
    task.setPrimaryObjRef(defaultObjectReference);
    task.setOwner("user-1-2");
    task.setName("Test Name");

    Task createdTask = taskService.createTask(task);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getPriority()).isEqualTo(0);
    assertThat(createdTask.getClassificationSummary()).isEqualTo(defaultClassificationSummary);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_WorkbasketDoesNotExist() {
    Task task = taskService.newTask("UNKNOWN");
    task.setClassificationKey(defaultClassificationSummary.getKey());
    task.setPrimaryObjRef(defaultObjectReference);
    task.setOwner("user-1-2");

    ThrowingCallable call = () -> taskService.createTask(task);
    WorkbasketNotFoundException e = catchThrowableOfType(call, WorkbasketNotFoundException.class);
    assertThat(e.getId()).isEqualTo("UNKNOWN");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_TestGetException_When_AppendIsNotPermitted() throws Exception {
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .buildAndStore(workbasketService, "admin");
    Task task = createDefaultTask();

    ThrowingCallable call = () -> taskService.createTask(task);

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(call, NotAuthorizedOnWorkbasketException.class);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getRequiredPermissions()).contains(WorkbasketPermission.APPEND);
  }

  @WithAccessId(user = "user-1-2")
  @TestFactory
  Stream<DynamicTest>
      should_ThrowException_When_MandatoryPrimaryObjectReferenceIsNotSetOrIncomplete() {
    List<Pair<String, ObjectReference>> list =
        List.of(
            Pair.of("Object Reference is Null", null),
            Pair.of(
                "Value of Object Reference is Null",
                createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null)),
            Pair.of(
                "Type of Object Reference is Null",
                createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567")),
            Pair.of(
                "Company of Object Reference is Null",
                createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567")));
    ThrowingConsumer<Pair<String, ObjectReference>> testCreateTask =
        t -> {
          Task task = taskService.newTask(defaultWorkbasketSummary.getId());
          task.setClassificationKey(defaultClassificationSummary.getKey());
          task.setOwner("user-1-2");
          if (t.getRight() != null) {
            task.setPrimaryObjRef(t.getRight());
          }

          ThrowingCallable call = () -> taskService.createTask(task);

          assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
        };

    return DynamicTest.stream(list.iterator(), Pair::getLeft, testCreateTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_SetDomainFromWorkbasket() throws Exception {
    Task task = createDefaultTask();

    Task createdTask = taskService.createTask(task);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getDomain()).isNotNull();
    assertThat(createdTask.getDomain()).isEqualTo(defaultWorkbasketSummary.getDomain());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ReadSameTaskObjectAsCreated() throws Exception {
    Task task = createDefaultTask();
    for (TaskCustomField taskCustomField : TaskCustomField.values()) {
      task.setCustomField(taskCustomField, taskCustomField.name());
    }
    task.setCustomAttributeMap(createSimpleCustomPropertyMap(5));
    task.setDescription("Description of test task");
    task.setNote("My note");
    task.addAttachment(defaultAttachment);

    Task createdTask = taskService.createTask(task);
    Task readTask = taskService.getTask(createdTask.getId());

    assertThat(readTask).isEqualTo(createdTask);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_CreateSimpleTask_When_CallbackInfoIsSet() throws Exception {
    Task task = createDefaultTask();
    Map<String, String> callbackInfo = createSimpleCustomPropertyMap(10);
    task.setCallbackInfo(callbackInfo);
    Task createdTask = taskService.createTask(task);
    moveForwardToWorkingDay(createdTask.getCreated());

    assertThat(createdTask).isNotNull();

    Task retrievedTask = taskService.getTask(createdTask.getId());
    assertThat(retrievedTask.getCallbackInfo()).isEqualTo(callbackInfo);
  }

  @Test
  void should_ThrowException_When_NoUserIdIsSetAndSecurityIsOn() {
    Task task = createDefaultTask();

    ThrowingCallable call = () -> taskService.createTask(task);
    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(call, NotAuthorizedOnWorkbasketException.class);
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getCurrentUserId()).isEqualTo(null);
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_CreatingTaskWithNonEmptyId() {
    Task task = createDefaultTask();
    ((TaskImpl) task).setId("TKI:000000000000000000000000000000000000");

    ThrowingCallable call = () -> taskService.createTask(task);
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserNotAuthorizedOnWorkbasket() throws Exception {
    Task task = createDefaultTask();

    ThrowingCallable call = () -> taskService.createTask(task);
    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(call, NotAuthorizedOnWorkbasketException.class);
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-1");
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_NotThrowNullPointerException_When_CreatingTaskWithoutWorkbasketSummary() {
    Task task = new TaskImpl();

    ThrowingCallable call = () -> taskService.createTask(task);
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "admin")
  @Test // Fail
  void should_ThrowException_When_WorkbasketMarkedForDeletion() throws Exception {
    WorkbasketSummary newWorkbasketSummary =
        defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    Task taskToPreventWorkbasketDeletion = taskService.newTask(newWorkbasketSummary.getId());
    taskToPreventWorkbasketDeletion.setClassificationKey(defaultClassificationSummary.getKey());
    taskToPreventWorkbasketDeletion.setPrimaryObjRef(defaultObjectReference);
    taskToPreventWorkbasketDeletion.setOwner("user-1-2");
    taskService.createTask(taskToPreventWorkbasketDeletion);
    taskService.cancelTask(taskToPreventWorkbasketDeletion.getId());
    Task testTask = taskService.newTask(newWorkbasketSummary.getId());
    testTask.setClassificationKey(defaultClassificationSummary.getKey());
    testTask.setPrimaryObjRef(defaultObjectReference);

    workbasketService.deleteWorkbasket(newWorkbasketSummary.getId());
    ThrowingCallable call = () -> taskService.createTask(testTask);

    WorkbasketNotFoundException e = catchThrowableOfType(call, WorkbasketNotFoundException.class);
    assertThat(e.getId()).isEqualTo(newWorkbasketSummary.getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_CreatingTaskWithAttachmentClassificationNull() {
    Task task = createDefaultTask();
    task.addAttachment(
        TaskAttachmentBuilder.newAttachment()
            .classificationSummary(null)
            .objectReference(defaultObjectReference)
            .channel("E-MAIL")
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .customAttributes(createSimpleCustomPropertyMap(3))
            .build());

    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Classification of Attachment must not be null.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_CreatingTaskWithAttachmentObjectReferenceNull() {
    Task task = createDefaultTask();
    task.addAttachment(
        TaskAttachmentBuilder.newAttachment()
            .classificationSummary(defaultClassificationSummary)
            .objectReference(null)
            .channel("E-MAIL")
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .customAttributes(createSimpleCustomPropertyMap(3))
            .build());

    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessageContaining("ObjectReference of Attachment must not be null.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_CreatingTaskWithNotExistingAttachmentClassification() {
    ClassificationSummary classificationSummary =
        classificationService
            .newClassification("NOT_EXISTING", defaultClassificationSummary.getDomain(), "")
            .asSummary();
    Task task = createDefaultTask();

    task.addAttachment(
        TaskAttachmentBuilder.newAttachment()
            .classificationSummary(classificationSummary)
            .objectReference(defaultObjectReference)
            .channel("E-MAIL")
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .customAttributes(createSimpleCustomPropertyMap(3))
            .build());

    ThrowingCallable call = () -> taskService.createTask(task);
    ClassificationNotFoundException e =
        catchThrowableOfType(call, ClassificationNotFoundException.class);
    assertThat(e.getClassificationKey()).isEqualTo("NOT_EXISTING");
    assertThat(e.getDomain()).isEqualTo(defaultClassificationSummary.getDomain());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ThrowException_When_CreatingTaskWithMissingAttachmentClassificationKey() {
    Task task = createDefaultTask();
    ClassificationSummary classificationSummary = new ClassificationSummaryImpl();
    task.addAttachment(
        TaskAttachmentBuilder.newAttachment()
            .classificationSummary(classificationSummary)
            .objectReference(defaultObjectReference)
            .channel("E-MAIL")
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .customAttributes(createSimpleCustomPropertyMap(3))
            .build());

    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessageContaining("ClassificationKey of Attachment must not be empty.");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_FetchAttachmentClassification_When_CreatingTaskWithAttachments() throws Exception {
    Task task = createDefaultTask();
    ClassificationSummary newClassificationSummary =
        classificationService
            .newClassification(
                defaultClassificationSummary.getKey(), defaultClassificationSummary.getDomain(), "")
            .asSummary();
    task.addAttachment(
        TaskAttachmentBuilder.newAttachment()
            .classificationSummary(newClassificationSummary)
            .objectReference(defaultObjectReference)
            .channel("E-MAIL")
            .received(Instant.parse("2018-01-15T00:00:00Z"))
            .customAttributes(createSimpleCustomPropertyMap(3))
            .build());

    assertThat(newClassificationSummary.getServiceLevel()).isNull();

    task = taskService.createTask(task);
    newClassificationSummary = task.getAttachments().get(0).getClassificationSummary();

    assertThat(newClassificationSummary.getId()).isNotNull();
    assertThat(newClassificationSummary.getDomain()).isNotNull();
    assertThat(newClassificationSummary.getServiceLevel()).isNotNull();
  }

  private Map<String, String> createSimpleCustomPropertyMap(int propertiesCount) {
    return IntStream.rangeClosed(1, propertiesCount)
        .mapToObj(String::valueOf)
        .collect(Collectors.toMap("Property_"::concat, "Property Value of Property_"::concat));
  }

  private Task createDefaultTask() {
    Task task = taskService.newTask(defaultWorkbasketSummary.getId());
    task.setClassificationKey(defaultClassificationSummary.getKey());
    task.setPrimaryObjRef(defaultObjectReference);
    task.setOwner("user-1-2");
    return task;
  }

  private Instant moveForwardToWorkingDay(Instant date) {
    WorkingTimeCalculator workingTimeCalculator = taskanaEngine.getWorkingTimeCalculator();
    return workingTimeCalculator.addWorkingTime(date, Duration.ZERO);
  }

  private ObjectReference createObjectReference(
      String company, String system, String instance, String type, String value) {
    return ObjectReferenceBuilder.newObjectReference()
        .company(company)
        .system(system)
        .systemInstance(instance)
        .type(type)
        .value(value)
        .build();
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithAdditionalUserInfoEnabled implements TaskanaConfigurationModifier {

    @TaskanaInject TaskService taskService;

    @Override
    public TaskanaConfiguration.Builder modify(TaskanaConfiguration.Builder builder) {
      return builder.addAdditionalUserInfo(true);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetOwnerLongName() throws Exception {
      Task task = createDefaultTask();

      Task createdTask = taskService.createTask(task);

      assertThat(createdTask).isNotNull();
      assertThat(createdTask.getOwnerLongName()).isEqualTo("Mustermann, Max - (user-1-2)");
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithEnforceServiceLevelDisabled implements TaskanaConfigurationModifier {

    @TaskanaInject TaskService taskService;

    @Override
    public TaskanaConfiguration.Builder modify(TaskanaConfiguration.Builder builder) {
      return builder.enforceServiceLevel(false);
    }

    @WithAccessId(user = "user-1-2")
    @Test
    void should_SetDueAndPlanned_When_DueAndPlannedMismatchServiceLevel() throws Exception {
      Task task = createDefaultTask();
      Instant planned = Instant.parse("2018-01-02T00:00:00Z");
      task.setPlanned(planned);
      Instant due = Instant.parse("2018-02-15T00:00:00Z");
      task.setDue(due);

      Task createdTask = taskService.createTask(task);

      assertThat(createdTask).isNotNull();
      assertThat(createdTask.getPlanned()).isEqualTo(planned);
      assertThat(createdTask.getDue()).isEqualTo(due);
    }
  }
}
