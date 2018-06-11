package acceptance.task;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;

import java.util.HashMap;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "get task" scenarios.
 */

@RunWith(JAASRunner.class)
public class GetTaskAccTest extends AbstractAccTest {

    public GetTaskAccTest() {
        super();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test
    public void testGetTaskById() throws TaskNotFoundException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        Task task = taskService.getTask("TKI:000000000000000000000000000000000000");

        assertEquals(null, task.getCompleted());
        assertEquals("Task99", task.getName());
        assertEquals("creator_user_id", task.getCreator());
        assertEquals("Lorem ipsum was n Quatsch dolor sit amet.", task.getDescription());
        assertEquals("Some custom Note", task.getNote());
        assertEquals(1, task.getPriority());
        assertEquals(TaskState.CLAIMED, task.getState());
        assertEquals("MANUAL", task.getClassificationCategory());
        assertEquals("T2000", task.getClassificationSummary().getKey());
        assertEquals("CLI:100000000000000000000000000000000016", task.getClassificationSummary().getId());
        assertEquals("WBI:100000000000000000000000000000000006", task.getWorkbasketSummary().getId());
        assertEquals("USER_1_1", task.getWorkbasketKey());
        assertEquals("DOMAIN_A", task.getDomain());
        assertEquals("BPI21", task.getBusinessProcessId());
        assertEquals("PBPI21", task.getParentBusinessProcessId());
        assertEquals("user_1_1", task.getOwner());
        assertEquals("MyCompany1", task.getPrimaryObjRef().getCompany());
        assertEquals("MySystem1", task.getPrimaryObjRef().getSystem());
        assertEquals("MyInstance1", task.getPrimaryObjRef().getSystemInstance());
        assertEquals("MyType1", task.getPrimaryObjRef().getType());
        assertEquals("MyValue1", task.getPrimaryObjRef().getValue());
        assertEquals(true, task.isRead());
        assertEquals(false, task.isTransferred());
        assertEquals(new HashMap<String, String>(), task.getCallbackInfo());
        assertEquals(new HashMap<String, String>(), task.getCustomAttributes());
        assertEquals("custom1", task.getCustomAttribute("1"));
        assertEquals("custom2", task.getCustomAttribute("2"));
        assertEquals("custom3", task.getCustomAttribute("3"));
        assertEquals("custom4", task.getCustomAttribute("4"));
        assertEquals("custom5", task.getCustomAttribute("5"));
        assertEquals("custom6", task.getCustomAttribute("6"));
        assertEquals("custom7", task.getCustomAttribute("7"));
        assertEquals("custom8", task.getCustomAttribute("8"));
        assertEquals("custom9", task.getCustomAttribute("9"));
        assertEquals("custom10", task.getCustomAttribute("10"));
        assertEquals("custom11", task.getCustomAttribute("11"));
        assertEquals("custom12", task.getCustomAttribute("12"));
        assertEquals("custom13", task.getCustomAttribute("13"));
        assertEquals("abc", task.getCustomAttribute("14"));
        assertEquals("custom15", task.getCustomAttribute("15"));
        assertEquals("custom16", task.getCustomAttribute("16"));
    }
}
