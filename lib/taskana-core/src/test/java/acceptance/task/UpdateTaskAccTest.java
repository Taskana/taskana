package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "update task" scenarios. */
@ExtendWith(JaasExtension.class)
class UpdateTaskAccTest extends AbstractAccTest {

  UpdateTaskAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdatePrimaryObjectReferenceOfTask()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          TaskNotFoundException, ConcurrencyException, AttachmentPersistenceException {

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

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfMandatoryPrimaryObjectReferenceIsNotSetOrIncomplete()
      throws NotAuthorizedException, ClassificationNotFoundException, TaskNotFoundException,
          ConcurrencyException, AttachmentPersistenceException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.setPrimaryObjRef(null);
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);

    task.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", null));
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);

    task.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", null, "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);

    task.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", null, "VNR", "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);

    task.setPrimaryObjRef(createObjectReference("COMPANY_A", null, "INSTANCE_A", "VNR", "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);

    task.setPrimaryObjRef(createObjectReference(null, "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfTaskHasAlreadyBeenUpdated()
      throws NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
          TaskNotFoundException, ConcurrencyException, AttachmentPersistenceException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    Task task2 = taskService.getTask("TKI:000000000000000000000000000000000000");

    task.setCustomAttribute("1", "willi");
    taskService.updateTask(task);

    task2.setCustomAttribute("2", "Walter");
    // TODO flaky test ... if speed is too high,
    assertThatThrownBy(() -> taskService.updateTask(task2))
        .isInstanceOf(ConcurrencyException.class)
        .withFailMessage("The task has already been updated by another user");
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateClassificationOfTask()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException {

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
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testUpdateReadFlagOfTask() throws TaskNotFoundException, NotAuthorizedException {

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

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCustomPropertiesOfTask()
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    task.setCustomAttribute("1", "T2100");
    Task updatedTask = taskService.updateTask(task);
    updatedTask = taskService.getTask(updatedTask.getId());

    assertThat(updatedTask).isNotNull();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateOfWorkbasketKeyWhatIsNotAllowed()
      throws NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    ((TaskImpl) task).setWorkbasketKey("USER_2_2");

    assertThatThrownBy(() -> taskService.updateTask(task))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateTasksByPorForUser1() throws InvalidArgumentException {
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

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateTasksByPor()
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

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateTasksById()
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

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateCallbackInfoOfSimpleTask()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          ConcurrencyException, AttachmentPersistenceException {

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

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testSetOwnerAndSubsequentClaimSucceeds()
      throws TaskNotFoundException, NotAuthorizedException, InvalidStateException,
          InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    String taskReadyId = "TKI:000000000000000000000000000000000025";
    Task taskReady = taskService.getTask(taskReadyId);
    assertThat(taskReady.getState()).isEqualTo(TaskState.READY);
    assertThat(taskReady.getOwner()).isNull();
    Task modifiedTaskReady = taskService.setOwner(taskReadyId, "Holger");
    assertThat(modifiedTaskReady.getState()).isEqualTo(TaskState.READY);
    assertThat(modifiedTaskReady.getOwner()).isEqualTo("Holger");
    Task taskClaimed = taskService.claim(taskReadyId);
    assertThat(taskClaimed.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(taskClaimed.getOwner()).isEqualTo("user_1_2");
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testSetOwnerNotAuthorized()
      throws TaskNotFoundException, NotAuthorizedException, InvalidStateException,
          InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    String taskReadyId = "TKI:000000000000000000000000000000000024";
    assertThatThrownBy(() -> taskService.getTask(taskReadyId))
        .isInstanceOf(NotAuthorizedException.class);
    assertThatThrownBy(() -> taskService.setOwner(taskReadyId, "Holger"))
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testSetOwnerOfClaimedTaskFails()
      throws TaskNotFoundException, NotAuthorizedException, InvalidStateException,
          InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    String taskClaimedId = "TKI:000000000000000000000000000000000026";
    Task taskClaimed = taskService.getTask(taskClaimedId);
    assertThat(taskClaimed.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(taskClaimed.getOwner()).isEqualTo("user_1_1");
    assertThatThrownBy(() -> taskService.setOwner(taskClaimedId, "Holger"))
        .isInstanceOf(InvalidStateException.class);
  }
}
