package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance test for all "update task" scenarios. */
@ExtendWith(JaasExtension.class)
class UpdateTaskAccTest extends AbstractAccTest {

  UpdateTaskAccTest() {
    super();
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void should_UpdatePrimaryObjectReferenceOfTask_When_Requested()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          TaskNotFoundException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException {

    TaskService taskService = taskanaEngine.getTaskService();
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

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void should_ThrowException_When_MandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
      throws NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();
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
    task3.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task3))
        .isInstanceOf(InvalidArgumentException.class);

    Task task4 = taskService.getTask("TKI:000000000000000000000000000000000000");
    task4.setPrimaryObjRef(
        createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task4))
        .isInstanceOf(InvalidArgumentException.class);

    Task task5 = taskService.getTask("TKI:000000000000000000000000000000000000");
    task5.setPrimaryObjRef(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task5))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void should_ThrowException_When_TaskHasAlreadyBeenUpdated()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          TaskNotFoundException, ConcurrencyException, AttachmentPersistenceException,
          InvalidStateException, InterruptedException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    final Task task2 = taskService.getTask("TKI:000000000000000000000000000000000000");

    task.setCustomAttribute("1", "willi");
    Thread.sleep(10);
    taskService.updateTask(task);
    task2.setCustomAttribute("2", "Walter");
    // TODO flaky test ... if speed is too high,
    assertThatThrownBy(() -> taskService.updateTask(task2))
        .isInstanceOf(ConcurrencyException.class)
        .withFailMessage("The task has already been updated by another user");
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void should_UpdateTaskProperties_When_ClassificationOfTaskIsChanged()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException, SQLException {

    TaskService taskService = taskanaEngine.getTaskService();
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
    resetDb(false); // classification of task TKI:0..00 was changed...
  }

  @WithAccessId(user = "user_1_2", groups = "group_1")
  @Test
  void should_UpdateReadFlagOfTask_When_SetTaskReadIsCalled()
      throws TaskNotFoundException, NotAuthorizedException {

    TaskService taskService = taskanaEngine.getTaskService();

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

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void should_UpdateTask_When_CustomPropertiesOfTaskWereChanged()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.setCustomAttribute("1", "T2100");
    Task updatedTask = taskService.updateTask(task);
    updatedTask = taskService.getTask(updatedTask.getId());

    assertThat(updatedTask).isNotNull();
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void should_ThrowException_When_ModificationOfWorkbasketKeyIsAttempted()
      throws NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    ((TaskImpl) task).setWorkbasketKey("USER_2_2");

    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void should_UpdateNoTasks_When_UpdateTasksWithUnmatchedObjectReferenceWasCalled()
      throws InvalidArgumentException {
    ObjectReference por = new ObjectReference();
    por.setCompany("00");
    por.setSystem("PASystem");
    por.setSystemInstance("00");
    por.setType("VNR");
    por.setValue("22334455");
    Map<String, String> customProperties = new HashMap<>();
    customProperties.put("7", "This is modifiedValue 7");
    customProperties.put("14", null);
    customProperties.put("3", "This is modifiedValue 3");
    customProperties.put("16", "This is modifiedValue 16");
    TaskService taskService = taskanaEngine.getTaskService();

    List<String> taskIds = taskService.updateTasks(por, customProperties);
    assertThat(taskIds).isEmpty();
  }

  @WithAccessId(user = "teamlead_1", groups = "group_1")
  @Test
  void should_UpdateTasks_When_MatchingPrimaryObjectReferenceWasChanged()
      throws InvalidArgumentException, TaskNotFoundException, NotAuthorizedException {
    ObjectReference por = new ObjectReference();
    por.setCompany("00");
    por.setSystem("PASystem");
    por.setSystemInstance("00");
    por.setType("VNR");
    por.setValue("22334455");
    Map<String, String> customProperties = new HashMap<>();
    customProperties.put("7", "This is modifiedValue 7");
    customProperties.put("14", null);
    customProperties.put("3", "This is modifiedValue 3");
    customProperties.put("16", "This is modifiedValue 16");
    TaskService taskService = taskanaEngine.getTaskService();

    List<String> taskIds = taskService.updateTasks(por, customProperties);
    assertThat(taskIds).hasSize(6);
    for (String taskId : taskIds) {
      Task task = taskService.getTask(taskId);
      assertThat(task.getCustomAttribute("3")).isEqualTo("This is modifiedValue 3");
      assertThat(task.getCustomAttribute("7")).isEqualTo("This is modifiedValue 7");
      assertThat(task.getCustomAttribute("16")).isEqualTo("This is modifiedValue 16");
      assertThat(task.getCustomAttribute("14")).isNull();
    }
  }

  @WithAccessId(user = "teamlead_1", groups = "group_1")
  @Test
  void should_UpdateTaskCustomAttributes_When_UpdateTasksIsCalled()
      throws InvalidArgumentException, TaskNotFoundException, NotAuthorizedException {
    List<String> taskIds =
        Arrays.asList(
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000010");
    Map<String, String> customProperties = new HashMap<>();
    customProperties.put("1", "This is modifiedValue 1");
    customProperties.put("5", "This is modifiedValue 5");
    customProperties.put("10", "This is modifiedValue 10");
    customProperties.put("12", "This is modifiedValue 12");
    TaskService taskService = taskanaEngine.getTaskService();

    List<String> changedTasks = taskService.updateTasks(taskIds, customProperties);
    assertThat(changedTasks).hasSize(3);
    for (String taskId : changedTasks) {
      Task task = taskService.getTask(taskId);
      assertThat(task.getCustomAttribute("1")).isEqualTo("This is modifiedValue 1");
      assertThat(task.getCustomAttribute("5")).isEqualTo("This is modifiedValue 5");
      assertThat(task.getCustomAttribute("10")).isEqualTo("This is modifiedValue 10");
      assertThat(task.getCustomAttribute("12")).isEqualTo("This is modifiedValue 12");
      assertThat(task.getCustomAttribute("2")).isNull();
    }
  }

  @WithAccessId(user = "user_1_1", groups = "group_1")
  @Test
  void should_UpdateCallbackInfo_When_RequestedByApi()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          ConcurrencyException, AttachmentPersistenceException, InvalidStateException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.getPrimaryObjRef().getValue()).isEqualTo("1234567");
    assertThat(createdTask.getCreated()).isNotNull();
    assertThat(createdTask.getModified()).isNotNull();
    assertThat(createdTask.getBusinessProcessId()).isNotNull();
    assertThat(createdTask.getClaimed()).isNull();
    assertThat(createdTask.getCompleted()).isNull();
    assertThat(createdTask.getModified()).isEqualTo(createdTask.getCreated());
    assertThat(createdTask.getPlanned()).isEqualTo(createdTask.getCreated());
    assertThat(createdTask.getState()).isEqualTo(TaskState.READY);
    assertThat(createdTask.getParentBusinessProcessId()).isNull();
    assertThat(createdTask.getPriority()).isEqualTo(2);
    assertThat(createdTask.isRead()).isEqualTo(false);
    assertThat(createdTask.isTransferred()).isEqualTo(false);

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
}
