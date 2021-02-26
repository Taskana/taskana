package acceptance.classification;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;

/** Acceptance test for classification queries and authorization. */
@ExtendWith(JaasExtension.class)
class ClassificationQueryAccTest extends AbstractAccTest {

  @Test
  void should_FindAllAccesibleClassifications_When_UserNotAuthenticated() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertThat(classificationSummaryList).hasSize(18);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @TestTemplate
  void should_FindAllAccessibleClassifications_When_UserInRoleAdminOrBusinessadmin() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertThat(classificationSummaryList).hasSize(18);
  }
}
