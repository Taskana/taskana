package acceptance.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.impl.BulkOperationResults;
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
    @Test(expected = TaskNotFoundException.class)
    public void testDeleteSingleTask() throws TaskNotFoundException, InvalidStateException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000036");

        taskService.deleteTask(task.getId());

        taskService.getTask("TKI:000000000000000000000000000000000036");
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = InvalidStateException.class)
    public void testThrowsExceptionIfTaskIsNotCompleted()
        throws TaskNotFoundException, InvalidStateException, SQLException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000029");

        taskService.deleteTask(task.getId());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
    public void testForceDeleteTaskIfNotCompleted() throws SQLException, TaskNotFoundException, InvalidStateException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000027");
        try {
            taskService.deleteTask(task.getId());
            fail("Should not be possible to delete claimed task without force flag");
        } catch (InvalidStateException ex) {
            taskService.deleteTask(task.getId(), true);
        }

        taskService.getTask("TKI:000000000000000000000000000000000027");
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
    public void testBulkDeleteTask() throws TaskNotFoundException, InvalidArgumentException {

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
    public void testBulkDeleteTasksWithException() throws TaskNotFoundException, InvalidArgumentException {

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

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
