package acceptance.task;

import java.sql.SQLException;
import java.util.ArrayList;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
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

    @Ignore
    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
    public void testDeleteSingleTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000036");

        // taskService.deleteTask(task.getId());

        Task deletedTask = taskService.getTask("TKI:000000000000000000000000000000000036");
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = InvalidStateException.class)
    public void testThrowsExceptionIfTaskIsNotCompleted()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

        // taskService.deleteTask(task.getId());
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
    public void testForceDeleteTaskIfNotCompleted()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

        // taskService.deleteTask(task.getId(), true);

        Task deletedTask = taskService.getTask("TKI:000000000000000000000000000000000036");
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
    public void testBulkDeleteTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000037");
        taskIdList.add("TKI:000000000000000000000000000000000038");

        // BulkOperationResults results = taskService.deleteTasks(taskIdList);

        // assertFalse(results.containsError());
        Task deletedTask = taskService.getTask("TKI:000000000000000000000000000000000038");
    }

    @Ignore
    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
    public void testBulkDeleteTasksWithException()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000039");
        taskIdList.add("TKI:000000000000000000000000000000000040");
        taskIdList.add("TKI:000000000000000000000000000000000028");

        // BulkOperationResults results = taskService.deleteTasks(taskIdList);

        // assertTrue(results.containsError());
        // more assertions ...
        Task notDeletedTask = taskService.getTask("TKI:000000000000000000000000000000000028");
        Task deletedTask = taskService.getTask("TKI:000000000000000000000000000000000040");
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
