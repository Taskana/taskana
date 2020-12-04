package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import helper.AbstractAccTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
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

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketIdIsProvided() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000006");

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TransferTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000006");

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    taskService.transfer(task.getId(), "USER-1-1", "DOMAIN_A");

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ChangeDomain_When_TransferringTaskToWorkbasketWithDifferentDomain() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    String domain1 = task.getDomain();

    Task transferredTask = taskService.transfer(task.getId(), "GPK_B_KSC_1", "DOMAIN_B");

    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.getDomain()).isNotEqualTo(domain1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorization() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000001");

    ThrowingCallable call =
        () -> {
          taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_DestinationWorkbasketDoesNotExist() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    ThrowingCallable call =
        () -> {
          taskService.transfer(task.getId(), "INVALID");
        };
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferDoesNotExist() {
    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable call =
        () -> {
          taskService.transfer("Invalid", "WBI:100000000000000000000000000000000006");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TransferWithNoTransferAuthorization() {
    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable call =
        () -> {
          taskService.transfer(
              "TKI:200000000000000000000000000000000007",
              "WBI:100000000000000000000000000000000001");
        };
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. Permission 'TRANSFER' on workbasket "
                + "'WBI:100000000000000000000000000000000005' is needed.");
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferWithNoAppendAuthorization() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000002");

    ThrowingCallable call =
        () -> {
          taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000008");
        };
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. Permission 'APPEND' on workbasket "
                + "'WBI:100000000000000000000000000000000008' is needed.");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferIsAlreadyCompleted() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:100000000000000000000000000000000006");

    ThrowingCallable call =
        () -> {
          taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005");
        };
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketIdIsProvided() throws Exception {
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    TaskService taskService = taskanaEngine.getTaskService();
    ArrayList<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000004");
    taskIdList.add("TKI:000000000000000000000000000000000005");

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIdList);
    assertThat(results.containsErrors()).isFalse();

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER-1-1", "DOMAIN_A");
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000004");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
    transferredTask = taskService.getTask("TKI:000000000000000000000000000000000005");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferOnlyValidTasks_When_SomeTasksToTransferCauseExceptions()
      throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER-1-1", "DOMAIN_A");
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
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
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap().values()).hasSize(5);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000000000041").getClass())
        .isEqualTo(NotAuthorizedException.class);
    assertThat(results.getErrorForId("TKI:200000000000000000000000000000000006").getClass())
        .isEqualTo(InvalidStateException.class);
    assertThat(InvalidArgumentException.class).isEqualTo(results.getErrorForId("").getClass());
    assertThat(results.getErrorForId("TKI:000000000000000000000000000000000099").getClass())
        .isEqualTo(TaskNotFoundException.class);
    assertThat(results.getErrorForId("TKI:100000000000000000000000000000000006").getClass())
        .isEqualTo(InvalidStateException.class);

    // verify valid requests
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000006");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();

    transferredTask = taskService.getTask("TKI:000000000000000000000000000000000002");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("USER-1-1");

    transferredTask = taskService.getTask("TKI:200000000000000000000000000000000006");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TEAMLEAD-2");

    transferredTask = taskService.getTask("TKI:100000000000000000000000000000000006");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TEAMLEAD-1");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_BulkTransferTasksWithoutAppendPermissionOnTarget() {
    TaskService taskService = taskanaEngine.getTaskService();
    ArrayList<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000006"); // working
    taskIdList.add("TKI:000000000000000000000000000000000041"); // NotAuthorized READ

    ThrowingCallable call =
        () -> {
          taskService.transferTasks("WBI:100000000000000000000000000000000010", taskIdList);
        };
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedException.class)
        .hasMessageContaining("APPEND");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTasks_When_TransferringTasksWithListNotSupportingRemove() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> taskIds = List.of("TKI:000000000000000000000000000000000006");

    ThrowingCallable call =
        () -> {
          taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);
        };

    assertThatCode(call).doesNotThrowAnyException();
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferredTaskListIsNull() {
    TaskService taskService = taskanaEngine.getTaskService();
    // test with invalid list

    ThrowingCallable call =
        () -> taskService.transferTasks("WBI:100000000000000000000000000000000006", null);
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("TaskIds must not be null.");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferringTasksWithOnlyInvalidTasksIds() {
    TaskService taskService = taskanaEngine.getTaskService();

    // test with list containing only invalid arguments
    ThrowingCallable call =
        () ->
            taskService.transferTasks(
                "WBI:100000000000000000000000000000000006",
                /* we can't use List.of because of the null value we insert */
                Arrays.asList("", "", "", null));
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("TaskIds must not contain only invalid arguments.");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferringEmptyTaskIdList() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> taskIds = new ArrayList<>();
    ThrowingCallable call =
        () -> {
          taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);
        };
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    TaskService taskService = taskanaEngine.getTaskService();
    List<String> taskIdList = new ArrayList<>();

    taskIdList.add("TKI:000000000000000000000000000000000023");
    taskIdList.add("TKI:000000000000000000000000000000000024");

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("GPK_B_KSC_1", "DOMAIN_B", taskIdList);
    assertThat(results.containsErrors()).isFalse();

    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("GPK_B_KSC_1", "DOMAIN_B");
    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000023");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
    transferredTask = taskService.getTask("TKI:000000000000000000000000000000000024");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isTrue();
    assertThat(transferredTask.isRead()).isFalse();
    assertThat(transferredTask.getState()).isEqualTo(TaskState.READY);
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo(wb.getKey());
    assertThat(transferredTask.getDomain()).isEqualTo(wb.getDomain());
    assertThat(transferredTask.getModified().isBefore(before)).isFalse();
    assertThat(transferredTask.getOwner()).isNull();
  }
}
