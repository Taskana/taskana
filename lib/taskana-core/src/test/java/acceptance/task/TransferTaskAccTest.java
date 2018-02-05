package acceptance.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.TaskState;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "update task" scenarios.
 */
@RunWith(JAASRunner.class)
public class TransferTaskAccTest extends AbstractAccTest {

    public TransferTaskAccTest() {
        super();
    }

    @Ignore
    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testTransferTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
        taskService.claim(task.getId());
        taskService.setTaskRead(task.getId(), true);

        taskService.transfer(task.getId(), "USER_1_1");

        Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
        assertNotNull(transferredTask);
        assertTrue(transferredTask.isTransferred());
        assertFalse(transferredTask.isRead());
        assertEquals(TaskState.READY, transferredTask.getState());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testDomainChangingWhenTransferTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        String domain1 = task.getDomain();

        Task transferedTask = taskService.transfer(task.getId(), "GPK_B_KSC_1");

        assertNotNull(transferedTask);
        assertNotEquals(domain1, transferedTask.getDomain());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfTransferWithNoTransferAuthorization()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000001");

        taskService.transfer(task.getId(), "TEAMLEAD_2");
    }

    @WithAccessId(
        userName = "USER_1_1",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfTransferWithNoAppendAuthorization()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000002");

        taskService.transfer(task.getId(), "USER_1_1");
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
