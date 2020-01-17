package acceptance.classification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.security.JaasExtension;

/** Acceptance test for all "query classifications with pagination" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryClassificationWithPaginationAccTest extends AbstractAccTest {

  QueryClassificationWithPaginationAccTest() {
    super();
  }

  @Test
  void testGetFirstPageOfClassificationQueryWithOffset() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(0, 5);
    assertThat(results.size(), equalTo(5));
  }

  @Test
  void testGetSecondPageOfClassificationQueryWithOffset() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(5, 5);
    assertThat(results.size(), equalTo(5));
  }

  @Test
  void testListOffsetAndLimitOutOfBounds() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();

    // both will be 0, working
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(-1, -3);
    assertThat(results.size(), equalTo(0));

    // limit will be 0
    results = classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(1, -3);
    assertThat(results.size(), equalTo(0));

    // offset will be 0
    results = classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(-1, 3);
    assertThat(results.size(), equalTo(3));
  }

  @Test
  void testPaginationWithPages() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();

    // Getting full page
    int pageNumber = 1;
    int pageSize = 4;
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(4));

    // Getting full page
    pageNumber = 3;
    pageSize = 4;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(4));

    // Getting last results on 1 big page
    pageNumber = 1;
    pageSize = 100;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(17));

    // Getting last results on multiple pages
    pageNumber = 2;
    pageSize = 10;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(7));
  }

  @Test
  void testPaginationNullAndNegativeLimitsIgnoring() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();

    // 0 limit/size = 0 results
    int pageNumber = 1;
    int pageSize = 0;
    List<ClassificationSummary> results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(0));

    // Negative will be 0 = all results
    pageNumber = 1;
    pageSize = -1;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(0));

    // Negative page = first page
    pageNumber = -1;
    pageSize = 10;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(10));
  }

  @Test
  void testCountOfClassificationsQuery() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    long count = classificationService.createClassificationQuery().domainIn("DOMAIN_A").count();
    assertThat(count, equalTo(17L));
  }
}
