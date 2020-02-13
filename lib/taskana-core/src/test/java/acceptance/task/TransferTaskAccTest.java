package acceptance.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;

/** Acceptance test for all "transfer task" scenarios. */
@ExtendWith(JaasExtension.class)
class TransferTaskAccTest extends AbstractAccTest {

  TransferTaskAccTest() {
    super();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testTransferTaskToWorkbasketId()
      throws NotAuthorizedException, WorkbasketNotFoundException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000006");

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertNotNull(transferredTask);
    assertTrue(transferredTask.isTransferred());
    assertFalse(transferredTask.isRead());
    assertEquals(TaskState.READY, transferredTask.getState());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testTransferTaskToWorkbasketKeyDomain()
      throws NotAuthorizedException, WorkbasketNotFoundException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "USER_1_1", "DOMAIN_A");

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertNotNull(transferredTask);
    assertTrue(transferredTask.isTransferred());
    assertFalse(transferredTask.isRead());
    assertEquals(TaskState.READY, transferredTask.getState());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testDomainChangingWhenTransferTask()
      throws NotAuthorizedException, WorkbasketNotFoundException, TaskNotFoundException,
          InvalidStateException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    String domain1 = task.getDomain();

    Task transferedTask = taskService.transfer(task.getId(), "GPK_B_KSC_1", "DOMAIN_B");

