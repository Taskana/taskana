package acceptance.task;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Acceptance test for all "query tasks by object reference" scenarios.
 */
public class QueryTasksByObjectReferenceAccTest extends AbstractAccTest {

    public QueryTasksByObjectReferenceAccTest() {
        super();
    }

    @Ignore
    @Test
    public void testQueryTasksByExcactValueOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        // TaskService taskService = taskanaEngine.getTaskService();
        // List<TaskSummary> results = taskService.createTaskQuery()
        // .primaryObjectReferenceValueEquals("223344")
        // .list();
        // Assert.assertEquals(5L, results.size());
    }

    @Ignore
    @Test
    public void testQueryTasksByExcactValueAndTypeOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        // TaskService taskService = taskanaEngine.getTaskService();
        // List<TaskSummary> results = taskService.createTaskQuery()
        // .primaryObjectReferenceTypeEquals("VNR")
        // .primaryObjectReferenceValueEquals("223344")
        // .list();
        // Assert.assertEquals(3L, results.size());
    }

    @Ignore
    @Test
    public void testQueryTasksByValueLikeOfObjectReference()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        // TaskService taskService = taskanaEngine.getTaskService();
        // List<TaskSummary> results = taskService.createTaskQuery()
        // .primaryObjectReferenceValueLike("223")
        // .list();
        // Assert.assertEquals(15L, results.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
