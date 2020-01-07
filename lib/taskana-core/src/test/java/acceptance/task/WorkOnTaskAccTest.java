package acceptance.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.BulkOperationResults;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "work on task" scenarios. This includes claim, complete... */
@ExtendWith(JAASExtension.class)
class WorkOnTaskAccTest extends AbstractAccTest {

  WorkOnTaskAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testClaimTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000025");

    taskService.claim(task.getId());

    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000025");
    assertNotNull(claimedTask);
    assertEquals(TaskState.CLAIMED, claimedTask.getState());
    assertNotNull(claimedTask.getClaimed());
    assertNotEquals(claimedTask.getCreated(), claimedTask.getModified());
    assertEquals(claimedTask.getClaimed(), claimedTask.getModified());
    assertTrue(claimedTask.isRead());
    assertEquals("user_1_2", claimedTask.getOwner());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfTaskIsAlreadyClaimed()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000026");

    Assertions.assertThrows(InvalidOwnerException.class, () -> taskService.claim(task.getId()));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testClaimAlreadyClaimedByCallerTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

    taskService.claim(task.getId());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testForceClaimTaskWhichIsAlreadyClaimedByAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000028");

    Assertions.assertThrows(InvalidOwnerException.class, () -> taskService.claim(task.getId()));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testCancelClaimTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000029");

    taskService.cancelClaim(claimedTask.getId());

    Task unclaimedTask = taskService.getTask("TKI:000000000000000000000000000000000029");
    assertNotNull(unclaimedTask);
    assertEquals(TaskState.READY, unclaimedTask.getState());
    assertNull(unclaimedTask.getClaimed());
    assertTrue(unclaimedTask.isRead());
    assertNull(unclaimedTask.getOwner());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfCancelClaimOfTaskFromAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000030");

    Assertions.assertThrows(
        InvalidOwnerException.class, () -> taskService.cancelClaim(claimedTask.getId()));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testForceCancelClaimOfTaskFromAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000031");

    taskService.forceCancelClaim(claimedTask.getId());

    Task unclaimedTask = taskService.getTask("TKI:000000000000000000000000000000000031");
    assertNotNull(unclaimedTask);
    assertEquals(TaskState.READY, unclaimedTask.getState());
    assertNull(unclaimedTask.getClaimed());
    assertTrue(unclaimedTask.isRead());
    assertNull(unclaimedTask.getOwner());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testCompleteTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    final Instant before = Instant.now().minus(Duration.ofSeconds(3L));
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000032");

    taskService.completeTask(claimedTask.getId());

    Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000032");
    assertNotNull(completedTask);
    assertEquals(TaskState.COMPLETED, completedTask.getState());
    assertNotNull(completedTask.getCompleted());
    assertEquals(completedTask.getCompleted(), completedTask.getModified());
    assertTrue(completedTask.getCompleted().isAfter(before));
    assertTrue(completedTask.getModified().isAfter(before));
    assertTrue(completedTask.isRead());
    assertEquals("user_1_2", completedTask.getOwner());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testForceCompleteUnclaimedTask()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000033");

    taskService.forceCompleteTask(claimedTask.getId());

    Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000033");
    assertNotNull(completedTask);
    assertEquals(TaskState.COMPLETED, completedTask.getState());
    assertNotNull(completedTask.getCompleted());
    assertEquals(completedTask.getCompleted(), completedTask.getModified());
    assertTrue(completedTask.isRead());
    assertEquals("user_1_2", completedTask.getOwner());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfCompletingClaimedTaskOfAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000034");

    Assertions.assertThrows(
        InvalidOwnerException.class, () -> taskService.completeTask(claimedTask.getId()));
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testForceCompleteClaimedTaskOfAnotherUser()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000035");

    taskService.forceCompleteTask(claimedTask.getId());

    Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000035");
    assertNotNull(completedTask);
    assertEquals(TaskState.COMPLETED, completedTask.getState());
    assertNotNull(completedTask.getCompleted());
    assertEquals(completedTask.getCompleted(), completedTask.getModified());
    assertTrue(completedTask.isRead());
    assertEquals("user_1_2", completedTask.getOwner());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testBulkCompleteTasks()
      throws NotAuthorizedException, InvalidArgumentException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();
    List<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000100");
    taskIdList.add("TKI:000000000000000000000000000000000101");

    BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

    assertFalse(results.containsErrors());
    Task completedTask1 = taskService.getTask("TKI:000000000000000000000000000000000100");
    assertEquals(TaskState.COMPLETED, completedTask1.getState());
    assertNotNull(completedTask1.getCompleted());
    Task completedTask2 = taskService.getTask("TKI:000000000000000000000000000000000101");
    assertEquals(TaskState.COMPLETED, completedTask2.getState());
    assertNotNull(completedTask2.getCompleted());
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testBulkDeleteTasksWithException() throws InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();
    List<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000102");
    taskIdList.add("TKI:000000000000000000000000000000003333");

    BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

    assertTrue(results.containsErrors());
    assertThat(results.getErrorMap().size(), equalTo(2));
    assertTrue(
        results.getErrorForId("TKI:000000000000000000000000000000003333")
            instanceof TaskNotFoundException);
    assertTrue(
        results.getErrorForId("TKI:000000000000000000000000000000000102")
            instanceof InvalidStateException);
  }
}
