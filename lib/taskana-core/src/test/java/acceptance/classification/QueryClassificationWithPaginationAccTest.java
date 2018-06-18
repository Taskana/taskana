package acceptance.classification;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.TaskanaRuntimeException;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query classifications with pagination" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryClassificationWithPaginationAccTest extends AbstractAccTest {

    public QueryClassificationWithPaginationAccTest() {
        super();
    }

    @Test
    public void testGetFirstPageOfClassificationQueryWithOffset() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .list(0, 5);
        assertThat(results.size(), equalTo(5));
    }

    @Test
    public void testGetSecondPageOfClassificationQueryWithOffset() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .list(5, 5);
        assertThat(results.size(), equalTo(5));
    }

    @Test
    public void testListOffsetAndLimitOutOfBounds() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();

        // both will be 0, working
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .list(-1, -3);
        assertThat(results.size(), equalTo(0));

        // limit will be 0
        results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .list(1, -3);
        assertThat(results.size(), equalTo(0));

        // offset will be 0
        results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .list(-1, 3);
        assertThat(results.size(), equalTo(3));
    }

    @Test
    public void testPaginationWithPages() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();

        // Getting full page
        int pageNumber = 1;
        int pageSize = 4;
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(4));

        // Getting full page
        pageNumber = 3;
        pageSize = 4;
        results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(4));

        // Getting last results on 1 big page
        pageNumber = 1;
        pageSize = 100;
        results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(17));

        // Getting last results on multiple pages
        pageNumber = 2;
        pageSize = 10;
        results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(7));
    }

    @Test
    public void testPaginationNullAndNegativeLimitsIgnoring() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();

        // 0 limit/size = 0 results
        int pageNumber = 1;
        int pageSize = 0;
        List<ClassificationSummary> results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative will be 0 = all results
        pageNumber = 1;
        pageSize = -1;
        results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(0));

        // Negative page = first page
        pageNumber = -1;
        pageSize = 10;
        results = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
        assertThat(results.size(), equalTo(10));
    }

    /**
     * Testcase only for DB2 users, because H2 doesn´t throw a Exception when the offset is set to high.<br>
     * Using DB2 should throw a unchecked RuntimeException for a offset which is out of bounds.
     */
    @Ignore
    @Test(expected = TaskanaRuntimeException.class)
    public void testPaginationThrowingExceptionWhenPageOutOfBounds() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();

        // entrypoint set outside result amount
        int pageNumber = 5;
        int pageSize = 10;
        classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    }

    @Test
    public void testCountOfClassificationsQuery() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        long count = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .count();
        assertThat(count, equalTo(17L));
    }

}
