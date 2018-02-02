package acceptance.workbasket;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidRequestException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query classifications with pagination" scenarios.
 */
@RunWith(JAASRunner.class)
public class WorkbasketQueryWithOrderedPaginationAccTest extends AbstractAccTest {

    public WorkbasketQueryWithOrderedPaginationAccTest() {
        super();
    }

    @Ignore
    @Test
    public void testGetFirstPageOfTaskQueryWithOffset()
        throws NotAuthorizedException, InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .orderByKey()
            .ascending()
            .list(0, 5);
        assertThat(results.size(), equalTo(5));
        assertThat(results.get(0).getKey(), equalTo("GPK_KSC"));
        assertThat(results.get(4).getKey(), equalTo("key2"));

        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .orderByKey()
            .descending()
            .list(0, 5);
        assertThat(results.size(), equalTo(5));
        assertThat(results.get(0).getKey(), equalTo("USER_2_2"));
        assertThat(results.get(4).getKey(), equalTo("TEAMLEAD_2"));
    }

    @Ignore
    @Test
    public void testGetSecondPageOfTaskQueryWithOffset()
        throws NotAuthorizedException, InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .orderByKey()
            .ascending()
            .list(5, 5);
        assertThat(results.size(), equalTo(5));
        assertThat(results.get(0).getKey(), equalTo("key3"));
        assertThat(results.get(4).getKey(), equalTo("USER_1_1"));

        results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .orderByKey()
            .descending()
            .list(5, 5);
        assertThat(results.size(), equalTo(5));
        assertThat(results.get(0).getKey(), equalTo("TEAMLEAD_1"));
        assertThat(results.get(4).getKey(), equalTo("key3"));
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
