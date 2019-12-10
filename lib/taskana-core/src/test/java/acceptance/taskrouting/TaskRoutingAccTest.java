package acceptance.taskrouting;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "create task" scenarios.
 */
@ExtendWith(JAASExtension.class)
class TaskRoutingAccTest extends AbstractAccTest {

    @WithAccessId(userName = "admin", groupNames = {"group_1"})
    @Test
    void testCreateTaskWithNullWorkbasket()
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException, TaskNotFoundException {
        TaskImpl createdTaskA = createTask("DOMAIN_A", "L12010");
        assertEquals("WBI:100000000000000000000000000000000001", createdTaskA.getWorkbasketSummary().getId());
        TaskImpl createdTaskB = createTask("DOMAIN_B", "T21001");
        assertEquals("WBI:100000000000000000000000000000000011", createdTaskB.getWorkbasketSummary().getId());
        Assertions.assertThrows(InvalidArgumentException.class, () -> createTask(null, "L12010"));
    }

    private TaskImpl createTask(String domain, String classificationKey)
        throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        TaskAlreadyExistException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        Task newTask = taskService.newTask(null, domain);
        newTask.setClassificationKey(classificationKey);

        newTask.setPrimaryObjRef(createObjectReference("COMPANY_A", "SYSTEM_A", "INSTANCE_A", "VNR", "1234567"));
        TaskImpl createdTask = (TaskImpl) taskService.createTask(newTask);
        return createdTask;
    }

}
