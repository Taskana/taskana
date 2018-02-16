package acceptance.workbasket;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query classifications with pagination" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketsWithPaginationAccTest extends AbstractAccTest {

    public QueryWorkbasketsWithPaginationAccTest() {
        super();
    }

    @Test
    public void testGetFirstPageOfWorkbasketQueryWithOffset()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .list(0, 5);
        assertThat(results.size(), equalTo(5));
    }

    @Test
    public void testGetSecondPageOfWorkbasketQueryWithOffset()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .list(5, 5);
        assertThat(results.size(), equalTo(5));
    }

    @Test
    public void testListOffsetAndLimitOutOfBounds() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        // both will be 0, working
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .list(-1, -3);
        assertThat(results.size(), equalTo(0));

        // limit will be 0
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .list(1, -3);
        assertThat(results.size(), equalTo(0));

        // offset will be 0
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .list(-1, 3);
        assertThat(results.size(), equalTo(3));
    }

    @Test
    public void testPaginationWithPages() throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        // Getting full page
        int pageNumber = 1;
        int pageSize = 4;
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(4));

        // Getting full page
        pageNumber = 3;
        pageSize = 1;
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(1));

        // Getting last results on 1 big page
        pageNumber = 0;
        pageSize = 100;
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(13));

        // Getting last results on multiple pages
        pageNumber = 2;
        pageSize = 5;
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(3));
    }

    @Test
    public void testPaginationNullAndNegativeLimitsIgnoring()
        throws NotAuthorizedException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        // 0 limit/size = 0 results
        int pageNumber = 1;
        int pageSize = 0;
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative size will be 0 = 0 results
        pageNumber = 1;
        pageSize = -1;
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative page = first page
        pageNumber = -1;
        pageSize = 10;
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(10));
    }

    /**
     * Testcase only for DB2 users, because H2 doesn´t throw a Exception when the offset is set to high.<br>
     * Using DB2 should throw a unchecked RuntimeException for a offset which is out of bounds.
     *
     * @throws NotAuthorizedException
     */
    @Ignore
    @Test(expected = TaskanaRuntimeException.class)
    public void testPaginationThrowingExceptionWhenPageOutOfBounds()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        // entrypoint set outside result amount
        int pageNumber = 5;
        int pageSize = 10;
        workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    }

    @Test
    public void testCountOfWorkbasketQuery()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        long count = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .count();
        assertThat(count, equalTo(13L));
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
