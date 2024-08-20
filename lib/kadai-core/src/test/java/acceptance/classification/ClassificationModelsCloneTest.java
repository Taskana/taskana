package acceptance.classification;

import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_1;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_2;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_3;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_4;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_5;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_6;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_7;
import static io.kadai.classification.api.ClassificationCustomField.CUSTOM_8;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.classification.api.models.Classification;
import io.kadai.classification.internal.models.ClassificationImpl;
import io.kadai.classification.internal.models.ClassificationSummaryImpl;
import org.junit.jupiter.api.Test;

class ClassificationModelsCloneTest {

  @Test
  void should_CopyWithoutId_When_ClassificationSummaryClone() {
    Classification dummyClassificationForSummaryTest = new ClassificationImpl();
    dummyClassificationForSummaryTest.setApplicationEntryPoint("dummyEntryPoint");
    dummyClassificationForSummaryTest.setCategory("dummyCategory");
    dummyClassificationForSummaryTest.setCustomField(CUSTOM_1, "dummyCustom1");
    dummyClassificationForSummaryTest.setCustomField(CUSTOM_2, "dummyCustom2");
    dummyClassificationForSummaryTest.setCustomField(CUSTOM_3, "dummyCustom3");
    dummyClassificationForSummaryTest.setCustomField(CUSTOM_4, "dummyCustom4");
    dummyClassificationForSummaryTest.setCustomField(CUSTOM_5, "dummyCustom5");
    dummyClassificationForSummaryTest.setCustomField(CUSTOM_6, "dummyCustom6");
    dummyClassificationForSummaryTest.setCustomField(CUSTOM_7, "dummyCustom7");
    dummyClassificationForSummaryTest.setCustomField(CUSTOM_8, "dummyCustom8");
    dummyClassificationForSummaryTest.setDescription("dummyDescription");
    dummyClassificationForSummaryTest.setIsValidInDomain(true);
    dummyClassificationForSummaryTest.setParentId("dummyParentId");
    dummyClassificationForSummaryTest.setServiceLevel("dummyServiceLevel");
    dummyClassificationForSummaryTest.setPriority(1);
    dummyClassificationForSummaryTest.setName("dummyName");
    dummyClassificationForSummaryTest.setParentKey("dummyParentKey");
    ClassificationSummaryImpl dummyClassificationSummary =
        (ClassificationSummaryImpl) dummyClassificationForSummaryTest.asSummary();
    dummyClassificationSummary.setId("dummyId");

    ClassificationSummaryImpl dummyClassificationSummaryCloned = dummyClassificationSummary.copy();

    assertThat(dummyClassificationSummaryCloned).isNotEqualTo(dummyClassificationSummary);
    dummyClassificationSummaryCloned.setId(dummyClassificationSummary.getId());
    assertThat(dummyClassificationSummaryCloned)
        .isEqualTo(dummyClassificationSummary)
        .isNotSameAs(dummyClassificationSummary);
  }

  @Test
  void should_CopyWithoutId_When_ClassificationClone() {
    ClassificationImpl dummyClassification = new ClassificationImpl();
    dummyClassification.setId("dummyId");
    dummyClassification.setApplicationEntryPoint("dummyEntryPoint");
    dummyClassification.setCategory("dummyCategory");
    dummyClassification.setCustomField(CUSTOM_1, "dummyCustom1");
    dummyClassification.setCustomField(CUSTOM_2, "dummyCustom2");
    dummyClassification.setCustomField(CUSTOM_3, "dummyCustom3");
    dummyClassification.setCustomField(CUSTOM_4, "dummyCustom4");
    dummyClassification.setCustomField(CUSTOM_5, "dummyCustom5");
    dummyClassification.setCustomField(CUSTOM_6, "dummyCustom6");
    dummyClassification.setCustomField(CUSTOM_7, "dummyCustom7");
    dummyClassification.setCustomField(CUSTOM_8, "dummyCustom8");
    dummyClassification.setDescription("dummyDescription");
    dummyClassification.setIsValidInDomain(true);
    dummyClassification.setParentId("dummyParentId");
    dummyClassification.setServiceLevel("dummyServiceLevel");
    dummyClassification.setPriority(1);
    dummyClassification.setName("dummyName");
    dummyClassification.setParentKey("dummyParentKey");

    ClassificationImpl dummyClassificationCloned =
        dummyClassification.copy(dummyClassification.getKey());

    assertThat(dummyClassificationCloned).isNotEqualTo(dummyClassification);
    dummyClassificationCloned.setId(dummyClassification.getId());
    assertThat(dummyClassificationCloned)
        .isEqualTo(dummyClassification)
        .isNotSameAs(dummyClassification);
  }
}
