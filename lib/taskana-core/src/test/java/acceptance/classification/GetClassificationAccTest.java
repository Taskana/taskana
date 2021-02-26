package acceptance.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_1;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_2;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_3;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_4;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_5;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_6;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_7;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_8;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;

/** Acceptance test for all "get classification" scenarios. */
class GetClassificationAccTest extends AbstractAccTest {

  private ClassificationService classificationService;

  GetClassificationAccTest() {
    super();
    classificationService = taskanaEngine.getClassificationService();
  }

  @Test
  void testFindAllClassifications() {
    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().list();
    assertThat(classificationSummaryList).isNotNull();
  }

  @Test
  void testGetOneClassificationByKeyAndDomain() throws Exception {
    Classification classification = classificationService.getClassification("T6310", "DOMAIN_A");
    assertThat(classification).isNotNull();
    assertThat(classification.getId()).isEqualTo("CLI:100000000000000000000000000000000011");
    assertThat(classification.getParentId()).isEmpty();
    assertThat(classification.getCategory()).isEqualTo("AUTOMATIC");
    assertThat(classification.getType()).isEqualTo("TASK");
    assertThat(classification.getIsValidInDomain()).isTrue();
    assertThat(classification.getName()).isEqualTo("T-GUK Honorarrechnung erstellen");
    assertThat(classification.getPriority()).isEqualTo(2);
    assertThat(classification.getServiceLevel()).isEqualTo("P2D");
    assertThat(classification.getApplicationEntryPoint()).isEqualTo("point0815");
    assertThat(classification.getCustomAttribute(CUSTOM_1)).isEqualTo("VNR");
    assertThat(classification.getCustomAttribute(CUSTOM_2)).isEqualTo("custom2");
    assertThat(classification.getCustomAttribute(CUSTOM_3)).isEqualTo("custom3");
    assertThat(classification.getCustomAttribute(CUSTOM_4)).isEqualTo("custom4");
    assertThat(classification.getCustomAttribute(CUSTOM_5)).isEqualTo("custom5");
    assertThat(classification.getCustomAttribute(CUSTOM_6)).isEqualTo("custom6");
    assertThat(classification.getCustomAttribute(CUSTOM_7)).isEqualTo("custom7");
    assertThat(classification.getCustomAttribute(CUSTOM_8)).isEqualTo("custom8");
  }

  @Test
  void testGetOneClassificationById() throws Exception {
    Classification classification =
        classificationService.getClassification("CLI:100000000000000000000000000000000011");
    assertThat(classification).isNotNull();
    assertThat(classification.getKey()).isEqualTo("T6310");
    assertThat(classification.getParentId()).isEmpty();
    assertThat(classification.getCategory()).isEqualTo("AUTOMATIC");
    assertThat(classification.getType()).isEqualTo("TASK");
    assertThat(classification.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(classification.getIsValidInDomain()).isTrue();
    assertThat(classification.getName()).isEqualTo("T-GUK Honorarrechnung erstellen");
    assertThat(classification.getPriority()).isEqualTo(2);
    assertThat(classification.getServiceLevel()).isEqualTo("P2D");
    assertThat(classification.getApplicationEntryPoint()).isEqualTo("point0815");
    assertThat(classification.getCustomAttribute(CUSTOM_1)).isEqualTo("VNR");
    assertThat(classification.getCustomAttribute(CUSTOM_2)).isEqualTo("custom2");
    assertThat(classification.getCustomAttribute(CUSTOM_3)).isEqualTo("custom3");
    assertThat(classification.getCustomAttribute(CUSTOM_4)).isEqualTo("custom4");
    assertThat(classification.getCustomAttribute(CUSTOM_5)).isEqualTo("custom5");
    assertThat(classification.getCustomAttribute(CUSTOM_6)).isEqualTo("custom6");
    assertThat(classification.getCustomAttribute(CUSTOM_7)).isEqualTo("custom7");
    assertThat(classification.getCustomAttribute(CUSTOM_8)).isEqualTo("custom8");
  }

  @Test
  void testGetClassificationWithSpecialCharacter() throws Exception {
    Classification classification =
        classificationService.getClassification("CLI:100000000000000000000000000000000009");
    assertThat(classification.getName()).isEqualTo("Zustimmungserklärung");
  }

  @Test
  void testGetClassificationAsSummary() throws Exception {
    ClassificationSummary classification =
        classificationService
            .getClassification("CLI:100000000000000000000000000000000011")
            .asSummary();
    assertThat(classification).isNotNull();
    assertThat(classification.getKey()).isEqualTo("T6310");
    assertThat(classification.getParentId()).isEmpty();
    assertThat(classification.getCategory()).isEqualTo("AUTOMATIC");
    assertThat(classification.getType()).isEqualTo("TASK");
    assertThat(classification.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(classification.getName()).isEqualTo("T-GUK Honorarrechnung erstellen");
    //    assertThat(classification.getDescription())
    //        .isEqualTo("Generali Unterstützungskasse Honorar wird fällig");
    assertThat(classification.getPriority()).isEqualTo(2);
    assertThat(classification.getServiceLevel()).isEqualTo("P2D");
  }

  @Test
  void testGetOneClassificationByIdFails() {
    ThrowingCallable call =
        () -> classificationService.getClassification("CLI:100000000470000000000000000000000011");
    assertThatThrownBy(call).isInstanceOf(ClassificationNotFoundException.class);
  }

  @Test
  void testGetClassificationByNullKeyFails() {
    ThrowingCallable call = () -> classificationService.getClassification(null, "");
    assertThatThrownBy(call).isInstanceOf(ClassificationNotFoundException.class);
  }

  @Test
  void testGetClassificationByInvalidKeyAndDomain() {
    ThrowingCallable call =
        () -> classificationService.getClassification("Key0815", "NOT_EXISTING");
    assertThatThrownBy(call).isInstanceOf(ClassificationNotFoundException.class);
  }

  @Test
  void testGetOneClassificationForDomainAndGetClassificationFromMasterDomain() throws Exception {
    Classification classification = classificationService.getClassification("L10000", "DOMAIN_B");
    assertThat(classification).isNotNull();
    assertThat(classification.getDomain()).isEmpty();
    assertThat(classification.getPriority()).isEqualTo(999L);
  }

  @Test
  void testGetOneClassificationForMasterDomain() throws Exception {
    Classification classification = classificationService.getClassification("L10000", "");
    assertThat(classification).isNotNull();
    assertThat(classification.getDomain()).isEmpty();
    assertThat(classification.getPriority()).isEqualTo(999L);
  }
}
