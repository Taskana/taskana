package acceptance.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.security.CurrentUserContext;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance tests for all claim and complete scenarios.
 */

@RunWith(JAASRunner.class)
public class CompleteTaskAccTest extends AbstractAccTest {

    public CompleteTaskAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCompleteTask()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();

        assertEquals(TaskState.CLAIMED,
                taskService.getTask("TKI:000000000000000000000000000000000001").getState());

        Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000001");
        assertNotNull(completedTask);
        assertNotNull(completedTask.getCompleted());
        assertEquals(TaskState.COMPLETED, completedTask.getState());
        assertNotEquals(completedTask.getCreated(), completedTask.getModified());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCompleteTaskTwice()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000002");
        Task completedTask2 = taskService.completeTask("TKI:000000000000000000000000000000000002");
        assertEquals(completedTask, completedTask2);
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testForceCompleteAlreadyClaimed()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
        InvalidOwnerException, InvalidStateException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setOwner("other");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        TaskImpl newTaskImpl = (TaskImpl) newTask;
        newTaskImpl.setState(TaskState.CLAIMED);
        newTaskImpl.setClaimed(Instant.now());

        Task createdTask = taskService.createTask(newTaskImpl);
        Task completedTask = taskService.forceCompleteTask(createdTask.getId());

        assertEquals(TaskState.COMPLETED, completedTask.getState());
        assertNotNull(completedTask.getCompleted());
        assertNotEquals(completedTask.getCreated(), completedTask.getModified());
        assertEquals(completedTask.getModified(), completedTask.getCompleted());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testForceCompleteNotClaimed()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
        InvalidOwnerException, InvalidStateException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setOwner("other");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        TaskImpl newTaskImpl = (TaskImpl) newTask;
        newTaskImpl.setClaimed(Instant.now());

        Task createdTask = taskService.createTask(newTaskImpl);
        Task completedTask = taskService.forceCompleteTask(createdTask.getId());

        assertEquals(TaskState.COMPLETED, completedTask.getState());
        assertNotNull(completedTask.getCompleted());
        assertNotEquals(completedTask.getCreated(), completedTask.getModified());
        assertEquals(completedTask.getModified(), completedTask.getCompleted());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCompleteTaskThrowsErrors() {
        TaskService taskService = taskanaEngine.getTaskService();
        try {
            taskService.completeTask("TKI:0000000000000000000000000000000000xx");
        } catch (Exception e) {
            Assert.assertEquals(TaskNotFoundException.class, e.getClass());
        }

        try {
            taskService.completeTask("TKI:000000000000000000000000000000000004");
        } catch (Exception e) {
            Assert.assertEquals(NotAuthorizedException.class, e.getClass());
        }

        try {
            taskService.completeTask("TKI:000000000000000000000000000000000025");
        } catch (Exception e) {
            Assert.assertEquals(InvalidStateException.class, e.getClass());
        }

        try {
            taskService.completeTask("TKI:000000000000000000000000000000000026");
        } catch (Exception e) {
            Assert.assertEquals(InvalidOwnerException.class, e.getClass());
        }
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testClaimTaskWithDefaultFlag()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException, InvalidStateException,
        InvalidOwnerException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setOwner(null);
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertNull(createdTask.getClaimed());

        Instant before = Instant.now();
        Task claimedTask = taskService.claim(createdTask.getId());
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Instant after = Instant.now();

        assertNotNull(claimedTask.getOwner());
        assertEquals(claimedTask.getOwner(), CurrentUserContext.getUserid());
        assertNotNull(claimedTask.getClaimed());
        assertTrue(claimedTask.getClaimed().isAfter(before));
        assertTrue(claimedTask.getClaimed().isBefore(after));
        assertTrue(claimedTask.getModified().isAfter(before));
        assertTrue(claimedTask.getModified().isBefore(after));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testForceClaimTaskFromOtherUser()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        newTask.setOwner("other_user");
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertEquals(createdTask.getOwner(), "other_user");

        Instant beforeForceClaim = Instant.now();
        Task taskAfterClaim = taskService.forceClaim(createdTask.getId());

        assertEquals(CurrentUserContext.getUserid(), taskAfterClaim.getOwner());
        assertTrue(taskAfterClaim.getModified().isAfter(beforeForceClaim));
        assertTrue(taskAfterClaim.getClaimed().isAfter(beforeForceClaim));
        assertEquals(TaskState.CLAIMED, taskAfterClaim.getState());
        assertNotEquals(createdTask.getCreated(), taskAfterClaim.getModified());
        assertTrue(taskAfterClaim.isRead());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = TaskNotFoundException.class)
    public void testClaimTaskNotExisting()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
        NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        taskService.claim("NOT_EXISTING");
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidStateException.class)
    public void testClaimTaskWithInvalidState()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
        NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        taskService.forceClaim("TKI:000000000000000000000000000000000036");
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidOwnerException.class)
    public void testClaimTaskWithInvalidOwner()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
        NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        taskService.claim("TKI:000000000000000000000000000000000100");
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidStateException.class)
    public void testCancelClaimForcedWithInvalidState()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
        NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        taskService.forceCancelClaim("TKI:000000000000000000000000000000000036");
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testCancelClaimDefaultFlag()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task newTask = taskService.newTask("USER_1_1", "DOMAIN_A");
        newTask.setClassificationKey("T2100");
        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask);
        assertEquals(createdTask.getState(), TaskState.READY);

        createdTask = taskService.cancelClaim(createdTask.getId());

        assertNotNull(createdTask);
        assertEquals(createdTask.getState(), TaskState.READY);
    }

    @WithAccessId(
        userName = "admin",
        groupNames = {"admin"})
    @Test
    public void testForceCancelClaimSuccessfull()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
        NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        Task taskBefore = taskService.getTask("TKI:000000000000000000000000000000000043");

        assertNotNull(taskBefore);
        assertEquals(TaskState.CLAIMED, taskBefore.getState());

        Instant before = Instant.now();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Task taskAfter = taskService.forceCancelClaim("TKI:000000000000000000000000000000000043");

        assertNotNull(taskAfter);
        assertEquals(TaskState.READY, taskAfter.getState());
        assertNull(taskAfter.getClaimed());
        assertTrue(taskAfter.getModified().isAfter(before));
        assertNull(taskAfter.getOwner());
        assertTrue(taskAfter.isRead());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidOwnerException.class)
    public void testCancelClaimWithInvalidOwner()
        throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
        NotAuthorizedException {

        TaskService taskService = taskanaEngine.getTaskService();
        taskService.cancelClaim("TKI:000000000000000000000000000000000100");
    }

}
