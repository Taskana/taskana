package pro.taskana.classification.api.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pro.taskana.classification.internal.models.ClassificationImpl;

class ClassificationModelsCloneTest {

  @Test
  void testCloneInClassificationSummary() {
    Classification dummyClassificationForSummaryTest = new ClassificationImpl();
    dummyClassificationForSummaryTest.setApplicationEntryPoint("dummyEntryPoint");
    dummyClassificationForSummaryTest.setCategory("dummyCategory");
    dummyClassificationForSummaryTest.setCustom1("dummyCustom1");
    dummyClassificationForSummaryTest.setCustom2("dummyCustom2");
    dummyClassificationForSummaryTest.setCustom3("dummyCustom3");
    dummyClassificationForSummaryTest.setCustom4("dummyCustom4");
    dummyClassificationForSummaryTest.setCustom5("dummyCustom5");
    dummyClassificationForSummaryTest.setCustom6("dummyCustom6");
    dummyClassificationForSummaryTest.setCustom7("dummyCustom7");
    dummyClassificationForSummaryTest.setCustom8("dummyCustom8");
    dummyClassificationForSummaryTest.setDescription("dummyDescription");
    dummyClassificationForSummaryTest.setIsValidInDomain(true);
    dummyClassificationForSummaryTest.setParentId("dummyParentId");
    dummyClassificationForSummaryTest.setServiceLevel("dummyServiceLevel");
    dummyClassificationForSummaryTest.setPriority(1);
    dummyClassificationForSummaryTest.setName("dummyName");
    dummyClassificationForSummaryTest.setParentKey("dummyParentKey");
    ClassificationSummary dummyClassificationSummary =
        dummyClassificationForSummaryTest.asSummary();
    ClassificationSummary dummyClassificationSummaryCloned = dummyClassificationSummary.copy();
    assertThat(dummyClassificationSummaryCloned).isEqualTo(dummyClassificationSummary);
    assertThat(dummyClassificationSummaryCloned).isNotSameAs(dummyClassificationSummary);
  }

  @Test
  void testCloneInClassification() {
    Classification dummyClassification = new ClassificationImpl();
    dummyClassification.setApplicationEntryPoint("dummyEntryPoint");
    dummyClassification.setCategory("dummyCategory");
    dummyClassification.setCustom1("dummyCustom1");
    dummyClassification.setCustom2("dummyCustom2");
    dummyClassification.setCustom3("dummyCustom3");
    dummyClassification.setCustom4("dummyCustom4");
    dummyClassification.setCustom5("dummyCustom5");
    dummyClassification.setCustom6("dummyCustom6");
    dummyClassification.setCustom7("dummyCustom7");
    dummyClassification.setCustom8("dummyCustom8");
    dummyClassification.setDescription("dummyDescription");
    dummyClassification.setIsValidInDomain(true);
    dummyClassification.setParentId("dummyParentId");
    dummyClassification.setServiceLevel("dummyServiceLevel");
    dummyClassification.setPriority(1);
    dummyClassification.setName("dummyName");
    dummyClassification.setParentKey("dummyParentKey");
    Classification dummyClassificationCloned = dummyClassification.copy();
    assertThat(dummyClassificationCloned).isEqualTo(dummyClassification);
    assertThat(dummyClassificationCloned).isNotSameAs(dummyClassification);
  }
}
