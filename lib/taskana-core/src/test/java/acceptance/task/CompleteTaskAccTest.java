package acceptance.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.InvalidOwnerException;
import pro.taskana.common.api.exceptions.InvalidStateException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance tests for all claim and complete scenarios. */
@ExtendWith(JaasExtension.class)
class CompleteTaskAccTest extends AbstractAccTest {

  CompleteTaskAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCompleteTask()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    TaskService taskService = taskanaEngine.getTaskService();

    assertEquals(
        TaskState.CLAIMED,
        taskService.getTask("TKI:000000000000000000000000000000000001").getState());

    Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000001");
    assertNotNull(completedTask);
    assertNotNull(completedTask.getCompleted());
    assertEquals(TaskState.COMPLETED, completedTask.getState());
    assertNotEquals(completedTask.getCreated(), completedTask.getModified());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCompleteTaskTwice()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000002");
    Task completedTask2 = taskService.completeTask("TKI:000000000000000000000000000000000002");
    assertEquals(completedTask, completedTask2);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testForceCompleteAlreadyClaimed()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidOwnerException, InvalidStateException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setOwner("other");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    TaskImpl newTaskImpl = (TaskImpl) newTask;
    newTaskImpl.setState(TaskState.CLAIMED);
    newTaskImpl.setClaimed(Instant.now());

    Task createdTask = taskService.createTask(newTaskImpl);
    Task completedTask = taskService.forceCompleteTask(createdTask.getId());

    assertEquals(TaskState.COMPLETED, completedTask.getState());
    assertNotNull(completedTask.getCompleted());
    assertTrue(isBeforeOrEqual(completedTask.getCreated(), completedTask.getModified()));
    assertEquals(completedTask.getModified(), completedTask.getCompleted());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testForceCompleteNotClaimed()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidOwnerException, InvalidStateException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setOwner("other");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    TaskImpl newTaskImpl = (TaskImpl) newTask;
    newTaskImpl.setClaimed(Instant.now());

    Task createdTask = taskService.createTask(newTaskImpl);
    Task completedTask = taskService.forceCompleteTask(createdTask.getId());

    assertEquals(TaskState.COMPLETED, completedTask.getState());
    assertNotNull(completedTask.getCompleted());
    assertTrue(isBeforeOrEqual(completedTask.getCreated(), completedTask.getModified()));
    assertEquals(completedTask.getModified(), completedTask.getCompleted());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCompleteTaskThrowsErrors() {
    TaskService taskService = taskanaEngine.getTaskService();

    Assertions.assertThrows(
        TaskNotFoundException.class,
        () -> taskService.completeTask("TKI:0000000000000000000000000000000000xx"));

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> taskService.completeTask("TKI:000000000000000000000000000000000004"));

    Assertions.assertThrows(
        InvalidStateException.class,
        () -> taskService.completeTask("TKI:000000000000000000000000000000000025"));

    Assertions.assertThrows(
        InvalidOwnerException.class,
        () -> taskService.completeTask("TKI:000000000000000000000000000000000027"));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testClaimTaskWithDefaultFlag()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner(null);
    Task createdTask = taskService.createTask(newTask);

    assertNotNull(createdTask);
    assertNull(createdTask.getClaimed());

    final Instant before = createdTask.getCreated();
    Task claimedTask = taskService.claim(createdTask.getId());

    assertNotNull(claimedTask.getOwner());
    assertEquals(claimedTask.getOwner(), CurrentUserContext.getUserid());
    assertNotNull(claimedTask.getClaimed());
    assertTrue(isBeforeOrEqual(before, claimedTask.getClaimed()));
    assertTrue(isBeforeOrEqual(claimedTask.getCreated(), claimedTask.getClaimed()));
    assertTrue(isBeforeOrEqual(claimedTask.getClaimed(), Instant.now()));
    assertEquals(claimedTask.getClaimed(), claimedTask.getModified());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testForceClaimTaskFromOtherUser()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setOwner("other_user");
    Task createdTask = taskService.createTask(newTask);

    assertNotNull(createdTask);
    assertEquals(createdTask.getOwner(), "other_user");

    Instant beforeForceClaim = Instant.now();
    Task taskAfterClaim = taskService.forceClaim(createdTask.getId());

    assertEquals(CurrentUserContext.getUserid(), taskAfterClaim.getOwner());
    assertTrue(isBeforeOrEqual(beforeForceClaim, taskAfterClaim.getModified()));
    assertTrue(isBeforeOrEqual(beforeForceClaim, taskAfterClaim.getClaimed()));
    assertTrue(isBeforeOrEqual(taskAfterClaim.getCreated(), taskAfterClaim.getModified()));

    assertEquals(TaskState.CLAIMED, taskAfterClaim.getState());
    assertTrue(taskAfterClaim.isRead());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testClaimTaskNotExisting() {

    TaskService taskService = taskanaEngine.getTaskService();
    Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.claim("NOT_EXISTING"));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testClaimTaskWithInvalidState() {

    TaskService taskService = taskanaEngine.getTaskService();
    Assertions.assertThrows(
        InvalidStateException.class,
        () -> taskService.forceClaim("TKI:000000000000000000000000000000000036"));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testClaimTaskWithInvalidOwner() {

    TaskService taskService = taskanaEngine.getTaskService();
    Assertions.assertThrows(
        InvalidOwnerException.class,
        () -> taskService.claim("TKI:000000000000000000000000000000000100"));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCancelClaimForcedWithInvalidState() {

    TaskService taskService = taskanaEngine.getTaskService();
    Assertions.assertThrows(
        InvalidStateException.class,
        () -> taskService.forceCancelClaim("TKI:000000000000000000000000000000000036"));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCancelClaimDefaultFlag()
      throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
    newTask.setClassificationKey("T2100");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    Task createdTask = taskService.createTask(newTask);

    assertNotNull(createdTask);
    assertEquals(createdTask.getState(), TaskState.READY);

    createdTask = taskService.cancelClaim(createdTask.getId());

    assertNotNull(createdTask);
    assertEquals(createdTask.getState(), TaskState.READY);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"admin"})
  @Test
  void testForceCancelClaimSuccessfull()
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException, InterruptedException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task taskBefore = taskService.getTask("TKI:000000000000000000000000000000000043");

    assertNotNull(taskBefore);
    assertEquals(TaskState.CLAIMED, taskBefore.getState());

    final Instant before = Instant.now();
    Thread.sleep(1);
    Task taskAfter = taskService.forceCancelClaim("TKI:000000000000000000000000000000000043");

    assertNotNull(taskAfter);
    assertEquals(TaskState.READY, taskAfter.getState());
    assertNull(taskAfter.getClaimed());
    assertTrue(taskAfter.getModified().isAfter(before));
    assertNull(taskAfter.getOwner());
    assertTrue(taskAfter.isRead());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCancelClaimWithInvalidOwner() {

    TaskService taskService = taskanaEngine.getTaskService();
    Assertions.assertThrows(
        InvalidOwnerException.class,
        () -> taskService.cancelClaim("TKI:000000000000000000000000000000000100"));
  }

  private boolean isBeforeOrEqual(Instant before, Instant after) {
    return before.isBefore(after) || before.equals(after);
  }
}
