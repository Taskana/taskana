package acceptance.workbasket;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query classifications with pagination" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketsWithPaginationAccTest extends AbstractAccTest {

    public QueryWorkbasketsWithPaginationAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testGetFirstPageOfWorkbasketQueryWithOffset() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .list(0, 5);
        assertThat(results.size(), equalTo(5));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testGetSecondPageOfWorkbasketQueryWithOffset() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .list(5, 5);
        assertThat(results.size(), equalTo(4));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testListOffsetAndLimitOutOfBounds() {
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

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testPaginationWithPages() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        // Getting full page
        int pageNumber = 2;
        int pageSize = 4;
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(4));

        // Getting full page
        pageNumber = 4;
        pageSize = 1;
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(1));

        // Getting last results on 1 big page
        pageNumber = 1;
        pageSize = 100;
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(9));

        // Getting last results on multiple pages
        pageNumber = 2;
        pageSize = 5;
        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(4));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testPaginationNullAndNegativeLimitsIgnoring() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        // 0 limit/size = 0 results
        int pageNumber = 2;
        int pageSize = 0;
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative size will be 0 = 0 results
        pageNumber = 2;
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
        assertThat(results.size(), equalTo(9));
    }

    /**
     * Testcase only for DB2 users, because H2 doesn´t throw a Exception when the offset is set to high.<br>
     * Using DB2 should throw a unchecked RuntimeException for a offset which is out of bounds.
     */
    @Ignore
    @Test(expected = TaskanaRuntimeException.class)
    public void testPaginationThrowingExceptionWhenPageOutOfBounds() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

        // entrypoint set outside result amount
        int pageNumber = 6;
        int pageSize = 10;
        workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testCountOfWorkbasketQuery() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        long count = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .count();
        assertThat(count, equalTo(9L));
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testWorkbasketQueryDomA() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> result = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .list();
        assertThat(result.size(), equalTo(9));
    }

}
