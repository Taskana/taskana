package acceptance.classification.query;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;

@TaskanaIntegrationTest
public class ClassificationQueryOffsetAndPaginationAccTest {

  @TaskanaInject ClassificationService classificationService;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void createClassifications() throws Exception {
    for (int i = 0; i <= 18; i++) {
      defaultTestClassification().buildAndStoreAsSummary(classificationService);
    }
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetFirstFiveClassifications_When_OffsetIsZeroAndLimitIsFive() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(0, 5);
    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetLastFourClassifications_When_OffsetIsFifteenAndLimitIsFive() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(15, 5);
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetZeroClassifications_When_OffsetIsNegativeAndLimitIsNegative() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(-1, -1);
    assertThat(results).hasSize(0);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetZeroClassifications_When_LimitIsNegative() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(0, -1);
    assertThat(results).hasSize(0);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetZeroClassifications_When_OffsetAndLimitAreZero() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(0, 0);
    assertThat(results).hasSize(0);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetOneClassifications_When_OffsetIsNegativeAndLimitIsPositive() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list(-1, 1);
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetFirstFiveClassifications_When_UsingPaginationWithPageOneAndSizeFive() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").listPage(1, 5);
    assertThat(results).hasSize(5);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetLastFourClassifications_When_UsingPaginationWithPageFourAndPageSizeFive() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").listPage(4, 5);
    assertThat(results).hasSize(4);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetAllClassifications_When_UsingPaginationWithPageOneAndPageSizeTwenty() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").listPage(1, 20);
    assertThat(results).hasSize(19);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetZeroClassifications_When_UsingPaginationWithSizeZero() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").listPage(1, 0);
    assertThat(results).hasSize(0);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetZeroClassifications_When_UsingPaginationWithNegativeSize() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").listPage(1, -1);
    assertThat(results).hasSize(0);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetOneClassification_When_UsingPaginationWithPageZeroButSizeOne() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").listPage(0, 1);
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetOneClassification_When_UsingPaginationWithNegativePageButSizeOne() {
    List<ClassificationSummary> results =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").listPage(-1, 1);
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_GetNumberOfAllClassifications_When_CountMethodIsCalled() {
    long count = classificationService.createClassificationQuery().domainIn("DOMAIN_A").count();
    assertThat(count).isEqualTo(19L);
  }
}
