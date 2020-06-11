package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.TaskanaEngineProxyForTest;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.AttachmentMapper;
import pro.taskana.task.internal.TaskTestMapper;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
class CreateTaskAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void should_beAbleToCreateNewTask_When_TaskCopy() throws Exception {
    Task oldTask = taskService.getTask("TKI:000000000000000000000000000000000000");

    Task newTask = oldTask.copy();
    newTask = taskService.createTask(newTask);

    assertThat(newTask.getId()).isNotNull();
    assertThat(newTask.getId()).isNotEqualTo(oldTask.getId());
    assertThat(newTask.getAttachments())
        .extracting(AttachmentSummary::getTaskId)
        .containsOnly(newTask.getId());
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testCreateSimpleManualTask()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    newTask.setOwner("user-1-1");
    Task createdTask = taskService.createTask(newTask);
    Instant expectedPlanned = moveForwardToWorkingDay(createdTask.getCreated());

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
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
    assertThat(createdTask.getState()).isEqualTo(TaskState.READY);
    assertThat(createdTask.getParentBusinessProcessId()).isNull();
    assertThat(createdTask.getPriority()).isEqualTo(2);
    assertThat(createdTask.isRead()).isFalse();
    assertThat(createdTask.isTransferred()).isFalse();
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void should_createTask_When_ObjectReferenceSystemAndSystemInstanceIsNull()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException {

    String currentUser = CurrentUserContext.getUserid();

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("COMPANY_A", null, null, "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    newTask.setOwner(currentUser);
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_CreateTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws TaskAlreadyExistException, InvalidArgumentException, WorkbasketNotFoundException,
          NotAuthorizedException, ClassificationNotFoundException {

    String currentUser = CurrentUserContext.getUserid();

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    ObjectReference objectReference =
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567");
    newTask.setPrimaryObjRef(objectReference);
    newTask.setOwner(currentUser);
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testIdempotencyOfTaskCreation()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setExternalId("MyExternalId");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);
    Instant expectedPlanned = moveForwardToWorkingDay(createdTask.getCreated());

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
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

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testCreateSimpleTaskWithCustomAttributes()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException,
          NoSuchFieldException, IllegalAccessException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Map<String, String> customAttributesForCreate = createSimpleCustomProperties(13);
    newTask.setCustomAttributes(customAttributesForCreate);
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
    assertThat(createdTask.getState()).isEqualTo(TaskState.READY);
    assertThat(createdTask.getParentBusinessProcessId()).isNull();
    assertThat(createdTask.getPriority()).isEqualTo(2);
    assertThat(createdTask.isRead()).isFalse();
    assertThat(createdTask.isTransferred()).isFalse();
    // verify that the database content is as expected
    TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest(taskanaEngine);
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
    Map<String, String> customAttributesFromDb = retrievedTask.getCustomAttributes();
    assertThat(customAttributesFromDb).isNotNull();
    assertThat(customAttributesForCreate).isEqualTo(customAttributesFromDb);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testCreateExternalTaskWithAttachment()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException,
          NoSuchFieldException, IllegalAccessException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010");
    Map<String, String> customAttributesForCreate = createSimpleCustomProperties(27);
    newTask.addAttachment(
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            customAttributesForCreate));
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);
    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());

    // verify that the database content is as expected
    TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest(taskanaEngine);
    try {
      SqlSession session = engineProxy.getSqlSession();
      AttachmentMapper mapper = session.getMapper(AttachmentMapper.class);
      engineProxy.openConnection();
      String customProperties =
          mapper.getCustomAttributesAsString(createdTask.getAttachments().get(0).getId());
      assertThat(customProperties.contains("\"Property_26\":\"Property Value of Property_26\""))
          .isTrue();
      assertThat(customProperties.contains("\"Property_25\":\"Property Value of Property_25\""))
          .isTrue();
      assertThat(customProperties.contains("\"Property_21\":\"Property Value of Property_21\""))
          .isTrue();
      assertThat(customProperties.contains("\"Property_19\":\"Property Value of Property_19\""))
          .isTrue();
      assertThat(customProperties.contains("\"Property_16\":\"Property Value of Property_16\""))
          .isTrue();
      assertThat(customProperties.contains("\"Property_12\":\"Property Value of Property_12\""))
          .isTrue();
      assertThat(customProperties.contains("\"Property_11\":\"Property Value of Property_11\""))
          .isTrue();
      assertThat(customProperties.contains("\"Property_7\":\"Property Value of Property_7\""))
          .isTrue();
      assertThat(customProperties.contains("\"Property_6\":\"Property Value of Property_6\""))
          .isTrue();
    } finally {
      engineProxy.returnConnection();
    }

    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
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
        readTask.getAttachments().get(0).getCustomAttributes();
    assertThat(customAttributesFromDb).isNotNull();
    assertThat(customAttributesForCreate).isEqualTo(customAttributesFromDb);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testCreateExternalTaskWithMultipleAttachments()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.addAttachment(
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));
    newTask.addAttachment(
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());

    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
    assertThat(readTask.getAttachments()).isNotNull();
    assertThat(readTask.getAttachments()).hasSize(2);
    assertThat(readTask.getAttachments().get(1).getCreated()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified()).isNotNull();
    assertThat(readTask.getAttachments().get(1).getModified())
        .isEqualTo(readTask.getAttachments().get(0).getCreated());
    // assertThat(readTask.getAttachments().get(0).getClassification()).isNotNull();
    assertThat(readTask.getAttachments().get(0).getObjectReference()).isNotNull();
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testPrioDurationOfTaskFromAttachmentsAtCreate()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010"); // prio 8, SL P7D
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    newTask.addAttachment(
        createAttachment(
            "DOCTYPE_DEFAULT", // prio 99, SL P2000D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));
    newTask.addAttachment(
        createAttachment(
            "L1060", // prio 1, SL P1D
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask.getId()).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());

    Task readTask = taskService.getTask(createdTask.getId());
    assertThat(readTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
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
        converter.addWorkingDaysToInstant(readTask.getPlanned(), Duration.ofDays(1));

    assertThat(readTask.getDue()).isEqualTo(expDue);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testThrowsExceptionIfAttachmentIsInvalid() throws ClassificationNotFoundException {

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
        createAttachment(
            "DOCTYPE_DEFAULT", null, "E-MAIL", "2018-01-15", createSimpleCustomProperties(3)));

    testCreateTask.accept(
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId", null),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));

    testCreateTask.accept(
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                null,
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));

    testCreateTask.accept(
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                null,
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testUseCustomNameIfSetForNewTask()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setName("Test Name");
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
    assertThat(createdTask.getName()).isEqualTo("Test Name");
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testUseClassificationMetadataFromCorrectDomainForNewTask()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setName("Test Name");
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
    assertThat(createdTask.getPriority()).isEqualTo(2);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testGetExceptionIfWorkbasketDoesNotExist() {

    Task newTask = taskService.newTask("UNKNOWN");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testGetExceptionIfAppendIsNotPermitted() {

    Task newTask = taskService.newTask("GPK_KSC", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testThrowsExceptionIfMandatoryPrimaryObjectReferenceIsNotSetOrIncomplete() {

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

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testSetDomainFromWorkbasket()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException {

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    final Workbasket workbasket = workbasketService.getWorkbasket("USER-1-1", "DOMAIN_A");

    Task newTask = taskService.newTask("WBI:100000000000000000000000000000000006");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getCreator()).isEqualTo(CurrentUserContext.getUserid());
    assertThat(createdTask.getDomain()).isNotNull();
    assertThat(createdTask.getDomain()).isEqualTo(workbasket.getDomain());
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testCreatedTaskObjectEqualsReadTaskObject()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          WorkbasketNotFoundException, TaskAlreadyExistException, TaskNotFoundException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    for (int i = 1; i < 16; i++) {
      newTask.setCustomAttribute(Integer.toString(i), "VALUE " + i);
    }
    newTask.setCustomAttributes(createSimpleCustomProperties(5));
    newTask.setDescription("Description of test task");
    newTask.setNote("My note");
    newTask.addAttachment(
        createAttachment(
            "DOCTYPE_DEFAULT",
            createObjectReference(
                "COMPANY_A",
                "SYSTEM_B",
                "INSTANCE_B",
                "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL",
            "2018-01-15",
            createSimpleCustomProperties(3)));
    Task createdTask = taskService.createTask(newTask);
    Task readTask = taskService.getTask(createdTask.getId());

    assertThat(readTask).isEqualTo(createdTask);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testCreateSimpleTaskWithCallbackInfo()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    Map<String, String> callbackInfo = createSimpleCustomProperties(10);
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
  void testCreateTaskWithSecurityButNoUserId() {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_B", "SYSTEM_B", "INSTANCE_B", "VNR", "1234567"));

    ThrowingCallable call = () -> taskService.createTask(newTask);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testCreateTaskAlreadyExisting() throws NotAuthorizedException, TaskNotFoundException {

    Task existingTask = taskService.getTask("TKI:000000000000000000000000000000000000");

    ThrowingCallable call = () -> taskService.createTask(existingTask);
    assertThatThrownBy(call).isInstanceOf(TaskAlreadyExistException.class);
  }

  @WithAccessId(user = "user-1-1", groups = "group-1")
  @Test
  void testCreateTaskNotAuthorizedOnWorkbasket() {

    Task task = taskService.newTask("TEAMLEAD-2", "DOMAIN_A");

    ThrowingCallable call = () -> taskService.createTask(task);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin", groups = "group-1")
  @Test
  void testCreateTaskWithWorkbasketMarkedForDeletion()
      throws NotAuthorizedException, InvalidStateException, TaskNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, WorkbasketNotFoundException,
          ClassificationNotFoundException, WorkbasketInUseException {

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
    newTask.setClassificationKey("L12010");
    return newTask;
  }
}
