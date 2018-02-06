package acceptance.task;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;

/**
 * Acceptance test for all "query tasks by object reference" scenarios.
 */
public class QueryTasksByObjectReferenceAccTest extends AbstractAccTest {

    public QueryTasksByObjectReferenceAccTest() {
        super();
    }

    @Test
    public void testQueryTasksByExcactValueOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceValueIn("11223344", "22334455")
            .list();
        Assert.assertEquals(32L, results.size());
    }

    @Test
    public void testQueryTasksByExcactValueAndTypeOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceTypeIn("SDNR")
            .primaryObjectReferenceValueIn("11223344")
            .list();
        Assert.assertEquals(10L, results.size());
    }

    @Test
    public void testQueryTasksByValueLikeOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, SystemException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .primaryObjectReferenceValueLike("%567%")
            .list();
        Assert.assertEquals(10L, results.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
