package acceptance.security;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for classification queries and authorization.
 *
 * @author bbr
 */
@ExtendWith(JaasExtension.class)
class ClassificationQueryAccTest extends AbstractAccTest {

  ClassificationQueryAccTest() {
    super();
  }

  @Test
  void testFindClassificationsByDomainUnauthenticated() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertThat(classificationSummaryList).isNotNull();
    assertThat(classificationSummaryList).hasSize(17);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void testFindClassificationsByDomainBusinessAdmin() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertThat(classificationSummaryList).isNotNull();
    assertThat(classificationSummaryList).hasSize(17);
  }

  @WithAccessId(user = "admin")
  @Test
  void testFindClassificationsByDomainAdmin() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertThat(classificationSummaryList).isNotNull();
    assertThat(classificationSummaryList).hasSize(17);
  }
}
