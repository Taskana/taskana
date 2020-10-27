package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
class CallbackStateAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @Test
  void testCreateTaskWithDifferentCallbackStates() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();
    TaskImpl createdTask = createTask(taskService, CallbackState.NONE);
    createdTask = (TaskImpl) taskService.getTask(createdTask.getId());
    assertThat(createdTask.getCallbackState()).isEqualTo(CallbackState.NONE);

    createdTask = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    createdTask = (TaskImpl) taskService.getTask(createdTask.getId());
    assertThat(createdTask.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    createdTask = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    createdTask = (TaskImpl) taskService.getTask(createdTask.getId());
    assertThat(createdTask.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_COMPLETED);

    createdTask = createTask(taskService, CallbackState.CLAIMED);
    createdTask = (TaskImpl) taskService.getTask(createdTask.getId());
    assertThat(createdTask.getCallbackState()).isEqualTo(CallbackState.CLAIMED);
  }

  @WithAccessId(user = "admin")
  @Test
  void testDeletionOfTaskWithWrongCallbackStateIsBlocked() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();

    final TaskImpl createdTask =
        createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(createdTask.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    assertThat(createdTask.getState()).isEqualTo(TaskState.READY);
    String endOfMessage = " cannot be deleted because its callback is not yet processed";

    ThrowingCallable call =
        () -> {
          taskService.forceDeleteTask(createdTask.getId());
        };
    assertThatThrownBy(call)
        .isInstanceOf(InvalidStateException.class)
        .hasMessageEndingWith(endOfMessage);

    final TaskImpl createdTask2 = (TaskImpl) taskService.claim(createdTask.getId());

    assertThat(createdTask2.getState()).isEqualTo(TaskState.CLAIMED);

    call =
        () -> {
          taskService.forceDeleteTask(createdTask2.getId());
        };
    assertThatThrownBy(call)
        .isInstanceOf(InvalidStateException.class)
        .hasMessageEndingWith(endOfMessage);

    final TaskImpl createdTask3 = (TaskImpl) taskService.completeTask(createdTask.getId());

    call =
        () -> {
          taskService.forceDeleteTask(createdTask3.getId());
        };
    assertThatThrownBy(call)
        .isInstanceOf(InvalidStateException.class)
        .hasMessageEndingWith(endOfMessage);
  }

  @WithAccessId(user = "admin")
  @Test
  void testUpdateOfCallbackState() throws Exception {

    TaskImpl createdTask1 =
        createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(createdTask1.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    TaskImpl createdTask2 =
        createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(createdTask2.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    TaskImpl createdTask3 =
        createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(createdTask3.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    TaskService taskService = taskanaEngine.getTaskService();
    createdTask1 = (TaskImpl) taskService.forceCompleteTask(createdTask1.getId());
    createdTask2 = (TaskImpl) taskService.forceCompleteTask(createdTask2.getId());
    createdTask3 = (TaskImpl) taskService.forceCompleteTask(createdTask3.getId());

    assertThat(createdTask1.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(createdTask2.getState()).isEqualTo(TaskState.COMPLETED);
    assertThat(createdTask3.getState()).isEqualTo(TaskState.COMPLETED);

    List<String> taskIds =
        new ArrayList<>(
            Arrays.asList(createdTask1.getId(), createdTask2.getId(), createdTask3.getId()));
    // delete should fail because callback_state = CALLBACK_PROCESSING_REQUIRED
    BulkOperationResults<String, TaskanaException> bulkResult1 = taskService.deleteTasks(taskIds);

    assertThat(bulkResult1.containsErrors()).isTrue();
    List<String> failedTaskIds = bulkResult1.getFailedIds();

    assertThat(failedTaskIds).hasSize(3);
    for (String taskId : failedTaskIds) {
      TaskanaException excpt = bulkResult1.getErrorForId(taskId);
      assertThat(excpt.getClass().getName()).isEqualTo(InvalidStateException.class.getName());
    }
    List<String> externalIds =
        new ArrayList<>(
            Arrays.asList(
                createdTask1.getExternalId(),
                createdTask2.getExternalId(),
                createdTask3.getExternalId()));

    // now enable deletion by setting callback state to CALLBACK_PROCESSING_COMPLETED
    BulkOperationResults<String, TaskanaException> bulkResult2 =
        taskService.setCallbackStateForTasks(
            externalIds, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertThat(bulkResult2.containsErrors()).isFalse();

    taskIds =
        new ArrayList<>(
            Arrays.asList(createdTask1.getId(), createdTask2.getId(), createdTask3.getId()));
    // now it should be possible to delete the tasks
    BulkOperationResults<String, TaskanaException> bulkResult3 = taskService.deleteTasks(taskIds);
    assertThat(bulkResult3.containsErrors()).isFalse();
  }

  @WithAccessId(user = "admin")
  @Test
  void testInvalidUpdateOfCallbackStateToNone() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskImpl createdTask1 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(createdTask1.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    TaskImpl createdTask2 = createTask(taskService, CallbackState.CLAIMED);
    assertThat(createdTask2.getCallbackState()).isEqualTo(CallbackState.CLAIMED);

    TaskImpl createdTask3 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertThat(createdTask3.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_COMPLETED);

    List<String> externalIds =
        new ArrayList<>(
            Arrays.asList(
                createdTask1.getExternalId(),
                createdTask2.getExternalId(),
                createdTask3.getExternalId()));

    // try to set CallbackState to NONE
    BulkOperationResults<String, TaskanaException> bulkResult =
        taskService.setCallbackStateForTasks(externalIds, CallbackState.NONE);

    // It's never allowed to set CallbackState to NONE over public API
    assertThat(bulkResult.containsErrors()).isTrue();
    List<String> failedTaskIds = bulkResult.getFailedIds();
    assertThat(failedTaskIds).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void testInvalidUpdateOfCallbackStateToComplete() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskImpl createdTask1 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(createdTask1.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    TaskImpl createdTask2 = createTask(taskService, CallbackState.CLAIMED);
    assertThat(createdTask2.getCallbackState()).isEqualTo(CallbackState.CLAIMED);

    TaskImpl createdTask3 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertThat(createdTask3.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_COMPLETED);

    List<String> externalIds =
        new ArrayList<>(
            Arrays.asList(
                createdTask1.getExternalId(),
                createdTask2.getExternalId(),
                createdTask3.getExternalId()));

    // complete a task
    createdTask3 = (TaskImpl) taskService.forceCompleteTask(createdTask3.getId());

    // It's only allowed to set CallbackState to COMPLETE, if TaskState equals COMPLETE, therefore 2
    // tasks should not get updated
    BulkOperationResults<String, TaskanaException> bulkResult =
        taskService.setCallbackStateForTasks(
            externalIds, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertThat(bulkResult.containsErrors()).isTrue();
    List<String> failedTaskIds = bulkResult.getFailedIds();
    assertThat(failedTaskIds).hasSize(2).doesNotContain(createdTask3.getExternalId());
  }

  @WithAccessId(user = "admin")
  @Test
  void testInvalidUpdateOfCallbackStateToClaimed() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskImpl createdTask1 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(createdTask1.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    TaskImpl createdTask2 = createTask(taskService, CallbackState.CLAIMED);
    assertThat(createdTask2.getCallbackState()).isEqualTo(CallbackState.CLAIMED);

    TaskImpl createdTask3 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertThat(createdTask3.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_COMPLETED);

    List<String> externalIds =
        new ArrayList<>(
            Arrays.asList(
                createdTask1.getExternalId(),
                createdTask2.getExternalId(),
                createdTask3.getExternalId()));

    // claim two tasks
    createdTask1 = (TaskImpl) taskService.forceClaim(createdTask1.getId());
    taskService.forceClaim(createdTask2.getId());

    // It's only allowed to claim a task if the TaskState equals CLAIMED and the CallbackState
    // equals REQUIRED
    // Therefore 2 tasks should not get updated
    BulkOperationResults<String, TaskanaException> bulkResult =
        taskService.setCallbackStateForTasks(externalIds, CallbackState.CLAIMED);
    assertThat(bulkResult.containsErrors()).isTrue();
    List<String> failedTaskIds = bulkResult.getFailedIds();
    assertThat(failedTaskIds)
        .hasSize(2)
        .doesNotContain(createdTask1.getExternalId())
        .containsExactlyInAnyOrder(createdTask2.getExternalId(), createdTask3.getExternalId());
  }

  @WithAccessId(user = "admin")
  @Test
  void testInvalidUpdateOfCallbackStateToRequired() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskImpl createdTask1 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(createdTask1.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    TaskImpl createdTask2 = createTask(taskService, CallbackState.CLAIMED);
    assertThat(createdTask2.getCallbackState()).isEqualTo(CallbackState.CLAIMED);

    TaskImpl createdTask3 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertThat(createdTask3.getCallbackState())
        .isEqualTo(CallbackState.CALLBACK_PROCESSING_COMPLETED);

    List<String> externalIds =
        new ArrayList<>(
            Arrays.asList(
                createdTask1.getExternalId(),
                createdTask2.getExternalId(),
                createdTask3.getExternalId()));

    // It's only allowed to set the CallbackState to REQUIRED if the TaskState doesn't equal
    // COMPLETE
    // Therefore 1 task should not get updated
    BulkOperationResults<String, TaskanaException> bulkResult =
        taskService.setCallbackStateForTasks(
            externalIds, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(bulkResult.containsErrors()).isTrue();
    List<String> failedTaskIds = bulkResult.getFailedIds();
    assertThat(failedTaskIds).containsExactlyInAnyOrder(createdTask3.getExternalId());
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueriesWithCallbackState() throws Exception {
    resetDb(false);
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> claimedTasks =
        taskService.createTaskQuery().stateIn(TaskState.CLAIMED).list();
    assertThat(claimedTasks).hasSizeGreaterThan(10);
    taskService.forceCompleteTask(claimedTasks.get(0).getId());
    taskService.forceCompleteTask(claimedTasks.get(1).getId());
    taskService.forceCompleteTask(claimedTasks.get(2).getId());

    // now we should have several completed tasks with callback state NONE.
    // let's set it to CALLBACK_PROCESSING_REQUIRED
    List<TaskSummary> completedTasks =
        taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
    List<String> externalIds =
        completedTasks.stream().map(TaskSummary::getExternalId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> bulkResultCompleted =
        taskService.setCallbackStateForTasks(
            externalIds, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertThat(bulkResultCompleted.containsErrors()).isFalse();

    // now complete some additional tasks
    taskService.forceCompleteTask(claimedTasks.get(3).getId());
    taskService.forceCompleteTask(claimedTasks.get(4).getId());
    taskService.forceCompleteTask(claimedTasks.get(5).getId());

    int numberOfCompletedTasksAtStartOfTest = completedTasks.size();
    // now lets retrieve those completed tasks that have callback_processing_required
    List<TaskSummary> tasksToBeActedUpon =
        taskService
            .createTaskQuery()
            .stateIn(TaskState.COMPLETED)
            .callbackStateIn(CallbackState.CALLBACK_PROCESSING_REQUIRED)
            .list();
    assertThat(tasksToBeActedUpon).hasSize(numberOfCompletedTasksAtStartOfTest);
    // now we set callback state to callback_processing_completed
    externalIds =
        tasksToBeActedUpon.stream().map(TaskSummary::getExternalId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> bulkResult =
        taskService.setCallbackStateForTasks(
            externalIds, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertThat(bulkResult.containsErrors()).isFalse();

    long numOfTasksRemaining =
        taskService
            .createTaskQuery()
            .stateIn(TaskState.COMPLETED)
            .callbackStateIn(CallbackState.CALLBACK_PROCESSING_REQUIRED)
            .count();
    assertThat(numOfTasksRemaining).isEqualTo(0);
  }

  private TaskImpl createTask(TaskService taskService, CallbackState callbackState)
      throws Exception {
    Task newTask = taskService.newTask("USER-1-1", "DOMAIN_A");
    newTask.setClassificationKey("L12010");
    newTask.setPrimaryObjRef(
        createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
    newTask.setClassificationKey("L12010");
    HashMap<String, String> callbackInfo = new HashMap<>();
    callbackInfo.put(Task.CALLBACK_STATE, callbackState.name());
    newTask.setCallbackInfo(callbackInfo);
    augmentCallbackInfo(newTask);

    return (TaskImpl) taskService.createTask(newTask);
  }

  private void augmentCallbackInfo(Task task) {
    Map<String, String> callbackInfo = task.getCallbackInfo();
    for (int i = 1; i <= 10; i++) {
      callbackInfo.put("info_" + i, "Value of info_" + i);
    }
    task.setCallbackInfo(callbackInfo);
  }
}
