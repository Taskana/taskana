package acceptance.task;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.BulkOperationResults;
import pro.taskana.CallbackState;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "create task" scenarios.
 */
@ExtendWith(JAASExtension.class)
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
    }

    @WithAccessId(
        userName = "admin",
        groupNames = {"group_1"})
    @Test
    void testDeletionOfTaskWithWrongCallbackStateIsBlocked()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException, InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();

        TaskImpl createdTask = createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
        assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask.getCallbackState());

        assertEquals(TaskState.READY, createdTask.getState());
        try {
            taskService.forceDeleteTask(createdTask.getId());
            fail();
        } catch (InvalidStateException ex) {
            String endOfMessage = " cannot be deleted because its callback is not yet processed";
            assertTrue(ex.getMessage().endsWith(endOfMessage));
            assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask.getCallbackState());
        }

        createdTask = (TaskImpl) taskService.claim(createdTask.getId());

        assertEquals(TaskState.CLAIMED, createdTask.getState());
        try {
            taskService.forceDeleteTask(createdTask.getId());
            fail();
        } catch (InvalidStateException ex) {
            String endOfMessage = " cannot be deleted because its callback is not yet processed";
            assertTrue(ex.getMessage().endsWith(endOfMessage));
            assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask.getCallbackState());
        }

        createdTask = (TaskImpl) taskService.completeTask(createdTask.getId());

        assertEquals(TaskState.COMPLETED, createdTask.getState());
        try {
            taskService.forceDeleteTask(createdTask.getId());
            fail();
        } catch (InvalidStateException ex) {
            String endOfMessage = " cannot be deleted because its callback is not yet processed";
            assertTrue(ex.getMessage().endsWith(endOfMessage));
            assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask.getCallbackState());
        }
    }

    @WithAccessId(
        userName = "admin",
        groupNames = {"group_1"})
    @Test
    void testUpdateOfCallbackState()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException, InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();

        TaskImpl createdTask1 = createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
        assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask1.getCallbackState());

        TaskImpl createdTask2 = createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
        assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask2.getCallbackState());

        TaskImpl createdTask3 = createTask(taskanaEngine.getTaskService(), CallbackState.CALLBACK_PROCESSING_REQUIRED);
        assertEquals(CallbackState.CALLBACK_PROCESSING_REQUIRED, createdTask3.getCallbackState());

        createdTask1 = (TaskImpl) taskService.forceCompleteTask(createdTask1.getId());
        createdTask2 = (TaskImpl) taskService.forceCompleteTask(createdTask2.getId());
        createdTask3 = (TaskImpl) taskService.forceCompleteTask(createdTask3.getId());

        assertEquals(TaskState.COMPLETED, createdTask1.getState());
        assertEquals(TaskState.COMPLETED, createdTask1.getState());
        assertEquals(TaskState.COMPLETED, createdTask1.getState());

        List<String> taskIds = new ArrayList<>(Arrays.asList(createdTask1.getId(), createdTask2.getId(), createdTask3.getId()));
        // delete should fail because callback_state = CALLBACK_PROCESSING_REQUIRED
        BulkOperationResults<String, TaskanaException> bulkResult1 = taskService.deleteTasks(taskIds);

        assertTrue(bulkResult1.containsErrors());
        List<String> failedTaskIds = bulkResult1.getFailedIds();

        assertTrue(failedTaskIds.size() == 3);
        for (String taskId : failedTaskIds) {
            TaskanaException excpt = bulkResult1.getErrorForId(taskId);
            assertEquals("pro.taskana.exceptions.InvalidStateException", excpt.getClass().getName());
        }

        // now enable deletion by setting callback state to CALLBACK_PROCESSING_COMPLETED
        taskIds = new ArrayList<>(Arrays.asList(createdTask1.getId(), createdTask2.getId(), createdTask3.getId()));
        BulkOperationResults<String, TaskanaException> bulkResult2 = taskService.setCallbackStateForTasks(taskIds, CallbackState.CALLBACK_PROCESSING_COMPLETED);
        assertFalse(bulkResult2.containsErrors());

        taskIds = new ArrayList<>(Arrays.asList(createdTask1.getId(), createdTask2.getId(), createdTask3.getId()));
        // now it should be possible to delete the tasks
        BulkOperationResults<String, TaskanaException> bulkResult3 = taskService.deleteTasks(taskIds);
        assertFalse(bulkResult3.containsErrors());
    }

    @WithAccessId(
        userName = "admin",
        groupNames = {"group_1"})
    @Test
    void testQueriesWithCallbackState()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException, InvalidStateException, InvalidOwnerException, SQLException, IOException {
        resetDb(false);
        TaskService taskService = taskanaEngine.getTaskService();

        List<TaskSummary> claimedTasks = taskService.createTaskQuery()
            .stateIn(TaskState.CLAIMED)
            .list();
        assertTrue(claimedTasks.size() > 10);
        taskService.forceCompleteTask(claimedTasks.get(0).getTaskId());
        taskService.forceCompleteTask(claimedTasks.get(1).getTaskId());
        taskService.forceCompleteTask(claimedTasks.get(2).getTaskId());

        // now we should have several completed tasks with callback state NONE.
        // let's set it to CALLBACK_PROCESSING_REQUIRED
        List<TaskSummary> completedTasks = taskService.createTaskQuery()
            .stateIn(TaskState.COMPLETED)
            .list();
        long numberOfCompletedTasksAtStartOfTest = completedTasks.size();
        List<String> taskIds = completedTasks.stream().map(TaskSummary::getTaskId).collect(Collectors.toList());
        BulkOperationResults<String, TaskanaException> bulkResultCompleted = taskService.setCallbackStateForTasks(taskIds, CallbackState.CALLBACK_PROCESSING_REQUIRED);
        assertFalse(bulkResultCompleted.containsErrors());

        // now complete some additional tasks
        taskService.forceCompleteTask(claimedTasks.get(3).getTaskId());
        taskService.forceCompleteTask(claimedTasks.get(4).getTaskId());
        taskService.forceCompleteTask(claimedTasks.get(5).getTaskId());

        // now lets retrieve those completed tasks that have callback_processing_required
        List<TaskSummary> tasksToBeActedUpon = taskService.createTaskQuery()
            .stateIn(TaskState.COMPLETED)
            .callbackStateIn(CallbackState.CALLBACK_PROCESSING_REQUIRED)
            .list();
        assertTrue(tasksToBeActedUpon.size() == numberOfCompletedTasksAtStartOfTest);
        // now we set callback state to callback_processing_completed
        taskIds = tasksToBeActedUpon.stream().map(TaskSummary::getTaskId).collect(Collectors.toList());
        BulkOperationResults<String, TaskanaException> bulkResult = taskService.setCallbackStateForTasks(taskIds, CallbackState.CALLBACK_PROCESSING_COMPLETED);
        assertFalse(bulkResult.containsErrors());

        long numOfTasksRemaining = taskService.createTaskQuery()
            .stateIn(TaskState.COMPLETED)
            .callbackStateIn(CallbackState.CALLBACK_PROCESSING_REQUIRED)
            .count();
        assertTrue(numOfTasksRemaining == 0);

    }

    private TaskImpl createTask(TaskService taskService, CallbackState callbackState) throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException, TaskAlreadyExistException, InvalidArgumentException {
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("L12010");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setClassificationKey("L12010");
        HashMap<String, String> callbackInfo = new HashMap<>();
        callbackInfo.put(Task.CALLBACK_STATE, callbackState.name());
        newTask.setCallbackInfo(callbackInfo);
        augmentCallbackInfo(newTask);

        TaskImpl createdTask = (TaskImpl) taskService.createTask(newTask);
        return createdTask;
    }

    private void augmentCallbackInfo(Task task) {
        Map<String, String> callbackInfo = task.getCallbackInfo();
        for (int i = 1; i <= 10; i++) {
            callbackInfo.put("info_" + i, "Value of info_" + i);
        }
        task.setCallbackInfo(callbackInfo);

    }

}
