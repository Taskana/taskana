package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.TaskanaEngineProxy;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for all "set owner" scenarios. */
@ExtendWith(JaasExtension.class)
class SetOwnerAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-2")
  @Test
  void testSetOwnerAndSubsequentClaimSucceeds() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();
    String taskReadyId = "TKI:000000000000000000000000000000000025";
    Task taskReady = taskService.getTask(taskReadyId);
    assertThat(taskReady.getState()).isEqualTo(TaskState.READY);
    assertThat(taskReady.getOwner()).isNull();
    String anyUserName = "TestUser27";
    Task modifiedTaskReady = setOwner(taskReadyId, anyUserName);

    assertThat(modifiedTaskReady.getState()).isEqualTo(TaskState.READY);
    assertThat(modifiedTaskReady.getOwner()).isEqualTo(anyUserName);
    Task taskClaimed = taskService.claim(taskReadyId);
    assertThat(taskClaimed.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(taskClaimed.getOwner()).isEqualTo("user-1-2");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testSetOwnerViaUpdateTaskNotAuthorized() {

    TaskService taskService = taskanaEngine.getTaskService();
    String taskReadyId = "TKI:000000000000000000000000000000000024";
    String anyUserName = "TestUser3";

    assertThatThrownBy(() -> taskService.getTask(taskReadyId))
        .isInstanceOf(NotAuthorizedException.class);
    assertThatThrownBy(() -> setOwner(taskReadyId, anyUserName))
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testSetOwnerOfClaimedTaskFails() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();
    String taskClaimedId = "TKI:000000000000000000000000000000000026";
    String anyUserName = "TestUser007";
    Task taskClaimed = taskService.getTask(taskClaimedId);
    assertThat(taskClaimed.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(taskClaimed.getOwner()).isEqualTo("user-1-1");
    assertThatThrownBy(() -> setOwner(taskClaimedId, anyUserName))
        .isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testSetOwnerNotAuthorized() {

    TaskService taskService = taskanaEngine.getTaskService();
    String taskReadyId = "TKI:000000000000000000000000000000000024";
    String anyUserName = "TestUser3";

    assertThatThrownBy(() -> taskService.getTask(taskReadyId))
        .isInstanceOf(NotAuthorizedException.class);
    BulkOperationResults<String, TaskanaException> results =
        taskService.setOwnerOfTasks(anyUserName, List.of(taskReadyId));
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorForId(taskReadyId)).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-b-2")
  @Test
  void testSetOwnerOfTasksWithDuplicatesSucceeds() {

    List<String> taskIds =
        List.of(
            "TKI:000000000000000000000000000000000058",
            "TKI:000000000000000000000000000000000059",
            "TKI:000000000000000000000000000000000058",
            "TKI:000000000000000000000000000000000060");
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setOwnerOfTasks("someUser", taskIds);
    assertThat(results.containsErrors()).isFalse();
  }

  @WithAccessId(user = "user-b-2")
  @Test
  void testSetOwnerOfTasksWithDuplicatesAndNotExistingSucceeds() {
    List<String> taskIds =
        List.of(
            "TKI:000000000000000000000000000000000058",
            "TKI:000000000000000000000000000047110059",
            "TKI:000000000000000000000000000000000059",
            "TKI:000000000000000000000000000000000058",
            "TKI:000000000000000000000000000000000060");
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setOwnerOfTasks("someUser", taskIds);
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(1);
    assertThat(results.getErrorForId("TKI:000000000000000000000000000047110059"))
        .isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "user-b-2")
  @Test
  void testSetOwnerOfTasksWithNoQualifyingTasks() {

    List<String> taskIds =
        List.of(
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000010");
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setOwnerOfTasks("someUser", taskIds);
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(3);
  }

  @WithAccessId(user = "user-b-2")
  @Test
  void testSetOwnerWithEmptyList() {
    List<String> taskIds = new ArrayList<>();
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setOwnerOfTasks("someUser", taskIds);
    assertThat(results.containsErrors()).isFalse();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void testSetOwnerWithAllTasksAndVariousExceptions() throws Exception {
    resetDb(false);
    List<TaskSummary> allTaskSummaries =
        new TaskanaEngineProxy(taskanaEngine)
            .getEngine()
            .runAsAdmin(() -> taskanaEngine.getTaskService().createTaskQuery().list());
    List<String> allTaskIds =
        allTaskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setOwnerOfTasks("theWorkaholic", allTaskIds);
    assertThat(allTaskSummaries).hasSize(87);
    assertThat(results.containsErrors()).isTrue();

    Condition<Object> invalidStateException =
        new Condition<>(c -> c.getClass() == InvalidStateException.class, "InvalidStateException");
    Condition<Object> notAuthorizedException =
        new Condition<>(
            c -> c.getClass() == NotAuthorizedException.class, "NotAuthorizedException");
    assertThat(results.getErrorMap())
        .hasSize(85)
        .extractingFromEntries(Entry::getValue)
        .hasOnlyElementsOfTypes(InvalidStateException.class, NotAuthorizedException.class)
        .areExactly(28, invalidStateException)
        .areExactly(57, notAuthorizedException);
  }

  @WithAccessId(user = "admin")
  @Test
  void testSetOwnerWithAllTasksAndVariousExceptionsAsAdmin() throws Exception {
    resetDb(false);
    List<TaskSummary> allTaskSummaries = taskanaEngine.getTaskService().createTaskQuery().list();
    List<String> allTaskIds =
        allTaskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getTaskService().setOwnerOfTasks("theWorkaholic", allTaskIds);
    assertThat(allTaskSummaries).hasSize(87);
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap())
        .hasSize(40)
        .extractingFromEntries(Entry::getValue)
        .hasOnlyElementsOfType(InvalidStateException.class);
  }

  private Task setOwner(String taskReadyId, String anyUserName) throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask(taskReadyId);
    task.setOwner(anyUserName);
    task = taskService.updateTask(task);
    return task;
  }
}
