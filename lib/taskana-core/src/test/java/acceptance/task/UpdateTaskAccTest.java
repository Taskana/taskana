package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_1;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_10;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_12;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_14;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_16;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_2;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_3;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_5;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_7;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;

/** Acceptance test for all "update task" scenarios. */
@ExtendWith(JaasExtension.class)
class UpdateTaskAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdatePrimaryObjectReferenceOfTask_When_Requested() throws Exception {

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    final Instant modifiedOriginal = task.getModified();
    task.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "7654321"));
    task.setBusinessProcessId("MY_PROCESS_ID");
    task.setParentBusinessProcessId("MY_PARENT_PROCESS_ID");
    Task updatedTask = taskService.updateTask(task);
    updatedTask = taskService.getTask(updatedTask.getId());

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getPrimaryObjRef().getValue()).isEqualTo("7654321");
    assertThat(updatedTask.getCreated()).isNotNull();
    assertThat(updatedTask.getModified()).isNotNull();
    assertThat(modifiedOriginal.isAfter(updatedTask.getModified())).isFalse();
    assertThat(updatedTask.getModified()).isNotEqualTo(updatedTask.getCreated());
    assertThat(updatedTask.getCreated()).isEqualTo(task.getCreated());
    assertThat(updatedTask.isRead()).isEqualTo(task.isRead());
    assertThat(updatedTask.getBusinessProcessId()).isEqualTo("MY_PROCESS_ID");
    assertThat(updatedTask.getParentBusinessProcessId()).isEqualTo("MY_PARENT_PROCESS_ID");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_PreventTimestampServiceLevelMismatch_When_ConfigurationPreventsIt() throws Exception {
    // Given

    taskanaEngineConfiguration.setValidationAllowTimestampServiceLevelMismatch(false);
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    // When
    Instant planned = Instant.parse("2018-03-02T00:00:00Z");
    task.setPlanned(planned);
    Instant due = Instant.parse("2018-04-15T00:00:00Z");
    task.setDue(due);

    // Then
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessageContaining("not matching the service level");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_AllowTimestampServiceLevelMismatch_When_ConfigurationAllowsIt() throws Exception {
    try {
      // Given
      taskanaEngineConfiguration.setValidationAllowTimestampServiceLevelMismatch(true);
      Task task = taskService.getTask("TKI:000000000000000000000000000000000000");

      // When
      Instant planned = Instant.parse("2018-01-02T00:00:00Z");
      task.setPlanned(planned);
      Instant due = Instant.parse("2018-02-15T00:00:00Z");
      task.setDue(due);
      Task updateTask = taskService.updateTask(task);

      // Then
      assertThat(updateTask.getPlanned()).isEqualTo(planned);
      assertThat(updateTask.getDue()).isEqualTo(due);
    } finally {
      taskanaEngineConfiguration.setValidationAllowTimestampServiceLevelMismatch(false);
    }
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdatePrimaryObjectReferenceOfTask_When_ObjectReferenceSystemAndSystemInstanceIsNull()
      throws Exception {

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    final Instant modifiedOriginal = task.getModified();
    task.setPrimaryObjRef(createObjectReference("COMPANY_A", null, null, "VNR", "7654321"));
    task.setBusinessProcessId("MY_PROCESS_ID");
    task.setParentBusinessProcessId("MY_PARENT_PROCESS_ID");
    Task updatedTask = taskService.updateTask(task);
    updatedTask = taskService.getTask(updatedTask.getId());

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getPrimaryObjRef().getValue()).isEqualTo("7654321");
    assertThat(updatedTask.getPrimaryObjRef().getSystem()).isNull();
    assertThat(updatedTask.getPrimaryObjRef().getSystemInstance()).isNull();
    assertThat(updatedTask.getCreated()).isNotNull();
    assertThat(updatedTask.getModified()).isNotNull();
    assertThat(modifiedOriginal.isAfter(updatedTask.getModified())).isFalse();
    assertThat(updatedTask.getModified()).isNotEqualTo(updatedTask.getCreated());
    assertThat(updatedTask.getCreated()).isEqualTo(task.getCreated());
    assertThat(updatedTask.isRead()).isEqualTo(task.isRead());
    assertThat(updatedTask.getBusinessProcessId()).isEqualTo("MY_PROCESS_ID");
    assertThat(updatedTask.getParentBusinessProcessId()).isEqualTo("MY_PARENT_PROCESS_ID");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_MandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
      throws Exception {

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.setPrimaryObjRef(null);
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);

    Task task1 = taskService.getTask("TKI:000000000000000000000000000000000000");
    task1.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
    assertThatThrownBy(() -> taskService.updateTask(task1))
        .isInstanceOf(InvalidArgumentException.class);

    Task task2 = taskService.getTask("TKI:000000000000000000000000000000000000");
    task2.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task2))
        .isInstanceOf(InvalidArgumentException.class);

    Task task3 = taskService.getTask("TKI:000000000000000000000000000000000000");
    task3.setPrimaryObjRef(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task3))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_TaskHasAlreadyBeenUpdated() throws Exception {

    String taskId = "TKI:000000000000000000000000000000000000";
    Task task = taskService.getTask(taskId);
    final Task task2 = taskService.getTask(taskId);

    task.setCustomAttribute(CUSTOM_1, "willi");
    Thread.sleep(10);
    taskService.updateTask(task);
    task2.setCustomAttribute(CUSTOM_2, "Walter");
    // TODO flaky test ... if speed is too high,
    assertThatThrownBy(() -> taskService.updateTask(task2))
        .isInstanceOf(ConcurrencyException.class)
        .hasMessage(
            "The entity with id '%s' cannot be updated since it has been modified while editing.",
            taskId);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_UpdateTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.setClassificationKey("T2100");
    ThrowingCallable updateTaskCall = () -> taskService.updateTask(task);
    assertThatCode(updateTaskCall).doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateTaskProperties_When_ClassificationOfTaskIsChanged() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    final ClassificationSummary classificationSummary = task.getClassificationSummary();
    task.setClassificationKey("T2100");
    Task updatedTask = taskService.updateTask(task);
    updatedTask = taskService.getTask(updatedTask.getId());

    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getClassificationSummary().getKey()).isEqualTo("T2100");
    assertThat(updatedTask.getClassificationSummary()).isNotEqualTo(classificationSummary);
    assertThat(updatedTask.getCreated()).isNotEqualTo(updatedTask.getModified());
    assertThat(task.getPlanned()).isEqualTo(updatedTask.getPlanned());
    assertThat(task.getName()).isEqualTo(updatedTask.getName());
    assertThat(task.getDescription()).isEqualTo(updatedTask.getDescription());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_UpdateReadFlagOfTask_When_SetTaskReadIsCalled() throws Exception {

    taskService.setTaskRead("TKI:000000000000000000000000000000000030", true);
    Task updatedTask = taskService.getTask("TKI:000000000000000000000000000000000030");
    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.isRead()).isTrue();
    assertThat(updatedTask.getCreated()).isNotEqualTo(updatedTask.getModified());

    taskService.setTaskRead("TKI:000000000000000000000000000000000030", false);
    Task updatedTask2 = taskService.getTask("TKI:000000000000000000000000000000000030");
    assertThat(updatedTask2).isNotNull();
    assertThat(updatedTask2.isRead()).isFalse();
    assertThat(updatedTask2.getModified().isBefore(updatedTask.getModified())).isFalse();

    assertThatThrownBy(() -> taskService.setTaskRead("INVALID", true))
        .isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateTask_When_CustomPropertiesOfTaskWereChanged() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.setCustomAttribute(CUSTOM_1, "T2100");
    Task updatedTask = taskService.updateTask(task);
    updatedTask = taskService.getTask(updatedTask.getId());

    assertThat(updatedTask).isNotNull();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ModificationOfWorkbasketKeyIsAttempted() throws Exception {

    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    ((TaskImpl) task).setWorkbasketKey("USER-2-2");

    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateNoTasks_When_UpdateTasksWithUnmatchedObjectReferenceWasCalled()
      throws Exception {
    ObjectReference por = new ObjectReference();
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

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_UpdateTasks_When_MatchingPrimaryObjectReferenceWasChanged() throws Exception {
    ObjectReference por = new ObjectReference();
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
    assertThat(taskIds).hasSize(6);
    for (String taskId : taskIds) {
      Task task = taskService.getTask(taskId);
      assertThat(task.getCustomAttribute(CUSTOM_3)).isEqualTo("This is modifiedValue 3");
      assertThat(task.getCustomAttribute(CUSTOM_7)).isEqualTo("This is modifiedValue 7");
      assertThat(task.getCustomAttribute(CUSTOM_16)).isEqualTo("This is modifiedValue 16");
      assertThat(task.getCustomAttribute(CUSTOM_14)).isNull();
    }
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_UpdateTaskCustomAttributes_When_UpdateTasksIsCalled() throws Exception {
    List<String> taskIds =
        List.of(
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000010");
    Map<TaskCustomField, String> customProperties = new HashMap<>();
    customProperties.put(CUSTOM_1, "This is modifiedValue 1");
    customProperties.put(CUSTOM_5, "This is modifiedValue 5");
    customProperties.put(CUSTOM_10, "This is modifiedValue 10");
    customProperties.put(CUSTOM_12, "This is modifiedValue 12");

    List<String> changedTasks = taskService.updateTasks(taskIds, customProperties);
    assertThat(changedTasks).hasSize(3);
    for (String taskId : changedTasks) {
      Task task = taskService.getTask(taskId);
      assertThat(task.getCustomAttribute(CUSTOM_1)).isEqualTo("This is modifiedValue 1");
      assertThat(task.getCustomAttribute(CUSTOM_5)).isEqualTo("This is modifiedValue 5");
      assertThat(task.getCustomAttribute(CUSTOM_10)).isEqualTo("This is modifiedValue 10");
      assertThat(task.getCustomAttribute(CUSTOM_12)).isEqualTo("This is modifiedValue 12");
      assertThat(task.getCustomAttribute(CUSTOM_2)).isNull();
    }
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateCallbackInfo_When_RequestedByApi() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

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

    HashMap<String, String> callbackInfo = new HashMap<>();
    for (int i = 1; i <= 10; i++) {
      callbackInfo.put("info_" + i, "Value of info_" + i);
    }
    retrievedTask.setCallbackInfo(callbackInfo);
    taskService.updateTask(retrievedTask);

    Task retrievedUpdatedTask = taskService.getTask(createdTask.getId());

    assertThat(retrievedUpdatedTask.getCallbackInfo()).isEqualTo(callbackInfo);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateRetrieved_When_UpdateTaskIsCalled() throws Exception {

    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));

    Task createdTask = taskService.createTask(newTask);

    Task retrievedTask = taskService.getTask(createdTask.getId());
    Instant retrievedTime = Instant.parse("2019-09-13T08:44:17.588Z");
    retrievedTask.setReceived(retrievedTime);
    taskService.updateTask(retrievedTask);

    Task retrievedUpdatedTask = taskService.getTask(createdTask.getId());

    assertThat(retrievedUpdatedTask).extracting(TaskSummary::getReceived).isEqualTo(retrievedTime);
  }
}
