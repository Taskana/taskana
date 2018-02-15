package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.BulkOperationResults;
import pro.taskana.model.TaskState;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "work on task" scenarios. This includes claim, complete...
 */
@RunWith(JAASRunner.class)
public class WorkOnTaskAccTest extends AbstractAccTest {

    public WorkOnTaskAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testClaimTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000025");

        taskService.claim(task.getId());

        Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000025");
        assertNotNull(claimedTask);
        assertEquals(TaskState.CLAIMED, claimedTask.getState());
        assertNotNull(claimedTask.getClaimed());
        assertNotEquals(claimedTask.getCreated(), claimedTask.getModified());
        assertEquals(claimedTask.getClaimed(), claimedTask.getModified());
        assertTrue(claimedTask.isRead());
        assertEquals("user_1_2", claimedTask.getOwner());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = InvalidOwnerException.class)
    public void testThrowsExceptionIfTaskIsAlreadyClaimed()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000026");

        taskService.claim(task.getId());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testClaimAlreadyClaimedByCallerTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

        taskService.claim(task.getId());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = InvalidOwnerException.class)
    public void testForceClaimTaskWhichIsAlreadyClaimedByAnotherUser()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000028");

        taskService.claim(task.getId());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testCancelClaimTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000029");

        taskService.cancelClaim(claimedTask.getId());

        Task unclaimedTask = taskService.getTask("TKI:000000000000000000000000000000000029");
        assertNotNull(unclaimedTask);
        assertEquals(TaskState.READY, unclaimedTask.getState());
        assertNull(unclaimedTask.getClaimed());
        assertTrue(unclaimedTask.isRead());
        assertNull(unclaimedTask.getOwner());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = InvalidOwnerException.class)
    public void testThrowsExceptionIfCancelClaimOfTaskFromAnotherUser()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000030");

        taskService.cancelClaim(claimedTask.getId());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testForceCancelClaimOfTaskFromAnotherUser()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000031");

        taskService.cancelClaim(claimedTask.getId(), true);

        Task unclaimedTask = taskService.getTask("TKI:000000000000000000000000000000000031");
        assertNotNull(unclaimedTask);
        assertEquals(TaskState.READY, unclaimedTask.getState());
        assertNull(unclaimedTask.getClaimed());
        assertTrue(unclaimedTask.isRead());
        assertNull(unclaimedTask.getOwner());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testCompleteTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        Instant before = Instant.now().minus(Duration.ofSeconds(3L));
        TaskService taskService = taskanaEngine.getTaskService();
        Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000032");

        taskService.completeTask(claimedTask.getId());

        Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000032");
        assertNotNull(completedTask);
        assertEquals(TaskState.COMPLETED, completedTask.getState());
        assertNotNull(completedTask.getCompleted());
        assertEquals(completedTask.getCompleted(), completedTask.getModified());
        assertTrue(completedTask.getCompleted().isAfter(before));
        assertTrue(completedTask.getModified().isAfter(before));
        assertTrue(completedTask.isRead());
        assertEquals("user_1_2", completedTask.getOwner());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testForceCompleteUnclaimedTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000033");

        taskService.completeTask(claimedTask.getId(), true);

        Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000033");
        assertNotNull(completedTask);
        assertEquals(TaskState.COMPLETED, completedTask.getState());
        assertNotNull(completedTask.getCompleted());
        assertEquals(completedTask.getCompleted(), completedTask.getModified());
        assertTrue(completedTask.isRead());
        assertEquals("user_1_2", completedTask.getOwner());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test(expected = InvalidOwnerException.class)
    public void testThrowsExceptionIfCompletingClaimedTaskOfAnotherUser()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000034");

        taskService.completeTask(claimedTask.getId());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testForceCompleteClaimedTaskOfAnotherUser()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task claimedTask = taskService.getTask("TKI:000000000000000000000000000000000035");

        taskService.completeTask(claimedTask.getId(), true);

        Task completedTask = taskService.getTask("TKI:000000000000000000000000000000000035");
        assertNotNull(completedTask);
        assertEquals(TaskState.COMPLETED, completedTask.getState());
        assertNotNull(completedTask.getCompleted());
        assertEquals(completedTask.getCompleted(), completedTask.getModified());
        assertTrue(completedTask.isRead());
        assertEquals("user_1_2", completedTask.getOwner());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testBulkCompleteTasks()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        List<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000100");
        taskIdList.add("TKI:000000000000000000000000000000000101");

        BulkOperationResults<String, TaskanaException> results = taskService.completeTasks(taskIdList);

        assertFalse(results.containsErrors());
        Task completedTask1 = taskService.getTask("TKI:000000000000000000000000000000000100");
        assertEquals(TaskState.COMPLETED, completedTask1.getState());
        assertNotNull(completedTask1.getCompleted());
        Task completedTask2 = taskService.getTask("TKI:000000000000000000000000000000000101");
        assertEquals(TaskState.COMPLETED, completedTask2.getState());
        assertNotNull(completedTask2.getCompleted());
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testBulkDeleteTasksWithException()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        ConcurrencyException, AttachmentPersistenceException {

        TaskService taskService = taskanaEngine.getTaskService();
        List<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000102");
        taskIdList.add("TKI:000000000000000000000000000000003333");

        BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

        assertTrue(results.containsErrors());
        assertThat(results.getErrorMap().size(), equalTo(2));
        assertTrue(results.getErrorForId("TKI:000000000000000000000000000000003333") instanceof TaskNotFoundException);
        assertTrue(results.getErrorForId("TKI:000000000000000000000000000000000102") instanceof InvalidStateException);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
