package acceptance.task.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;

/** Acceptance test for all "transfer task" scenarios. */
@ExtendWith(JaasExtension.class)
class TransferTaskAccTest extends AbstractAccTest {

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTaskToWorkbasket_When_WorkbasketIdIsProvided() throws Exception {
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
  @TestFactory
  Stream<DynamicTest> should_SetStateCorrectly_When_TransferringSingleTask() {
    List<Pair<String, TaskState>> testCases =
        List.of(
            Pair.of("TKI:100000000000000000000000000000000025", TaskState.READY_FOR_REVIEW),
            Pair.of("TKI:000000000000000000000000000000000025", TaskState.READY),
            Pair.of("TKI:200000000000000000000000000000000025", TaskState.READY_FOR_REVIEW),
            Pair.of("TKI:000000000000000000000000000000000026", TaskState.READY));

    ThrowingConsumer<Pair<String, TaskState>> test =
        p -> {
          String taskId = p.getLeft();
          TaskState expectedState = p.getRight();
          taskService.transfer(taskId, "WBI:100000000000000000000000000000000006");
          Task result = taskService.getTask(taskId);
          assertThat(result.getState()).isEqualTo(expectedState);
          taskService.transfer(taskId, "WBI:100000000000000000000000000000000007");
        };

    return DynamicTest.stream(
        testCases.iterator(), p -> "Expected state: " + p.getRight().name(), test);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SetStateCorrectly_When_BulkTranferringTasks() throws Exception {
    String readyForReview = "TKI:100000000000000000000000000000000025";
    String ready = "TKI:000000000000000000000000000000000025";
    String inReview = "TKI:200000000000000000000000000000000025";
    String claimed = "TKI:000000000000000000000000000000000026";
    List<String> taskIds = List.of(readyForReview, ready, inReview, claimed);

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);

    assertThat(results.containsErrors()).isFalse();
    assertThat(taskService.getTask(readyForReview).getState())
        .isEqualTo(taskService.getTask(inReview).getState())
        .isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(taskService.getTask(ready).getState())
        .isEqualTo(taskService.getTask(claimed).getState())
        .isEqualTo(TaskState.READY);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_TransferTask_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
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
    Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
    String domain1 = task.getDomain();

    Task transferredTask = taskService.transfer(task.getId(), "GPK_B_KSC_1", "DOMAIN_B");

    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.getDomain()).isNotEqualTo(domain1);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorization() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000001");

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005");
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1", groups = "cn=routers,cn=groups,OU=Test,O=TASKANA")
  @Test
  void should_ThrowException_When_UserHasNoTransferAuthorizationAndIsMemeberOfTaskRouterRole()
      throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000001");

    assertThatThrownBy(
            () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005"))
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_DestinationWorkbasketDoesNotExist() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    taskService.claim(task.getId());
    taskService.setTaskRead(task.getId(), true);

    ThrowingCallable call = () -> taskService.transfer(task.getId(), "INVALID");
    assertThatThrownBy(call).isInstanceOf(WorkbasketNotFoundException.class);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferDoesNotExist() {
    ThrowingCallable call =
        () -> taskService.transfer("Invalid", "WBI:100000000000000000000000000000000005");
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TransferWithNoTransferAuthorization() {
    ThrowingCallable call =
        () ->
            taskService.transfer(
                "TKI:200000000000000000000000000000000007",
                "WBI:100000000000000000000000000000000001");
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. The current user 'teamlead-1' has no '[TRANSFER]' permission(s) "
                + "for Workbasket 'WBI:100000000000000000000000000000000005'.");
  }

  @WithAccessId(user = "user-1-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferWithNoAppendAuthorization() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000002");

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000008");
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedException.class)
        .extracting(Throwable::getMessage)
        .asString()
        .startsWith(
            "Not authorized. The current user 'user-1-1' has no '[APPEND]' permission(s) "
                + "for Workbasket 'WBI:100000000000000000000000000000000008'.");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_TaskToTransferIsAlreadyCompleted() throws Exception {
    Task task = taskService.getTask("TKI:100000000000000000000000000000000006");

    ThrowingCallable call =
        () -> taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005");
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketIdIsProvided() throws Exception {
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<String> taskIdList =
        List.of(
            "TKI:000000000000000000000000000000000004", "TKI:000000000000000000000000000000000005");

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
    final Workbasket wb =
        taskanaEngine.getWorkbasketService().getWorkbasket("USER-1-1", "DOMAIN_A");
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    // we can't use List.of because of the null value we insert
    List<String> taskIdList =
        Arrays.asList(
            "TKI:000000000000000000000000000000000006", // working
            "TKI:000000000000000000000000000000000041", // NotAuthorized READ
            "TKI:000000000000000000000000000000000041", // NotAuthorized READ
            "TKI:200000000000000000000000000000000008", // NotAuthorized TRANSFER
            "", // InvalidArgument
            null, // InvalidArgument
            "TKI:000000000000000000000000000000000099", // not existing
            "TKI:100000000000000000000000000000000006"); // already completed

    BulkOperationResults<String, TaskanaException> results =
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIdList);
    // check for exceptions in bulk
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap().values()).hasSize(6);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000000000041").getClass())
        .isEqualTo(MismatchedWorkbasketPermissionException.class);
    assertThat(results.getErrorForId("TKI:200000000000000000000000000000000008").getClass())
        .isEqualTo(MismatchedWorkbasketPermissionException.class);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000000000099").getClass())
        .isEqualTo(TaskNotFoundException.class);
    assertThat(results.getErrorForId("TKI:100000000000000000000000000000000006").getClass())
        .isEqualTo(InvalidTaskStateException.class);
    assertThat(results.getErrorForId("").getClass()).isEqualTo(InvalidArgumentException.class);
    assertThat(results.getErrorForId(null).getClass()).isEqualTo(InvalidArgumentException.class);

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

    transferredTask = taskService.getTask("TKI:200000000000000000000000000000000008");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TPK_VIP");

    transferredTask = taskService.getTask("TKI:100000000000000000000000000000000006");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
    assertThat(transferredTask.getWorkbasketKey()).isEqualTo("TEAMLEAD-1");
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ThrowException_When_BulkTransferTasksWithoutAppendPermissionOnTarget() {

    List<String> taskIdList =
        List.of(
            "TKI:000000000000000000000000000000000006", // working
            "TKI:000000000000000000000000000000000041"); // NotAuthorized READ

    ThrowingCallable call =
        () -> taskService.transferTasks("WBI:100000000000000000000000000000000010", taskIdList);
    assertThatThrownBy(call)
        .isInstanceOf(NotAuthorizedException.class)
        .hasMessageContaining("APPEND");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_TransferTasks_When_TransferringTasksWithListNotSupportingRemove() {
    List<String> taskIds = List.of("TKI:000000000000000000000000000000000006");

    ThrowingCallable call =
        () -> taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferredTaskListIsNull() {
    ThrowingCallable call =
        () -> taskService.transferTasks("WBI:100000000000000000000000000000000006", null);
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("TaskIds must not be null or empty.");
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_ThrowException_When_TransferringEmptyTaskIdList() {
    ThrowingCallable call =
        () ->
            taskService.transferTasks(
                "WBI:100000000000000000000000000000000006", Collections.emptyList());
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "teamlead-1", groups = GROUP_1_DN)
  @Test
  void should_BulkTransferTasks_When_WorkbasketKeyAndDomainIsProvided() throws Exception {
    final Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<String> taskIdList =
        List.of(
            "TKI:000000000000000000000000000000000023", "TKI:000000000000000000000000000000000024");

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

  @WithAccessId(user = "admin")
  @Test
  void should_NotSetTheTransferFlag_When_SetTransferFlagNotRequested() throws Exception {
    taskService.transfer(
        "TKI:000000000000000000000000000000000003",
        "WBI:100000000000000000000000000000000006",
        false);

    Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
    assertThat(transferredTask).isNotNull();
    assertThat(transferredTask.isTransferred()).isFalse();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotSetTheTransferFlagWithinBulkTransfer_When_SetTransferFlagNotRequested()
      throws Exception {
    taskService.transferTasks(
        "WBI:100000000000000000000000000000000006",
        List.of(
            "TKI:000000000000000000000000000000000003",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000005"),
        false);

    List<TaskSummary> transferredTasks =
        taskService
            .createTaskQuery()
            .idIn(
                "TKI:000000000000000000000000000000000004",
                "TKI:000000000000000000000000000000000005",
                "TKI:000000000000000000000000000000000003")
            .list();

    assertThat(transferredTasks).extracting(TaskSummary::isTransferred).containsOnly(false);
  }
}