    assertNotNull(transferedTask);
    assertNotEquals(domain1, transferedTask.getDomain());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfTransferWithNoTransferAuthorization()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000001");

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005"));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testTransferDestinationWorkbasketDoesNotExist()
      throws NotAuthorizedException, TaskNotFoundException, InvalidStateException,
          InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    Assertions.assertThrows(
        WorkbasketNotFoundException.class, () -> taskService.transfer(task.getId(), "INVALID"));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testTransferTaskDoesNotExist() {
    TaskService taskService = taskanaEngine.getTaskService();

    Assertions.assertThrows(
        TaskNotFoundException.class,
        () -> taskService.transfer("Invalid", "WBI:100000000000000000000000000000000006"));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"teamlead_1"})
  @Test
  void testTransferNotAuthorizationOnWorkbasketTransfer() {
    TaskService taskService = taskanaEngine.getTaskService();

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            taskService.transfer(
                "TKI:200000000000000000000000000000000007",
                "WBI:100000000000000000000000000000000006"));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfTaskIsAlreadyCompleted()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:100000000000000000000000000000000006");

    Assertions.assertThrows(
        InvalidStateException.class,
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005"));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfTransferWithNoAppendAuthorization()
      throws NotAuthorizedException, TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000002");

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000008"));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testBulkTransferTaskToWorkbasketById()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          TaskNotFoundException {
    final Instant before = Instant.now();
    TaskService taskService = taskanaEngine.getTaskService();
    ArrayList<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000004");
    taskIdList.add("TKI:000000000000000000000000000000000005");

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIdList);
    assertFalse(results.containsErrors());

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER_1_1", "DOMAIN_A");
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000004");
    assertNotNull(transferredTask);
    assertTrue(transferredTask.isTransferred());
    assertFalse(transferredTask.isRead());
    assertEquals(TaskState.READY, transferredTask.getState());
    assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
    assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
    assertFalse(transferredTask.getModified().isBefore(before));
    assertThat(transferredTask.getOwner(), equalTo(null));
    transferredTask = taskService.getTask("TKI:000000000000000000000000000000000005");
    assertNotNull(transferredTask);
    assertTrue(transferredTask.isTransferred());
    assertFalse(transferredTask.isRead());
    assertEquals(TaskState.READY, transferredTask.getState());
    assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
    assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
    assertFalse(transferredTask.getModified().isBefore(before));
    assertThat(transferredTask.getOwner(), equalTo(null));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testBulkTransferTaskWithExceptions()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException,
          TaskNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER_1_1", "DOMAIN_A");
    final Instant before = Instant.now();
    ArrayList<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000006"); // working
    taskIdList.add("TKI:000000000000000000000000000000000041"); // NotAuthorized READ
    taskIdList.add("TKI:200000000000000000000000000000000006"); // NotAuthorized TRANSFER
    taskIdList.add(""); // InvalidArgument
    taskIdList.add(null); // InvalidArgument (added with ""), duplicate
    taskIdList.add("TKI:000000000000000000000000000000000099"); // TaskNotFound
    taskIdList.add("TKI:100000000000000000000000000000000006"); // already completed

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIdList);
    // check for exceptions in bulk
    assertTrue(results.containsErrors());
    assertThat(results.getErrorMap().values().size(), equalTo(5));
    assertEquals(
        results.getErrorForId("TKI:000000000000000000000000000000000041").getClass(),
        NotAuthorizedException.class);
    assertEquals(
        results.getErrorForId("TKI:200000000000000000000000000000000006").getClass(),
        InvalidStateException.class);
    assertEquals(results.getErrorForId("").getClass(), InvalidArgumentException.class);
    assertEquals(
        results.getErrorForId("TKI:000000000000000000000000000000000099").getClass(),
        TaskNotFoundException.class);
    assertEquals(
        results.getErrorForId("TKI:100000000000000000000000000000000006").getClass(),
        InvalidStateException.class);

    // verify valid requests
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000006");
    assertNotNull(transferredTask);
    assertTrue(transferredTask.isTransferred());
    assertFalse(transferredTask.isRead());
    assertEquals(TaskState.READY, transferredTask.getState());
    assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
    assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
    assertFalse(transferredTask.getModified().isBefore(before));
    assertThat(transferredTask.getOwner(), equalTo(null));

    transferredTask = taskService.getTask("TKI:000000000000000000000000000000000002");
    assertNotNull(transferredTask);
    assertFalse(transferredTask.isTransferred());
    assertEquals("USER_1_1", transferredTask.getWorkbasketKey());

    transferredTask = taskService.getTask("TKI:200000000000000000000000000000000006");
    assertNotNull(transferredTask);
    assertFalse(transferredTask.isTransferred());
    assertEquals("TEAMLEAD_2", transferredTask.getWorkbasketKey());

    transferredTask = taskService.getTask("TKI:100000000000000000000000000000000006");
    assertNotNull(transferredTask);
    assertFalse(transferredTask.isTransferred());
    assertEquals("TEAMLEAD_1", transferredTask.getWorkbasketKey());
  }

  @WithAccessId(userName = "teamlead_1")
  @Test
  void testBulkTransferTaskWithoutAppendPermissionOnTarget() {
    TaskService taskService = taskanaEngine.getTaskService();
    ArrayList<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000006"); // working
    taskIdList.add("TKI:000000000000000000000000000000000041"); // NotAuthorized READ

    Throwable t =
        Assertions.assertThrows(
            NotAuthorizedException.class,
            () ->
                taskService.transferTasks("WBI:100000000000000000000000000000000010", taskIdList));

    assertTrue(t.getMessage().contains("APPEND"));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testTransferTasksWithListNotSupportingRemove()
      throws NotAuthorizedException, InvalidArgumentException, WorkbasketNotFoundException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> taskIds = Collections.singletonList("TKI:000000000000000000000000000000000006");
    taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testTransferTasksWithInvalidTasksIdList() {
    TaskService taskService = taskanaEngine.getTaskService();
    // test with invalid list

    Throwable t =
        Assertions.assertThrows(
            InvalidArgumentException.class,
            () -> taskService.transferTasks("WBI:100000000000000000000000000000000006", null));
    assertEquals(t.getMessage(), "TaskIds must not be null.");

    // test with list containing only invalid arguments
    Throwable t2 =
        Assertions.assertThrows(
            InvalidArgumentException.class,
            () ->
                taskService.transferTasks(
                    "WBI:100000000000000000000000000000000006", Arrays.asList("", "", "", null)));
    assertEquals(t2.getMessage(), "TaskIds must not contain only invalid arguments.");
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfEmptyListIsSupplied() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> taskIds = new ArrayList<>();
    Assertions.assertThrows(
        InvalidArgumentException.class,
        () -> taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testBulkTransferByWorkbasketAndDomainByKey()
      throws WorkbasketNotFoundException, NotAuthorizedException, InvalidArgumentException,
          TaskNotFoundException {
    final Instant before = Instant.now();
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> taskIdList = new ArrayList<>();

    taskIdList.add("TKI:000000000000000000000000000000000023");
    taskIdList.add("TKI:000000000000000000000000000000000024");

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("GPK_B_KSC_1", "DOMAIN_B", taskIdList);
    assertFalse(results.containsErrors());

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("GPK_B_KSC_1", "DOMAIN_B");
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000023");
    assertNotNull(transferredTask);
    assertTrue(transferredTask.isTransferred());
    assertFalse(transferredTask.isRead());
    assertEquals(TaskState.READY, transferredTask.getState());
    assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
    assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
    assertFalse(transferredTask.getModified().isBefore(before));
    assertThat(transferredTask.getOwner(), equalTo(null));
    transferredTask = taskService.getTask("TKI:000000000000000000000000000000000024");
    assertNotNull(transferredTask);
    assertTrue(transferredTask.isTransferred());
    assertFalse(transferredTask.isRead());
    assertEquals(TaskState.READY, transferredTask.getState());
    assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
    assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
    assertFalse(transferredTask.getModified().isBefore(before));
    assertThat(transferredTask.getOwner(), equalTo(null));
  }
}
