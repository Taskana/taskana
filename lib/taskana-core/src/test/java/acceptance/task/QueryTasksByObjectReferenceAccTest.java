package acceptance.task;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.SystemException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query tasks by object reference" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksByObjectReferenceAccTest extends AbstractAccTest {

    public QueryTasksByObjectReferenceAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryTasksByExcactValueOfObjectReference()
        throws SystemException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceValueIn("11223344", "22334455")
            .list();
        Assert.assertEquals(33L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryTasksByExcactValueAndTypeOfObjectReference()
        throws SystemException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceTypeIn("SDNR")
            .primaryObjectReferenceValueIn("11223344")
            .list();
        Assert.assertEquals(10L, results.size());
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testQueryTasksByValueLikeOfObjectReference()
        throws SystemException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceValueLike("%567%")
            .list();
        Assert.assertEquals(10L, results.size());
    }

}
