package acceptance.workbasket;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query classifications with pagination" scenarios.
 */
@RunWith(JAASRunner.class)
public class WorkbasketQueryWithOrderedPaginationAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public WorkbasketQueryWithOrderedPaginationAccTest() {
        super();
    }

    @Ignore
    @Test
    public void testGetFirstPageOfTaskQueryWithOffset() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .orderByKey(asc)
            .list(0, 5);
        assertThat(results.size(), equalTo(5));
        assertThat(results.get(0).getKey(), equalTo("GPK_KSC"));
        assertThat(results.get(4).getKey(), equalTo("key2"));

        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .orderByKey(desc)
            .list(0, 5);
        assertThat(results.size(), equalTo(5));
        assertThat(results.get(0).getKey(), equalTo("USER_2_2"));
        assertThat(results.get(4).getKey(), equalTo("TEAMLEAD_2"));
    }

    @Ignore
    @Test
    public void testGetSecondPageOfTaskQueryWithOffset() {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .orderByKey(asc)
            .list(5, 5);
        assertThat(results.size(), equalTo(5));
        assertThat(results.get(0).getKey(), equalTo("key3"));
        assertThat(results.get(4).getKey(), equalTo("USER_1_1"));

        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .orderByKey(desc)
            .list(5, 5);
        assertThat(results.size(), equalTo(5));
        assertThat(results.get(0).getKey(), equalTo("TEAMLEAD_1"));
        assertThat(results.get(4).getKey(), equalTo("key3"));
    }

}
