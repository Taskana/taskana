package acceptance.classification.query;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;

@TaskanaIntegrationTest
class ClassificationQueryAccTest {

  @TaskanaInject ClassificationService classificationService;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void createClassificationsWithDifferentRoles() throws Exception {
    for (int i = 0; i < 5; i++) {
      defaultTestClassification().buildAndStoreAsSummary(classificationService);
    }
  }

  @Test
  void should_FindAllAccesibleClassifications_When_UserNotAuthenticated() {
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertThat(classificationSummaryList).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @TestTemplate
  void should_FindAllAccessibleClassifications_When_UserInRoleAdminOrBusinessadmin() {
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertThat(classificationSummaryList).hasSize(5);
  }
}
