package acceptance.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.InvalidOwnerException;
import pro.taskana.common.api.exceptions.InvalidStateException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.Task;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.TaskSummary;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.internal.TaskImpl;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Acceptance test for all "create task" scenarios. */
@ExtendWith(JaasExtension.class)
class CallbackStateAccTest extends AbstractAccTest {

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCreateTaskWithDifferentCallbackStates()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();
    TaskImpl createdTask = createTask(taskService, CallbackState.NONE);
    createdTask = (TaskImpl) taskService.getTask(createdTask.getId());
    assertEquals(CallbackState.NONE, createdTask.getCallbackState());

    createdTask = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    createdTask = (TaskImpl) taskService.getTask(createdTask.getId());
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask.getCallbackState());

    createdTask = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    createdTask = (TaskImpl) taskService.getTask(createdTask.getId());
    assertEquals(CallbackState.CALLBACK_PROCESSING_COMPLETED, createdTask.getCallbackState());

    createdTask = createTask(taskService, CallbackState.CLAIMED);
    createdTask = (TaskImpl) taskService.getTask(createdTask.getId());
    assertEquals(CallbackState.CLAIMED, createdTask.getCallbackState());
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testDeletionOfTaskWithWrongCallbackStateIsBlocked()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {
    TaskService taskService = taskanaEngine.getTaskService();

    final TaskImpl createdTask =
        createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask.getCallbackState());

    assertEquals(TaskState.READY, createdTask.getState());
    String endOfMessage = " cannot be deleted because its callback is not yet processed";

    Throwable t =
        Assertions.assertThrows(
            InvalidStateException.class, () -> taskService.forceDeleteTask(createdTask.getId()));
    assertTrue(t.getMessage().endsWith(endOfMessage));

    final TaskImpl createdTask2 = (TaskImpl) taskService.claim(createdTask.getId());

    assertEquals(TaskState.CLAIMED, createdTask2.getState());

    Throwable t2 =
        Assertions.assertThrows(
            InvalidStateException.class, () -> taskService.forceDeleteTask(createdTask2.getId()));
    assertTrue(t2.getMessage().endsWith(endOfMessage));

    final TaskImpl createdTask3 = (TaskImpl) taskService.completeTask(createdTask.getId());

    Throwable t3 =
        Assertions.assertThrows(
            InvalidStateException.class, () -> taskService.forceDeleteTask(createdTask3.getId()));
    assertTrue(t3.getMessage().endsWith(endOfMessage));
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testUpdateOfCallbackState()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {

    TaskImpl createdTask1 =
        createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask1.getCallbackState());

    TaskImpl createdTask2 =
        createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask2.getCallbackState());

    TaskImpl createdTask3 =
        createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask3.getCallbackState());

    TaskService taskService = taskanaEngine.getTaskService();
    createdTask1 = (TaskImpl) taskService.forceCompleteTask(createdTask1.getId());
    createdTask2 = (TaskImpl) taskService.forceCompleteTask(createdTask2.getId());
    createdTask3 = (TaskImpl) taskService.forceCompleteTask(createdTask3.getId());

    assertEquals(TaskState.COMPLETED, createdTask1.getState());
    assertEquals(TaskState.COMPLETED, createdTask2.getState());
    assertEquals(TaskState.COMPLETED, createdTask3.getState());

    List<String> taskIds =
        new ArrayList<>(
            Arrays.asList(createdTask1.getId(), createdTask2.getId(), createdTask3.getId()));
    // delete should fail because callback_state = CALLBACK_PROCESSING_REQUIRED
    BulkOperationResults<String, TaskanaException> bulkResult1 = taskService.deleteTasks(taskIds);

    assertTrue(bulkResult1.containsErrors());
    List<String> failedTaskIds = bulkResult1.getFailedIds();

    assertEquals(3, failedTaskIds.size());
    for (String taskId : failedTaskIds) {
      TaskanaException excpt = bulkResult1.getErrorForId(taskId);
      assertEquals("pro.taskana.exceptions.InvalidStateException", excpt.getClass().getName());
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
    assertFalse(bulkResult2.containsErrors());

    taskIds =
        new ArrayList<>(
            Arrays.asList(createdTask1.getId(), createdTask2.getId(), createdTask3.getId()));
    // now it should be possible to delete the tasks
    BulkOperationResults<String, TaskanaException> bulkResult3 = taskService.deleteTasks(taskIds);
    assertFalse(bulkResult3.containsErrors());
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testInvalidUpdateOfCallbackStateToNone()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskImpl createdTask1 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask1.getCallbackState());

    TaskImpl createdTask2 = createTask(taskService, CallbackState.CLAIMED);
    assertEquals(CallbackState.CLAIMED, createdTask2.getCallbackState());

    TaskImpl createdTask3 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_COMPLETED, createdTask3.getCallbackState());

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
    assertTrue(bulkResult.containsErrors());
    List<String> failedTaskIds = bulkResult.getFailedIds();
    assertTrue(failedTaskIds.size() == 3);
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testInvalidUpdateOfCallbackStateToComplete()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, InvalidOwnerException,
          InvalidStateException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskImpl createdTask1 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask1.getCallbackState());

    TaskImpl createdTask2 = createTask(taskService, CallbackState.CLAIMED);
    assertEquals(CallbackState.CLAIMED, createdTask2.getCallbackState());

    TaskImpl createdTask3 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_COMPLETED, createdTask3.getCallbackState());

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
    assertTrue(bulkResult.containsErrors());
    List<String> failedTaskIds = bulkResult.getFailedIds();
    assertTrue(failedTaskIds.size() == 2 && !failedTaskIds.contains(createdTask3.getExternalId()));
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testInvalidUpdateOfCallbackStateToClaimed()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
          InvalidStateException, InvalidOwnerException {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskImpl createdTask1 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask1.getCallbackState());

    TaskImpl createdTask2 = createTask(taskService, CallbackState.CLAIMED);
    assertEquals(CallbackState.CLAIMED, createdTask2.getCallbackState());

    TaskImpl createdTask3 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_COMPLETED, createdTask3.getCallbackState());

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
    assertTrue(bulkResult.containsErrors());
    List<String> failedTaskIds = bulkResult.getFailedIds();
    assertTrue(failedTaskIds.size() == 2 && !failedTaskIds.contains(createdTask1.getExternalId()));
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testInvalidUpdateOfCallbackStateToRequired()
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskImpl createdTask1 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask1.getCallbackState());

    TaskImpl createdTask2 = createTask(taskService, CallbackState.CLAIMED);
    assertEquals(CallbackState.CLAIMED, createdTask2.getCallbackState());

    TaskImpl createdTask3 = createTask(taskService, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertEquals(CallbackState.CALLBACK_PROCESSING_COMPLETED, createdTask3.getCallbackState());

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
    assertTrue(bulkResult.containsErrors());
    List<String> failedTaskIds = bulkResult.getFailedIds();
    assertTrue(failedTaskIds.size() == 1 && failedTaskIds.contains(createdTask3.getExternalId()));
  }

  @WithAccessId(
      userName = "admin",
      groupNames = {"group_1"})
  @Test
  void testQueriesWithCallbackState() throws Exception {
    resetDb(false);
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> claimedTasks =
        taskService.createTaskQuery().stateIn(TaskState.CLAIMED).list();
    assertTrue(claimedTasks.size() > 10);
    taskService.forceCompleteTask(claimedTasks.get(0).getTaskId());
    taskService.forceCompleteTask(claimedTasks.get(1).getTaskId());
    taskService.forceCompleteTask(claimedTasks.get(2).getTaskId());

    // now we should have several completed tasks with callback state NONE.
    // let's set it to CALLBACK_PROCESSING_REQUIRED
    List<TaskSummary> completedTasks =
        taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
    List<String> externalIds =
        completedTasks.stream().map(TaskSummary::getExternalId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> bulkResultCompleted =
        taskService.setCallbackStateForTasks(
            externalIds, CallbackState.CALLBACK_PROCESSING_REQUIRED);
    assertFalse(bulkResultCompleted.containsErrors());

    // now complete some additional tasks
    taskService.forceCompleteTask(claimedTasks.get(3).getTaskId());
    taskService.forceCompleteTask(claimedTasks.get(4).getTaskId());
    taskService.forceCompleteTask(claimedTasks.get(5).getTaskId());

    long numberOfCompletedTasksAtStartOfTest = completedTasks.size();
    // now lets retrieve those completed tasks that have callback_processing_required
    List<TaskSummary> tasksToBeActedUpon =
        taskService
            .createTaskQuery()
            .stateIn(TaskState.COMPLETED)
            .callbackStateIn(CallbackState.CALLBACK_PROCESSING_REQUIRED)
            .list();
    assertEquals(tasksToBeActedUpon.size(), numberOfCompletedTasksAtStartOfTest);
    // now we set callback state to callback_processing_completed
    externalIds =
        tasksToBeActedUpon.stream().map(TaskSummary::getExternalId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> bulkResult =
        taskService.setCallbackStateForTasks(
            externalIds, CallbackState.CALLBACK_PROCESSING_COMPLETED);
    assertFalse(bulkResult.containsErrors());

    long numOfTasksRemaining =
        taskService
            .createTaskQuery()
            .stateIn(TaskState.COMPLETED)
            .callbackStateIn(CallbackState.CALLBACK_PROCESSING_REQUIRED)
            .count();
    assertEquals(0, numOfTasksRemaining);
  }

  private TaskImpl createTask(TaskService taskService, CallbackState callbackState)
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {
    Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
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
