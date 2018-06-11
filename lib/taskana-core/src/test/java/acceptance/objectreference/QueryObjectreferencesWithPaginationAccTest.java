package acceptance.objectreference;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.ObjectReference;
import pro.taskana.ObjectReferenceQuery;
import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query classifications with pagination" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryObjectreferencesWithPaginationAccTest extends AbstractAccTest {

    private TaskService taskService;
    private TaskQuery taskQuery;
    private ObjectReferenceQuery objRefQuery;

    public QueryObjectreferencesWithPaginationAccTest() {
        super();
    }

    @Before
    public void before() {
        taskService = taskanaEngine.getTaskService();
        taskQuery = taskService.createTaskQuery();
        objRefQuery = taskQuery.createObjectReferenceQuery();
    }

    @Test
    public void testGetFirstPageOfObjectRefQueryWithOffset() {

        List<ObjectReference> results = objRefQuery.list(0, 5);
        assertThat(results.size(), equalTo(3));
    }

    @Test
    public void testGetSecondPageOfObjectRefQueryWithOffset() {
        List<ObjectReference> results = objRefQuery.list(2, 5);
        assertThat(results.size(), equalTo(1));
    }

    @Test
    public void testListOffsetAndLimitOutOfBounds() {
        // both will be 0, working
        List<ObjectReference> results = objRefQuery.list(-1, -3);
        assertThat(results.size(), equalTo(0));

        // limit will be 0
        results = objRefQuery.list(1, -3);
        assertThat(results.size(), equalTo(0));

        // offset will be 0
        results = objRefQuery.list(-1, 3);
        assertThat(results.size(), equalTo(3));
    }

    @Test
    public void testPaginationWithPages() {
        // Getting full page
        int pageNumber = 1;
        int pageSize = 10;
        List<ObjectReference> results = objRefQuery.listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(3));

        // Getting full page
        pageNumber = 2;
        pageSize = 2;
        results = objRefQuery.listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(1));

        // Getting last results on 1 big page
        pageNumber = 1;
        pageSize = 100;
        results = objRefQuery.listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(3));

        // Getting last results on multiple pages
        pageNumber = 2;
        pageSize = 2;
        results = objRefQuery.listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(1));
    }

    @Test
    public void testPaginationNullAndNegativeLimitsIgnoring() {
        // 0 limit/size = 0 results
        int pageNumber = 2;
        int pageSize = 0;
        List<ObjectReference> results = objRefQuery.listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative will be 0 = all results
        pageNumber = 2;
        pageSize = -1;
        results = objRefQuery.listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative page = first page
        pageNumber = -1;
        pageSize = 10;
        results = objRefQuery.listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(3));
    }

    /**
     * Testcase only for DB2 users, because H2 doesn´t throw a Exception when the offset is set to high.<br>
     * Using DB2 should throw a unchecked RuntimeException for a offset which is out of bounds.
     */
    @Ignore
    @Test(expected = TaskanaRuntimeException.class)
    public void testPaginationThrowingExceptionWhenPageOutOfBounds() {
        // entrypoint set outside result amount
        int pageNumber = 6;
        int pageSize = 10;
        objRefQuery.listPage(pageNumber, pageSize);
    }

    @Test
    public void testCountOfClassificationsQuery() {
        long count = objRefQuery.count();
        assertThat(count, equalTo(3L));
    }

}
