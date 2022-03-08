package acceptance.classification.query;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.test.security.JaasExtension;

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
    assertThat(results).hasSize(5);
  }

  @Test
  void testGetSecondPageOfClassificationQueryWithOffset() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(5, 5);
    assertThat(results).hasSize(5);
  }

  @Test
  void testListOffsetAndLimitOutOfBounds() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();

    // both will be 0, working
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(-1, -3);
    assertThat(results).isEmpty();

    // limit will be 0
    results = classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(1, -3);
    assertThat(results).isEmpty();

    // offset will be 0
    results = classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(-1, 3);
    assertThat(results).hasSize(3);
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
    assertThat(results).hasSize(4);

    // Getting full page
    pageNumber = 3;
    pageSize = 4;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(4);

    // Getting last results on 1 big page
    pageNumber = 1;
    pageSize = 100;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(18);

    // Getting last results on multiple pages
    pageNumber = 2;
    pageSize = 10;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(8);
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
    assertThat(results).isEmpty();

    // Negative will be 0 = all results
    pageNumber = 1;
    pageSize = -1;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).isEmpty();

    // Negative page = first page
    pageNumber = -1;
    pageSize = 10;
    results =
        classificationService
            .createClassificationQuery()
            .domainIn("DOMAIN_A")
            .listPage(pageNumber, pageSize);
    assertThat(results).hasSize(10);
  }

  @Test
  void testCountOfClassificationsQuery() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    long count = classificationService.createClassificationQuery().domainIn("DOMAIN_A").count();
    assertThat(count).isEqualTo(18L);
  }
}
