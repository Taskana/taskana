package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.Workbasket;
import pro.taskana.exceptions.ClassificationNotFoundException;
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

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testTransferTaskToWorkbasketId()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
        taskService.claim(task.getId());
        taskService.setTaskRead(task.getId(), true);

        taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000006");

        Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000003");
        assertNotNull(transferredTask);
        assertTrue(transferredTask.isTransferred());
        assertFalse(transferredTask.isRead());
        assertEquals(TaskState.READY, transferredTask.getState());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testTransferTaskToWorkbasketKeyDomain()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
        taskService.claim(task.getId());
        taskService.setTaskRead(task.getId(), true);

        taskService.transfer(task.getId(), "USER_1_1", "DOMAIN_A");

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
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");
        String domain1 = task.getDomain();

        Task transferedTask = taskService.transfer(task.getId(), "GPK_B_KSC_1", "DOMAIN_B");

        assertNotNull(transferedTask);
        assertNotEquals(domain1, transferedTask.getDomain());
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfTransferWithNoTransferAuthorization()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000001");

        taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005");
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidStateException.class)
    public void testThrowsExceptionIfTaskIsAlreadyCompleted()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:100000000000000000000000000000000006");

        taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000005");
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedException.class)
    public void testThrowsExceptionIfTransferWithNoAppendAuthorization()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task task = taskService.getTask("TKI:000000000000000000000000000000000002");

        taskService.transfer(task.getId(), "WBI:100000000000000000000000000000000008");
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testBulkTransferTaskToWorkbasketById()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        Instant before = Instant.now();
        TaskService taskService = taskanaEngine.getTaskService();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000004");
        taskIdList.add("TKI:000000000000000000000000000000000005");

        BulkOperationResults<String, TaskanaException> results = taskService
            .transferTasks("WBI:100000000000000000000000000000000006", taskIdList);
        assertFalse(results.containsErrors());

        Workbasket wb = taskanaEngine.getWorkbasketService().getWorkbasket("USER_1_1", "DOMAIN_A");
        Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000004");
        assertNotNull(transferredTask);
        assertTrue(transferredTask.isTransferred());
        assertFalse(transferredTask.isRead());
        assertEquals(TaskState.READY, transferredTask.getState());
        assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
        assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
        assertFalse(transferredTask.getModified().isBefore(before));
        assertThat(transferredTask.getOwner(), equalTo(null));
        transferredTask = taskService.getTask("TKI:000000000000000000000000000000000005");
        assertNotNull(transferredTask);
        assertTrue(transferredTask.isTransferred());
        assertFalse(transferredTask.isRead());
        assertEquals(TaskState.READY, transferredTask.getState());
        assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
        assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
        assertFalse(transferredTask.getModified().isBefore(before));
        assertThat(transferredTask.getOwner(), equalTo(null));
    }

    @WithAccessId(userName = "teamlead_1", groupNames = {"group_1"})
    @Test
    public void testBulkTransferTaskWithExceptions()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Workbasket wb = taskanaEngine.getWorkbasketService().getWorkbasket("USER_1_1", "DOMAIN_A");
        Instant before = Instant.now();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000006"); // working
        taskIdList.add("TKI:000000000000000000000000000000000041"); // NotAuthorized READ
        taskIdList.add("TKI:200000000000000000000000000000000006"); // NotAuthorized TRANSFER
        taskIdList.add("");     // InvalidArgument
        taskIdList.add(null);   // InvalidArgument (added with ""), duplicate
        taskIdList.add("TKI:000000000000000000000000000000000099"); // TaskNotFound
        taskIdList.add("TKI:100000000000000000000000000000000006"); // already completed

        BulkOperationResults<String, TaskanaException> results = taskService
            .transferTasks("WBI:100000000000000000000000000000000006", taskIdList);
        assertTrue(results.containsErrors());
        assertThat(results.getErrorMap().values().size(), equalTo(5));
        // react to result
        for (String taskId : results.getErrorMap().keySet()) {
            TaskanaException ex = results.getErrorForId(taskId);
            if (ex instanceof NotAuthorizedException) {
                System.out.println("NotAuthorizedException on bulkTransfer for taskId=" + taskId);
            } else if (ex instanceof InvalidArgumentException) {
                System.out.println("InvalidArgumentException on bulkTransfer for EMPTY/NULL taskId='" + taskId + "'");
            } else if (ex instanceof TaskNotFoundException) {
                System.out.println("TaskNotFoundException on bulkTransfer for taskId=" + taskId);
            } else if (ex instanceof InvalidStateException) {
                System.out.println("InvalidStateException on bulkTransfer for taskId=" + taskId);
            } else {
                fail("Impossible failure Entry registered");
            }
        }
        Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000006");
        assertNotNull(transferredTask);
        assertTrue(transferredTask.isTransferred());
        assertFalse(transferredTask.isRead());
        assertEquals(TaskState.READY, transferredTask.getState());
        assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
        assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
        assertFalse(transferredTask.getModified().isBefore(before));
        assertThat(transferredTask.getOwner(), equalTo(null));

        transferredTask = taskService.getTask("TKI:000000000000000000000000000000000002");
        assertNotNull(transferredTask);
        assertFalse(transferredTask.isTransferred());
        assertEquals("USER_1_1", transferredTask.getWorkbasketKey());

        transferredTask = taskService.getTask("TKI:200000000000000000000000000000000006");
        assertNotNull(transferredTask);
        assertFalse(transferredTask.isTransferred());
        assertEquals("TEAMLEAD_2", transferredTask.getWorkbasketKey());

        transferredTask = taskService.getTask("TKI:100000000000000000000000000000000006");
        assertNotNull(transferredTask);
        assertFalse(transferredTask.isTransferred());
        assertEquals("TEAMLEAD_1", transferredTask.getWorkbasketKey());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testTransferTasksWithListNotSupportingRemove()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<String> taskIds = Collections.singletonList("");
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);

    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test(expected = InvalidArgumentException.class)
    public void testThrowsExceptionIfEmptyListIsSupplied()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<String> taskIds = new ArrayList<>();
        taskService.transferTasks("WBI:100000000000000000000000000000000006", taskIds);

    }

}
