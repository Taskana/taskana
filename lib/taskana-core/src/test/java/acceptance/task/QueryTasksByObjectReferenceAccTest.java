package acceptance.task;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Acceptance test for all "query tasks by object reference" scenarios.
 */
public class QueryTasksByObjectReferenceAccTest extends AbstractAccTest {

    public QueryTasksByObjectReferenceAccTest() {
        super();
    }

    @Test
    public void testQueryTasksByExcactValueOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<Task> results = taskService.createTaskQuery()
            .primaryObjectReferenceValueIn("Value1", "Value2")
            .list();
        Assert.assertEquals(10L, results.size());
    }

    @Test
    public void testQueryTasksByExcactValueAndTypeOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<Task> results = taskService.createTaskQuery()
            .primaryObjectReferenceTypeIn("Type3")
            .primaryObjectReferenceValueIn("Value3")
            .list();
        Assert.assertEquals(4L, results.size());
    }

    @Test
    public void testQueryTasksByValueLikeOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<Task> results = taskService.createTaskQuery()
            .primaryObjectReferenceValueLike("Val%")
            .list();
        Assert.assertEquals(14L, results.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
