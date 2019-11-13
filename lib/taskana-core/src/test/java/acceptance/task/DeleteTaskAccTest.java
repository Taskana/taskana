package acceptance.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.BulkOperationResults;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "delete task" scenarios.
 */
@ExtendWith(JAASExtension.class)
public class DeleteTaskAccTest extends AbstractAccTest {

    public DeleteTaskAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testDeleteSingleTaskNotAuthorized()
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        Assertions.assertThrows(NotAuthorizedException.class, () ->
            taskService.deleteTask("TKI:000000000000000000000000000000000037"));
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1", "admin"})
    @Test
    public void testDeleteSingleTask()
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000036");

        taskService.deleteTask(task.getId());

        Assertions.assertThrows(TaskNotFoundException.class, () ->
            taskService.getTask("TKI:000000000000000000000000000000000036"));
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1", "admin"})
    @Test
    public void testThrowsExceptionIfTaskIsNotCompleted()
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000029");

        Assertions.assertThrows(InvalidStateException.class, () ->
            taskService.deleteTask(task.getId()));
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1", "admin"})
    @Test
    public void testForceDeleteTaskIfNotCompleted()
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

        Assertions.assertThrows(InvalidStateException.class, () ->
            taskService.deleteTask(task.getId()), "Should not be possible to delete claimed task without force flag");

        taskService.forceDeleteTask(task.getId());

        Assertions.assertThrows(TaskNotFoundException.class, () ->
            taskService.getTask("TKI:000000000000000000000000000000000027"));
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testBulkDeleteTask()
        throws TaskNotFoundException, InvalidArgumentException, NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000037");
        taskIdList.add("TKI:000000000000000000000000000000000038");

        BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

        assertFalse(results.containsErrors());
        Assertions.assertThrows(TaskNotFoundException.class, () ->
            taskService.getTask("TKI:000000000000000000000000000000000038"));
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testBulkDeleteTasksWithException()
        throws TaskNotFoundException, InvalidArgumentException, NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000039");
        taskIdList.add("TKI:000000000000000000000000000000000040");
        taskIdList.add("TKI:000000000000000000000000000000000028");

        BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

        String expectedFailedId = "TKI:000000000000000000000000000000000028";
        assertTrue(results.containsErrors());
        List<String> failedTaskIds = results.getFailedIds();
        assertTrue(failedTaskIds.size() == 1);
        assertTrue(expectedFailedId.equals(failedTaskIds.get(0)));
        assertTrue(results.getErrorMap().get(expectedFailedId).getClass() == InvalidStateException.class);

        Task notDeletedTask = taskService.getTask("TKI:000000000000000000000000000000000028");
        assertTrue(notDeletedTask != null);
        Assertions.assertThrows(TaskNotFoundException.class, () ->
            taskService.getTask("TKI:000000000000000000000000000000000040"));

    }

}
