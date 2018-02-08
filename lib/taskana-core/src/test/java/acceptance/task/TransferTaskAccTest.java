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

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
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

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testBulkTransferTask()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        Instant before = Instant.now();
        TaskService taskService = taskanaEngine.getTaskService();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000004");
        taskIdList.add("TKI:000000000000000000000000000000000005");

        BulkOperationResults<String, TaskanaException> results = taskService.transferBulk("USER_1_1", taskIdList);
        assertFalse(results.containErrors());

        Workbasket wb = taskanaEngine.getWorkbasketService().getWorkbasketByKey("USER_1_1");
        Task transferredTask = taskService.getTask("TKI:000000000000000000000000000000000004");
        assertNotNull(transferredTask);
        assertTrue(transferredTask.isTransferred());
        assertFalse(transferredTask.isRead());
        assertEquals(TaskState.READY, transferredTask.getState());
        assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
        assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
        assertTrue(transferredTask.getModified().isAfter(before));
        assertThat(transferredTask.getOwner(), equalTo(null));
        transferredTask = taskService.getTask("TKI:000000000000000000000000000000000005");
        assertNotNull(transferredTask);
        assertTrue(transferredTask.isTransferred());
        assertFalse(transferredTask.isRead());
        assertEquals(TaskState.READY, transferredTask.getState());
        assertThat(transferredTask.getWorkbasketKey(), equalTo(wb.getKey()));
        assertThat(transferredTask.getDomain(), equalTo(wb.getDomain()));
        assertTrue(transferredTask.getModified().isAfter(before));
        assertThat(transferredTask.getOwner(), equalTo(null));
    }

    @WithAccessId(userName = "teamlead_1", groupNames = {"group_1"})
    @Test
    public void testBulkTransferTaskWithExceptions()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, ClassificationNotFoundException,
        WorkbasketNotFoundException, TaskAlreadyExistException, InvalidWorkbasketException, TaskNotFoundException,
        InvalidStateException, InvalidOwnerException {
        TaskService taskService = taskanaEngine.getTaskService();
        Workbasket wb = taskanaEngine.getWorkbasketService().getWorkbasketByKey("USER_1_1");
        Instant before = Instant.now();
        ArrayList<String> taskIdList = new ArrayList<>();
        taskIdList.add("TKI:000000000000000000000000000000000006"); // working
        taskIdList.add("TKI:000000000000000000000000000000000002"); // NotAuthorized
        taskIdList.add("");     // InvalidArgument
        taskIdList.add(null);   // InvalidArgument (added with ""), duplicate
        taskIdList.add("TKI:000000000000000000000000000000000099"); // TaskNotFound

        BulkOperationResults<String, TaskanaException> results = taskService.transferBulk("USER_1_1", taskIdList);
        assertTrue(results.containErrors());
        assertThat(results.getErrorMap().values().size(), equalTo(3));
        // react to result
        for (String taskId : results.getErrorMap().keySet()) {
            TaskanaException ex = results.getErrorForId(taskId);
            if (ex instanceof NotAuthorizedException) {
                System.out.println("NotAuthorizedException on bulkTransfer for taskId=" + taskId);
            } else if (ex instanceof InvalidArgumentException) {
                System.out.println("InvalidArgumentException on bulkTransfer for EMPTY/NULL taskId='" + taskId + "'");
            } else if (ex instanceof TaskNotFoundException) {
                System.out.println("TaskNotFoundException on bulkTransfer for taskId=" + taskId);
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
        assertTrue(transferredTask.getModified().isAfter(before));
        assertThat(transferredTask.getOwner(), equalTo(null));

        transferredTask = taskService.getTask("TKI:000000000000000000000000000000000002");
        assertNotNull(transferredTask);
        assertFalse(transferredTask.isTransferred());
        assertEquals("GPK_B_KSC", transferredTask.getWorkbasketKey());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
