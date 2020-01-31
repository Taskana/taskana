package acceptance.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.ClassificationSummary;
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

    assertNotNull(classificationSummaryList);
    assertEquals(17, classificationSummaryList.size());
  }

  @WithAccessId(userName = "businessadmin")
  @Test
  void testFindClassificationsByDomainBusinessAdmin() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertNotNull(classificationSummaryList);
    assertEquals(17, classificationSummaryList.size());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testFindClassificationsByDomainAdmin() {
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().domainIn("DOMAIN_A").list();

    assertNotNull(classificationSummaryList);
    assertEquals(17, classificationSummaryList.size());
  }
}
