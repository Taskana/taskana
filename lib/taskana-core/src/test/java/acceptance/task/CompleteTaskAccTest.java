package acceptance.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
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
        Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000001");
        assertNotNull(completedTask);
    }

    @WithAccessId(
            userName = "user_1_1",
            groupNames = {"group_1"})
    @Test
    public void testCompleteTaskTwice() throws TaskNotFoundException, InvalidStateException, InvalidOwnerException, NotAuthorizedException {
        TaskService taskService = taskanaEngine.getTaskService();
        Task completedTask = taskService.completeTask("TKI:000000000000000000000000000000000002");
        Task completedTask2 = taskService.completeTask("TKI:000000000000000000000000000000000002");
        assertEquals(completedTask, completedTask2);
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
}
