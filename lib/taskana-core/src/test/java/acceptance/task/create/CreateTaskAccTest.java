package acceptance.task.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import acceptance.TaskTestMapper;
import acceptance.TaskanaEngineProxy;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.AttachmentMapper;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateTaskAccTest extends AbstractAccTest {

  private final ClassificationService classificationService =
      taskanaEngine.getClassificationService();

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotSetAttachmentSummariesToNull_When_CreatingNewTaskWithTaskService() {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.newTask("WBI:100000000000000000000000000000000006");
    assertThat(task.getAttachments()).isNotNull();
    assertThat(task.asSummary().getAttachmentSummaries()).isNotNull();
  }

  @WithAccessId(user = "user-taskrouter")
  @Test
  void should_CreateTask_When_UserIsMemberOfTaskRouterRole() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("WBI:100000000000000000000000000000000006");
    newTask.setClassificationKey("T2100");
    ObjectReferenceImpl objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    objectReference.setTaskId(newTask.getId());
    newTask.setPrimaryObjRef(objectReference);

    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
  }

  @WithAccessId(user = "user-1-1", groups = "cn=routers,cn=groups,OU=Test,O=TASKANA")
  @Test
  void should_CreateTask_When_UserIsMemberOfGroupWithTaskRouterRole() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("WBI:100000000000000000000000000000000010");
    newTask.setClassificationKey("T2100");
    ObjectReferenceImpl objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    objectReference.setTaskId(newTask.getId());
    newTask.setPrimaryObjRef(objectReference);

    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_BeAbleToCreateNewTask_When_TaskCopy() throws Exception {
    Task oldTask = taskService.getTask("TKI:000000000000000000000000000000000000");

    Task newTask = oldTask.copy();
    newTask = taskService.createTask(newTask);

    assertThat(newTask.getId()).isNotNull();
    assertThat(newTask.getId()).isNotEqualTo(oldTask.getId());
    assertThat(newTask.getAttachments())
        .extracting(AttachmentSummary::getTaskId)
        .contains(newTask.getId());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateSimpleManualTask() throws Exception {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    ObjectReferenceImpl objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    objectReference.setTaskId(newTask.getId());
    newTask.setPrimaryObjRef(objectReference);

    newTask.setOwner("user-1-1");
    Task createdTask = taskService.createTask(newTask);

    Instant expectedPlanned = moveForwardToWorkingDay(createdTask.getCreated());

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(createdTask.getOwner()).isEqualTo("user-1-1");
    assertThat(createdTask.getWorkbasketKey()).isEqualTo("USER-1-1");
    assertThat(createdTask.getName()).isEqualTo("T-Vertragstermin VERA");
    assertThat(createdTask.getPrimaryObjRef()).isEqualTo(objectReference);
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
    assertThat(createdTask.getPriority()).isEqualTo(2);
    assertThat(createdTask.isRead()).isFalse();
    assertThat(createdTask.isTransferred()).isFalse();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateTaskWithAdditionalUserInfo() throws Exception {
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .addAdditionalUserInfo(true)
            .build();
    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaConfiguration);
    TaskService taskService = taskanaEngine.getTaskService();

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    ObjectReferenceImpl objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    objectReference.setTaskId(newTask.getId());
    newTask.setPrimaryObjRef(objectReference);
    newTask.setOwner("user-1-1");

    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getOwner()).isEqualTo("user-1-1");
    assertThat(createdTask.getOwnerLongName()).isEqualTo("Mustermann, Max - (user-1-1)");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_PreventTimestampServiceLevelMismatch_When_ConfigurationPreventsIt() {
    // Given
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T6310");
    ObjectReference objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    newTask.setOwner("user-1-1");

    // When
    Instant planned = Instant.parse("2018-01-02T00:00:00Z");
    newTask.setPlanned(planned);
    Instant due = Instant.parse("2018-02-15T00:00:00Z");
    newTask.setDue(due);

    // Then
    assertThatThrownBy(() -> taskService.createTask(newTask))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessageContaining("not matching the service level");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_AllowTimestampServiceLevelMismatch_When_ConfigurationAllowsIt() throws Exception {
    // Given
    TaskanaConfiguration taskanaConfiguration =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .allowTimestampServiceLevelMismatch(true)
            .build();
    TaskanaEngine taskanaEngine = TaskanaEngine.buildTaskanaEngine(taskanaConfiguration);
    Task newTask = taskanaEngine.getTaskService().newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T6310");
    ObjectReference objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    newTask.setOwner("user-1-1");

    // When
    Instant planned = Instant.parse("2018-01-02T00:00:00Z");
    newTask.setPlanned(planned);
    Instant due = Instant.parse("2018-02-15T00:00:00Z");
    newTask.setDue(due);
    Task createdTask = taskanaEngine.getTaskService().createTask(newTask);

    // Then
    assertThat(createdTask.getPlanned()).isEqualTo(planned);
    assertThat(createdTask.getDue()).isEqualTo(due);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateTask_When_ObjectReferenceSystemAndSystemInstanceIsNull() throws Exception {

    String currentUser = taskanaEngine.getCurrentUserContext().getUserid();

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("COMPANY_A", null, null, "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    newTask.setOwner(currentUser);
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_CreateTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    String currentUser = taskanaEngine.getCurrentUserContext().getUserid();

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    newTask.setOwner(currentUser);
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CreatingTheSameTaskTwice() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setExternalId("MyExternalId");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);
    Instant expectedPlanned = moveForwardToWorkingDay(createdTask.getCreated());

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(createdTask.getName()).isEqualTo("T-Vertragstermin VERA");
    assertThat(createdTask.getPrimaryObjRef().getValue()).isEqualTo("1234567");
    assertThat(createdTask.getExternalId()).isNotNull();
    assertThat(createdTask.getCreated()).isNotNull();
    assertThat(createdTask.getModified()).isNotNull();
    assertThat(createdTask.getBusinessProcessId()).isNotNull();
    assertThat(createdTask.getClaimed()).isNull();
    assertThat(createdTask.getCompleted()).isNull();
    assertThat(createdTask.getModified()).isEqualTo(createdTask.getCreated());
    assertThat(createdTask.getPlanned()).isEqualTo(expectedPlanned);
    assertThat(createdTask.getState()).isEqualTo(TaskState.READY);
    assertThat(createdTask.getParentBusinessProcessId()).isNull();
    assertThat(createdTask.getPriority()).isEqualTo(2);
    assertThat(createdTask.isRead()).isFalse();
    assertThat(createdTask.isTransferred()).isFalse();

    Task newTask2 = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask2.setExternalId("MyExternalId");
    newTask2.setClassificationKey("T2100");
    newTask2.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    ThrowingCallable call = () -> taskService.createTask(newTask2);
    assertThatThrownBy(call).isInstanceOf(TaskAlreadyExistException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateTask_When_CustomAttributesAreSpecified() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Map<String, String> customAttributesForCreate = createSimpleCustomPropertyMap(13);
    newTask.setCustomAttributeMap(customAttributesForCreate);
    Instant expectedReceived = Instant.parse("2019-09-13T08:44:17.588Z");
    newTask.setReceived(expectedReceived);
    Task createdTask = taskService.createTask(newTask);
    Instant expectedPlanned = moveForwardToWorkingDay(createdTask.getCreated());

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getName()).isEqualTo("T-Vertragstermin VERA");
    assertThat(createdTask.getPrimaryObjRef().getValue()).isEqualTo("1234567");
    assertThat(createdTask.getCreated()).isNotNull();
    assertThat(createdTask.getModified()).isNotNull();
    assertThat(createdTask.getBusinessProcessId()).isNotNull();
    assertThat(createdTask.getClaimed()).isNull();
    assertThat(createdTask.getCompleted()).isNull();
    assertThat(createdTask.getModified()).isEqualTo(createdTask.getCreated());
    assertThat(createdTask.getPlanned()).isEqualTo(expectedPlanned);
    assertThat(createdTask.getReceived()).isEqualTo(expectedReceived);
    assertThat(createdTask.getState()).isEqualTo(TaskState.READY);
    assertThat(createdTask.getParentBusinessProcessId()).isNull();
    assertThat(createdTask.getPriority()).isEqualTo(2);
    assertThat(createdTask.isRead()).isFalse();
    assertThat(createdTask.isTransferred()).isFalse();
    // verify that the database content is as expected
    TaskanaEngineProxy engineProxy = new TaskanaEngineProxy(taskanaEngine);
    try {
      SqlSession session = engineProxy.getSqlSession();
      Configuration config = session.getConfiguration();
      if (!config.hasMapper(TaskTestMapper.class)) {
        config.addMapper(TaskTestMapper.class);
      }
      TaskTestMapper mapper = session.getMapper(TaskTestMapper.class);

      engineProxy.openConnection();
      String customProperties = mapper.getCustomAttributesAsString(createdTask.getId());
      assertThat(customProperties)
          .contains(
              "\"Property_13\":\"Property Value of Property_13\"",
              "\"Property_12\":\"Property Value of Property_12\"",
              "\"Property_11\":\"Property Value of Property_11\"",
              "\"Property_10\":\"Property Value of Property_10\"",
              "\"Property_9\":\"Property Value of Property_9\"",
              "\"Property_8\":\"Property Value of Property_8\"",
              "\"Property_7\":\"Property Value of Property_7\"",
              "\"Property_6\":\"Property Value of Property_6\"",
              "\"Property_5\":\"Property Value of Property_5\"",
              "\"Property_4\":\"Property Value of Property_4\"",
              "\"Property_3\":\"Property Value of Property_3\"",
              "\"Property_2\":\"Property Value of Property_2\"",
              "\"Property_1\":\"Property Value of Property_1\"");
    } finally {
      engineProxy.returnConnection();
    }
    // verify that the map is correctly retrieved from the database
    Task retrievedTask = taskService.getTask(createdTask.getId());
    Map<String, String> customAttributesFromDb = retrievedTask.getCustomAttributeMap();
    assertThat(customAttributesFromDb).isNotNull();
    assertThat(customAttributesForCreate).isEqualTo(customAttributesFromDb);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateTask_When_AttachmentIsSpecified() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010");
    Map<String, String> customAttributesForCreate = createSimpleCustomPropertyMap(27);
    newTask.addAttachment(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            customAttributesForCreate));
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);
    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());

    // verify that the database content is as expected
    TaskanaEngineProxy engineProxy = new TaskanaEngineProxy(taskanaEngine);
    try {
      SqlSession session = engineProxy.getSqlSession();
      AttachmentMapper mapper = session.getMapper(AttachmentMapper.class);
      engineProxy.openConnection();
      String customProperties =
          mapper.getCustomAttributesAsString(createdTask.getAttachments().get(0).getId());
      Set<String> expectedPhrasesSet =
          IntStream.rangeClosed(1, 27)
              .mapToObj(String::valueOf)
              .map(
                  number ->
                      String.format(
                          "\"Property_%s\":\"Property Value of Property_%s\"", number, number))
              .collect(Collectors.toSet());
      assertThat(customProperties).contains(expectedPhrasesSet);
    } finally {
      engineProxy.returnConnection();
    }

    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(readTask.getAttachments()).isNotNull();
    assertThat(readTask.getAttachments()).hasSize(1);
    assertThat(readTask.getAttachments().get(0).getCreated()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getModified()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getModified())
        .isEqualTo(readTask.getAttachments().get(0).getCreated());
    assertThat(readTask.getAttachments().get(0).getClassificationSummary()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getClassificationSummary().getId()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getClassificationSummary().getKey()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getClassificationSummary().getType()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getClassificationSummary().getCategory())
        .isNotNull();
    assertThat(readTask.getAttachments().get(0).getClassificationSummary().getDomain()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getClassificationSummary().getServiceLevel())
        .isNotNull();
    assertThat(readTask.getAttachments().get(0).getReceived()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getChannel()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getObjectReference()).isNotNull();
    // verify that the map is correctly retrieved from the database
    Map<String, String> customAttributesFromDb =
        readTask.getAttachments().get(0).getCustomAttributeMap();
    assertThat(customAttributesFromDb).isNotNull();
    assertThat(customAttributesForCreate).isEqualTo(customAttributesFromDb);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateTask_When_MultipleAttachmentsAreSpecified() throws Exception {

    Instant earlierInstant = Instant.parse("2018-01-12T00:00:00Z");
    Instant laterInstant = Instant.parse("2018-01-15T00:00:00Z");
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.addAttachment(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            laterInstant,
            createSimpleCustomPropertyMap(3)));
    newTask.addAttachment(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            earlierInstant,
            createSimpleCustomPropertyMap(3)));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());

    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(readTask.getAttachments()).isNotNull();
    assertThat(readTask.getAttachments()).hasSize(2);
    assertThat(readTask.getAttachments().get(1).getCreated()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified())
        .isEqualTo(readTask.getAttachments().get(0).getCreated());
    // assertThat(readTask.getAttachments().get(0).getClassification()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getObjectReference()).isNotNull();
    assertThat(readTask.getReceived()).isEqualTo(earlierInstant);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_SetPriorityAndDurationCorrectly_When_UsingClassificationOfAttachment()
      throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010"); // prio 8, SL P7D
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    newTask.addAttachment(
        createExampleAttachment(
            "DOCTYPE_DEFAULT", // prio 99, SL P2000D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            createSimpleCustomPropertyMap(3)));
    newTask.addAttachment(
        createExampleAttachment(
            "L1060", // prio 1, SL P1D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            createSimpleCustomPropertyMap(3)));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());

    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(readTask.getAttachments()).isNotNull();
    assertThat(readTask.getAttachments()).hasSize(2);
    assertThat(readTask.getAttachments().get(1).getCreated()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified())
        .isEqualTo(readTask.getAttachments().get(0).getCreated());
    // assertThat(readTask.getAttachments().get(0).getClassification()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getObjectReference()).isNotNull();

    assertThat(readTask.getPriority()).isEqualTo(99);

    Instant expDue =
        workingTimeCalculator.addWorkingTime(readTask.getPlanned(), Duration.ofDays(1));

    assertThat(readTask.getDue()).isEqualTo(expDue);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_AttachmentIsInvalid() throws Exception {

    Consumer<Attachment> testCreateTask =
        invalidAttachment -> {
          Task taskWithInvalidAttachment = makeNewTask(taskService);
          taskWithInvalidAttachment.addAttachment(invalidAttachment);
          ThrowingCallable call = () -> taskService.createTask(taskWithInvalidAttachment);
          assertThatThrownBy(call)
              .describedAs(
                  "Should have thrown an InvalidArgumentException, "
                      + "because Attachment-ObjRef is null.")
              .isInstanceOf(InvalidArgumentException.class);
        };

    testCreateTask.accept(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            null,
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            createSimpleCustomPropertyMap(3)));

    testCreateTask.accept(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId", null),
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            createSimpleCustomPropertyMap(3)));

    testCreateTask.accept(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                null,
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            createSimpleCustomPropertyMap(3)));

    testCreateTask.accept(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                null,
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            createSimpleCustomPropertyMap(3)));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UseCustomName_For_NewTask() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setName("Test Name");
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(createdTask.getName()).isEqualTo("Test Name");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UseClassificationMetadataFromCorrectDomain_For_NewTask() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setName("Test Name");
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(createdTask.getPriority()).isEqualTo(2);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowExceptionIfWorkbasketDoesNotExist() {

    Task newTask = taskService.newTask("UNKNOWN");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testGetExceptionIfAppendIsNotPermitted() {

    Task newTask = taskService.newTask("GPK_KSC", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatThrownBy(call).isInstanceOf(MismatchedWorkbasketPermissionException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_MandatoryPrimaryObjectReferenceIsNotSetOrIncomplete() {

    Consumer<ObjectReference> testCreateTask =
        (ObjectReference objectReference) -> {
          Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
          newTask.setClassificationKey("T2100");
          if (objectReference != null) {
            newTask.setPrimaryObjRef(objectReference);
          }
          ThrowingCallable call = () -> taskService.createTask(newTask);
          assertThatThrownBy(call)
              .describedAs(
                  "Should have thrown an InvalidArgumentException, because ObjRef-ObjRef is null.")
              .isInstanceOf(InvalidArgumentException.class);
        };

    testCreateTask.accept(null);
    testCreateTask.accept(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
    testCreateTask.accept(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
    testCreateTask.accept(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_SetDomainFromWorkbasket() throws Exception {

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    final Workbasket workbasket = workbasketService.getWorkbasket("USER-1-1", "DOMAIN_A");

    Task newTask = taskService.newTask("WBI:100000000000000000000000000000000006");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator())
        .isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());
    assertThat(createdTask.getDomain()).isNotNull();
    assertThat(createdTask.getDomain()).isEqualTo(workbasket.getDomain());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReadSameTaskObjectAsCreated() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    for (TaskCustomField taskCustomField : TaskCustomField.values()) {
      newTask.setCustomField(taskCustomField, taskCustomField.name());
    }
    newTask.setCustomAttributeMap(createSimpleCustomPropertyMap(5));
    newTask.setDescription("Description of test task");
    newTask.setNote("My note");
    newTask.addAttachment(
        createExampleAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            Instant.parse("2018-01-15T00:00:00Z"),
            createSimpleCustomPropertyMap(3)));
    Task createdTask = taskService.createTask(newTask);
    Task readTask = taskService.getTask(createdTask.getId());

    assertThat(readTask).isEqualTo(createdTask);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateSimpleTask_When_CallbackInfoIsSet() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    Map<String, String> callbackInfo = createSimpleCustomPropertyMap(10);
    newTask.setCallbackInfo(callbackInfo);
    Task createdTask = taskService.createTask(newTask);
    Instant expectedPlanned = moveForwardToWorkingDay(createdTask.getCreated());

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getPrimaryObjRef().getValue()).isEqualTo("1234567");
    assertThat(createdTask.getCreated()).isNotNull();
    assertThat(createdTask.getModified()).isNotNull();
    assertThat(createdTask.getBusinessProcessId()).isNotNull();
    assertThat(createdTask.getClaimed()).isNull();
    assertThat(createdTask.getCompleted()).isNull();
    assertThat(createdTask.getModified()).isEqualTo(createdTask.getCreated());
    assertThat(createdTask.getPlanned()).isEqualTo(expectedPlanned);
    assertThat(createdTask.getState()).isEqualTo(TaskState.READY);
    assertThat(createdTask.getParentBusinessProcessId()).isNull();
    assertThat(createdTask.getPriority()).isEqualTo(2);
    assertThat(createdTask.isRead()).isFalse();
    assertThat(createdTask.isTransferred()).isFalse();

    Task retrievedTask = taskService.getTask(createdTask.getId());
    assertThat(retrievedTask.getCallbackInfo()).isEqualTo(callbackInfo);
  }

  @Test
  void should_ThrowException_When_NoUserIdIsSetAndSecurityIsOn() {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_B", "SYSTEM_B", "INSTANCE_B", "VNR", "1234567"));

    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatThrownBy(call).isInstanceOf(MismatchedWorkbasketPermissionException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CreatingTaskWithNonEmptyId() {

    Task newTask = taskService.newTask();
    ((TaskImpl) newTask).setId("TKI:000000000000000000000000000000000000");

    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserNotAuthorizedOnWorkbasket() {

    Task task = taskService.newTask("TEAMLEAD-2", "DOMAIN_A");

    ThrowingCallable call = () -> taskService.createTask(task);
    assertThatThrownBy(call).isInstanceOf(MismatchedWorkbasketPermissionException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_NotThrowNullPointerException_When_CreatingTaskWithoutWorkbasketSummary() {
    Task task = new TaskImpl();

    ThrowingCallable call = () -> taskService.createTask(task);
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowException_When_WorkbasketMarkedForDeletion() throws Exception {

    String wbId = "WBI:100000000000000000000000000000000008";
    Task taskToPreventWorkbasketDeletion = taskService.newTask(wbId);
    setTaskProperties(taskToPreventWorkbasketDeletion);
    taskService.createTask(taskToPreventWorkbasketDeletion);
    taskService.cancelTask(taskToPreventWorkbasketDeletion.getId());
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    workbasketService.deleteWorkbasket(wbId);
    Task task = taskService.newTask(wbId);
    final Task testTask = setTaskProperties(task);
    assertThatThrownBy(() -> taskService.createTask(testTask))
        .isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CreatingTaskWithAttachmentClassificationNull() {
    TaskImpl task = (TaskImpl) makeNewTask(taskService);
    Attachment attachment = taskService.newAttachment();
    attachment.setObjectReference(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    task.addAttachment(attachment);

    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Classification of Attachment must not be null.");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CreatingTaskWithAttachmentObjectReferenceNull() {
    TaskImpl task = (TaskImpl) makeNewTask(taskService);
    Attachment attachment = taskService.newAttachment();
    attachment.setClassificationSummary(task.getClassificationSummary());
    task.addAttachment(attachment);

    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessageContaining("ObjectReference of Attachment must not be null.");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CreatingTaskWithNotExistingAttachmentClassification() {
    Attachment attachment = taskService.newAttachment();
    attachment.setObjectReference(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    ClassificationSummary classification =
        classificationService.newClassification("NOT_EXISTING", "DOMAIN_A", "").asSummary();
    attachment.setClassificationSummary(classification);

    TaskImpl task = (TaskImpl) makeNewTask(taskService);
    task.addAttachment(attachment);

    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CreatingTaskWithMissingAttachmentClassificationKey() {
    Attachment attachment = taskService.newAttachment();
    attachment.setObjectReference(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    ClassificationSummaryImpl classification = new ClassificationSummaryImpl();
    attachment.setClassificationSummary(classification);

    TaskImpl task = (TaskImpl) makeNewTask(taskService);
    task.addAttachment(attachment);
    assertThatThrownBy(() -> taskService.createTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessageContaining("ClassificationKey of Attachment must not be empty.");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FetchAttachmentClassification_When_CreatingTaskWithAttachments() throws Exception {
    Attachment attachment = taskService.newAttachment();
    attachment.setObjectReference(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    ClassificationSummary classification =
        classificationService.newClassification("T2000", "DOMAIN_A", "").asSummary();
    attachment.setClassificationSummary(classification);
    TaskImpl task = (TaskImpl) makeNewTask(taskService);
    task.addAttachment(attachment);

    assertThat(classification.getServiceLevel()).isNull();

    task = (TaskImpl) taskService.createTask(task);
    classification = task.getAttachments().get(0).getClassificationSummary();

    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getDomain()).isNotNull();
    assertThat(classification.getServiceLevel()).isNotNull();
  }

  private Task setTaskProperties(Task task) {
    task.setClassificationKey("L12010");
    task.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    return task;
  }

  private Task makeNewTask(TaskService taskService) {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    return newTask;
  }
}
