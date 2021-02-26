package acceptance.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_1;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_2;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_3;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_4;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_5;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_6;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_7;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_8;

import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;

class ClassificationModelsCloneTest {

  @Test
  void should_CopyWithoutId_When_ClassificationSummaryClone() {
    Classification dummyClassificationForSummaryTest = new ClassificationImpl();
    dummyClassificationForSummaryTest.setApplicationEntryPoint("dummyEntryPoint");
    dummyClassificationForSummaryTest.setCategory("dummyCategory");
    dummyClassificationForSummaryTest.setCustomAttribute(CUSTOM_1, "dummyCustom1");
    dummyClassificationForSummaryTest.setCustomAttribute(CUSTOM_2, "dummyCustom2");
    dummyClassificationForSummaryTest.setCustomAttribute(CUSTOM_3, "dummyCustom3");
    dummyClassificationForSummaryTest.setCustomAttribute(CUSTOM_4, "dummyCustom4");
    dummyClassificationForSummaryTest.setCustomAttribute(CUSTOM_5, "dummyCustom5");
    dummyClassificationForSummaryTest.setCustomAttribute(CUSTOM_6, "dummyCustom6");
    dummyClassificationForSummaryTest.setCustomAttribute(CUSTOM_7, "dummyCustom7");
    dummyClassificationForSummaryTest.setCustomAttribute(CUSTOM_8, "dummyCustom8");
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
    dummyClassification.setCustomAttribute(CUSTOM_1, "dummyCustom1");
    dummyClassification.setCustomAttribute(CUSTOM_2, "dummyCustom2");
    dummyClassification.setCustomAttribute(CUSTOM_3, "dummyCustom3");
    dummyClassification.setCustomAttribute(CUSTOM_4, "dummyCustom4");
    dummyClassification.setCustomAttribute(CUSTOM_5, "dummyCustom5");
    dummyClassification.setCustomAttribute(CUSTOM_6, "dummyCustom6");
    dummyClassification.setCustomAttribute(CUSTOM_7, "dummyCustom7");
    dummyClassification.setCustomAttribute(CUSTOM_8, "dummyCustom8");
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
