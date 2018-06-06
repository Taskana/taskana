package acceptance.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BulkOperationResults;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "delete task" scenarios.
 */
@RunWith(JAASRunner.class)
public class DeleteTaskAccTest extends AbstractAccTest {

    public DeleteTaskAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedException.class)
    public void testDeleteSingleTaskNotAuthorized()
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        taskService.deleteTask("TKI:000000000000000000000000000000000037");
        fail("NotAuthorizedException should have been thrown");
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1", "admin"})
    @Test(expected = TaskNotFoundException.class)
    public void testDeleteSingleTask() throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000036");

        taskService.deleteTask(task.getId());

        taskService.getTask("TKI:000000000000000000000000000000000036");
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1", "admin"})
    @Test(expected = InvalidStateException.class)
    public void testThrowsExceptionIfTaskIsNotCompleted()
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000029");

        taskService.deleteTask(task.getId());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1", "admin"})
    @Test(expected = TaskNotFoundException.class)
    public void testForceDeleteTaskIfNotCompleted()
        throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000027");
        try {
            taskService.deleteTask(task.getId());
            fail("Should not be possible to delete claimed task without force flag");
        } catch (InvalidStateException ex) {
            taskService.forceDeleteTask(task.getId());
        }

        taskService.getTask("TKI:000000000000000000000000000000000027");
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
    public void testBulkDeleteTask() throws TaskNotFoundException, InvalidArgumentException, NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000037");
        taskIdList.add("TKI:000000000000000000000000000000000038");

        BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

        assertFalse(results.containsErrors());
        taskService.getTask("TKI:000000000000000000000000000000000038");
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
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
        taskService.getTask("TKI:000000000000000000000000000000000040");

    }

}
